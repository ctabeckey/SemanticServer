package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.exceptions.GenericContextInitializationException;
import com.paypal.credit.context.exceptions.InvalidArtifactSyntaxException;
import com.paypal.credit.context.exceptions.InvalidElementTypeException;
import com.paypal.credit.context.exceptions.UnknownCollectionTypeException;
import com.paypal.credit.context.xml.ArtifactType;
import com.paypal.credit.context.xml.BeanType;
import com.paypal.credit.context.xml.BeansType;
import com.paypal.credit.context.xml.ListType;
import com.paypal.credit.context.xml.ReferenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.lang.reflect.TypeVariable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 */
public class ContextFactory {
    /** */
    private final static Logger LOGGER = LoggerFactory.getLogger(ContextFactory.class);

    /** */
    private Set<ArtifactHolder> artifacts = new HashSet<>();
    /** */
    private Map<String, AbstractBeanReference> contextObjectsNameMap = new HashMap<>();

    /** JAXB Context is created on demand */
    private JAXBContext jaxbContext = null;

    /** */
    private BeansType beansType;

    /** */
    private ClassLoader parentClassLoader;

    /**
     *
     */
    public ContextFactory() {
    }

    /**
     *
     * @param beansType
     * @return
     */
    public ContextFactory with(BeansType beansType) {
        this.beansType = beansType;

        return this;
    }

    /**
     *
     * @param identifier
     * @param bean
     */
    public synchronized ContextFactory withBean(final String identifier, final AbstractBeanReference bean) {
        String id = identifier == null ?
                UUID.randomUUID().toString() :
                identifier;
        contextObjectsNameMap.put(id, bean);

        return this;
    }

    /**
     *
     * @param externalBeanDefinition
     * @return
     * @throws ContextInitializationException
     */
    public ContextFactory withExternalBeanDefinition(final ExternalBeanDefinition<?> externalBeanDefinition)
            throws ContextInitializationException {
        if (externalBeanDefinition != null) {
            withBean(externalBeanDefinition.getIdentifier(), new ResolvedBeanReference(externalBeanDefinition.getBeanInstance()));
        }
        return this;
    }

    /**
     *
     * @param externalBeanDefinitions
     * @return
     * @throws ContextInitializationException
     */
    public ContextFactory withExternalBeanDefinitions(final ExternalBeanDefinition<?>[] externalBeanDefinitions)
            throws ContextInitializationException {
        if (externalBeanDefinitions != null) {
            for (ExternalBeanDefinition<?> externalBeanDefinition : externalBeanDefinitions) {
                withExternalBeanDefinition(externalBeanDefinition);
            }
        }
        return this;
    }

    /**
     *
     * @param contextDefinition
     * @return
     * @throws JAXBException
     * @throws ContextInitializationException
     * @throws FileNotFoundException
     */
    public ContextFactory withContextDefinition(final File contextDefinition)
            throws JAXBException, ContextInitializationException, FileNotFoundException {
        FileInputStream fiS = new FileInputStream(contextDefinition);
        return withContextDefinition(fiS);
    }

    /**
     *
     * @param contextDefinition
     * @return
     * @throws JAXBException
     * @throws ContextInitializationException
     * @throws IOException
     */
    public ContextFactory withContextDefinition(final URL contextDefinition)
            throws JAXBException, ContextInitializationException, IOException {
        InputStream urlIS = contextDefinition.openStream();
        return withContextDefinition(urlIS);
    }

    /**
     *
     * @param inputStream
     * @return
     * @throws JAXBException
     * @throws ContextInitializationException
     */
    public ContextFactory withContextDefinition(final InputStream inputStream)
            throws JAXBException, ContextInitializationException {
        JAXBContext jaxbContext = getJaxbContext();
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        JAXBElement<BeansType> ctx = (JAXBElement<BeansType>) unmarshaller.unmarshal(inputStream);
        return with(ctx.getValue());
    }

    /**
     *
     * @param classLoader
     * @return
     */
    public ContextFactory withClassLoader(final ClassLoader classLoader) {
        this.parentClassLoader = classLoader;
        return this;
    }

    /**
     *
     * @return
     * @throws JAXBException
     */
    private final synchronized JAXBContext getJaxbContext()
            throws JAXBException {
        if (jaxbContext == null) {
            jaxbContext = JAXBContext.newInstance("com.paypal.credit.context.xml");
        }
        return jaxbContext;
    }

    /**
     *
     * @return
     * @throws ContextInitializationException
     */
    public Context build()
            throws ContextInitializationException {
        extractArtifactReferences();
        //loadReferencedArtifacts();

        // create the top-level beansType (those directly under the 'beansType' element)
        // AbstractBeanReference will create the dependencies under each top level
        // bean
        for (BeanType beanType : beansType.getBean()) {
            createBeanReference(beanType);
        }

        return new Context(this.contextObjectsNameMap);
    }

    /**
     *
     * @throws InvalidArtifactSyntaxException
     */
    private void extractArtifactReferences() throws InvalidArtifactSyntaxException {
        ClassLoader classLoader = this.parentClassLoader == null ? this.getClass().getClassLoader() : this.parentClassLoader;

        // a List of the artifact from which the beans may be loaded
        for (ArtifactType artifactType : beansType.getArtifact()) {
            if (artifactType.getResource() != null) {
                try {
                    ArtifactHolder holder = new ArtifactHolder(artifactType.getId(), new URI(artifactType.getResource()), classLoader);
                    // note that the holder will not be added if it is a duplicate
                    artifacts.add(holder);
                } catch (MalformedURLException | URISyntaxException e) {
                    throw new InvalidArtifactSyntaxException(artifactType, e);
                }
            }
        }
    }

    /**
     *
     * @param artifactReference
     * @return
     */
    ArtifactHolder getArtifactType(final String artifactReference) {
        for (ArtifactHolder holder : artifacts) {
            if (holder.identifier.equals(artifactReference)) {
                return holder;
            }
        }

        return null;
    }

    /**
     *
     * @param beanType
     * @return
     * @throws ContextInitializationException
     */
    AbstractBeanReference createBeanReference(final BeanType beanType)
            throws ContextInitializationException {
        AbstractBeanReference beanReference = null;

        switch(beanType.getScope()) {
            case PROTOTYPE: {
                beanReference = new PrototypeBeanFactoryReference(this, beanType);
                break;
            }
            case SINGLETON: {
                beanReference = new SingletonBeanFactoryReference(this, beanType);
                break;
            }
        }

        if (beanReference == null) {
            throw new GenericContextInitializationException(
                    String.format("Unrecognized scope specifier (%s) in context definition for bean %s", beanType.getScope(), beanType.getId())
            );
        }

        // add the bean and an ID (perhaps synthetic) to the context Set
        String id = beanType.getId() == null ? UUID.randomUUID().toString() : beanType.getId();
        contextObjectsNameMap.put(id, beanReference);

        return beanReference;
    }

    /**
     *
     * @param reference
     * @return
     * @throws ContextInitializationException
     */
    AbstractBeanReference createBeanReference(final ReferenceType reference)
            throws ContextInitializationException {
        SimpleBeanReference beanReference = new SimpleBeanReference(this, reference);
        return beanReference;
    }

    /**
     *
     * @param identifier
     * @return
     */
    public AbstractBeanReference findBeanReference(final String identifier) {
        return this.contextObjectsNameMap.get(identifier);
    }

    /**
     *
     *
     * @param parameterType
     * @param list
     * @return
     */
    Object createListElementArguments(final Class<?> parameterType, final ListType list)
            throws ContextInitializationException {
        // try to determine the type of the elements from the parameterType
        // for arrays this is dependable, for Collection it depends on compiler options
        Class<?> componentType = extractElementType(parameterType);

        List result = new ArrayList<>();

        for (Object argumentType : list.getBeanOrValueOrList()) {
            Object argValue = null;

            if (argumentType instanceof BeanType) {
                AbstractBeanReference dependency = createBeanReference((BeanType) argumentType);
                Object beanInstance = dependency.getBeanInstance();
                argValue = beanInstance;

            } else if (argumentType instanceof ListType) {
                Object listElements = createListElementArguments(String.class, (ListType) argumentType);
                argValue = listElements;

            } else if (argumentType instanceof ReferenceType) {
                AbstractBeanReference ref = createBeanReference((ReferenceType) argumentType);
                argValue = ref.getBeanInstance();

            } else {        // argumentType is String (static value)
                argValue = ContextUtility.createInstanceFromStringValue(componentType, argumentType.toString());
            }

            if (!componentType.isInstance(argValue)) {
                throw new InvalidElementTypeException(componentType, argValue);
            }

            result.add(argValue);
        }

        if (parameterType.isArray()) {
            return ContextUtility.createTypedArray(componentType, result);
        } else {
            return result;
        }
    }

    /**
     * Try to determine the type of the elements from the parameterType
     * For arrays this is dependable, for Collection it depends on compiler options.
     *
     * @param parameterType
     * @return
     * @throws UnknownCollectionTypeException
     */
    private Class<?> extractElementType(final Class<?> parameterType) throws UnknownCollectionTypeException {
        Class<?> componentType = Object.class;

        if (parameterType.isArray()) {
            componentType = parameterType.getComponentType();
        } else if (Collection.class.isInstance(parameterType)) {
            TypeVariable<? extends Class<?>>[] typeParameters = parameterType.getTypeParameters();
            if (typeParameters == null || typeParameters.length == 0) {
                componentType = Object.class;
            } else if (typeParameters.length > 1){
                throw new UnknownCollectionTypeException(parameterType);
            } else {
                componentType = typeParameters[0].getGenericDeclaration();
            }
        }
        return componentType;
    }

    /**
     * Holds references to late-loaded artifacts.
     */
    static class ArtifactHolder {
        private final String identifier;
        private final ClassLoader parentClassLoader;
        private URLClassLoader classLoader;
        private final URI resourceIdentifier;
        private boolean loaded = false;

        private final ReentrantLock loadedLock = new ReentrantLock();
        private final Condition notLoaded  = loadedLock.newCondition();

        /**
         *
         * @param identifier
         * @param resourceIdentifier
         * @param parentClassLoader
         * @throws MalformedURLException
         */
        public ArtifactHolder(final String identifier, final URI resourceIdentifier, final ClassLoader parentClassLoader)
                throws MalformedURLException {
            this.identifier = identifier;
            this.resourceIdentifier = resourceIdentifier;
            this.parentClassLoader = parentClassLoader;

            URL artifactLocation = this.resourceIdentifier.toURL();

            if ("http".equals(artifactLocation.getProtocol()) || "https".equals(artifactLocation.getProtocol())) {
                String tempPath = System.getenv("TMPDIR");
                File tempDirectory = new File(tempPath);
                System.out.println("Application "
                        + (tempDirectory.canRead() ? "CAN read" : "CANNOT read")
                        + " and "
                        + (tempDirectory.canWrite() ? "CAN write" : "CANNOT write")
                        + " to "
                        + tempPath
                );
            }

            this.classLoader = URLClassLoader.newInstance(new URL[]{artifactLocation}); // , parentClassLoader, null
        }

        private void dumpManifest(PrintStream out) {
            try (InputStreamReader isr = new InputStreamReader(
                    this.classLoader.getResourceAsStream("META-INF/MANIFEST.MF"))) {
                if (isr != null) {
                    try (LineNumberReader reader = new LineNumberReader(isr)) {

                        String line = null;
                        for (line = reader.readLine(); line != null; line = reader.readLine()) {
                            out.println(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException x) {
                x.printStackTrace();
            }

        }

        /**
         *
         * @return
         */
        public URLClassLoader getClassLoader() {
            return classLoader;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ArtifactHolder that = (ArtifactHolder) o;
            return Objects.equals(identifier, that.identifier) &&
                    Objects.equals(resourceIdentifier, that.resourceIdentifier);
        }

        @Override
        public int hashCode() {
            return Objects.hash(identifier, resourceIdentifier);
        }
    }
}
