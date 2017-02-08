package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.exceptions.GenericContextInitializationException;
import com.paypal.credit.context.exceptions.InvalidArtifactSyntaxException;
import com.paypal.credit.context.exceptions.SparseArgumentListDetectedException;
import com.paypal.credit.context.xml.ArtifactType;
import com.paypal.credit.context.xml.BeanType;
import com.paypal.credit.context.xml.Beans;
import com.paypal.credit.context.xml.ConstructorArgType;
import com.paypal.credit.context.xml.ScopeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

/**
 *
 */
public class ContextFactory {
    /** */
    private final static Logger LOGGER = LoggerFactory.getLogger(ContextFactory.class);

    /** JAXB Context is created on demand */
    private JAXBContext jaxbContext = null;

    /**
     * The class loader to use for loading the context beans from
     */
    private ClassLoader classLoader;

    /**
     * The parent of the context to create.
     * A parent is delegated to if a bean cannot be found in the
     * current context.
     */
    private Context parent;

    // ========================================================================================
    // The components of the ContextFactory that populate the Context
    // ========================================================================================

    /** Contains all of the artifact references */
    private Set<ArtifactType> artifacts = new HashSet<>();

    /**
     * Contains BeanType instances, usually read from an XML resource.
     * Note that this collection MUST be kept in order from the source document
     * because it is up to the source document to define references in order
     * that avoids the need for forward reference resolution.
     */
    private List<BeanType> beanTypes = new ArrayList<>();

    /** Contains pre-resolved beans that are to be added to the context when it is built */
    private Set<ExternalBeanDefinition> externalBeanDefinitions = new HashSet<>();

    /**
     *
     */
    public ContextFactory() {
    }

    /**
     *
     * @return
     * @throws JAXBException
     */
    private final JAXBContext getJaxbContext()
            throws JAXBException {
        if (jaxbContext == null) {
            jaxbContext = JAXBContext.newInstance("com.paypal.credit.context.xml");
        }
        return jaxbContext;
    }

    /**
     *
     * @param classLoader
     * @return
     */
    public ContextFactory withClassLoader(final ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    /**
     *
     * @param beansType
     * @return
     */
    public ContextFactory with(final Beans beansType) {
        for (BeanType beanType : beansType.getBean()) {
            // default the scope to prototype
            if (beanType.getScope() == null) {
                beanType.setScope(ScopeType.PROTOTYPE);
            }

            with(beanType);
        }

        for (ArtifactType artifactType : beansType.getArtifact()) {
            with(artifactType);
        }

        return this;
    }

    public ContextFactory with(final BeanType beanType) {
        if (beanType != null) {
            if (! this.beanTypes.contains(beanType)) {
                this.beanTypes.add(beanType);
            }
        }

        return this;
    }

    public ContextFactory with(final ArtifactType artifact) {
        if (artifact != null) {
            this.artifacts.add(artifact);
        }

        return this;
    }

    /**
     *
     * @param identifier
     * @param bean
     * @return
     * @throws ContextInitializationException
     */
    public ContextFactory withExternalBeanDefinition(final String identifier, final Object bean)
            throws ContextInitializationException {
        if (bean != null) {
            withExternalBeanDefinition(new ExternalBeanDefinition(identifier, bean));
        }
        return this;
    }

    /**
     *
     * @param xBeanDef
     * @return
     */
    private ContextFactory withExternalBeanDefinition(ExternalBeanDefinition xBeanDef) {
        if (xBeanDef != null) {
            if (xBeanDef.getIdentifier() == null) {
                xBeanDef = new ExternalBeanDefinition(UUID.randomUUID().toString(), xBeanDef.beanInstance);
            }
            this.externalBeanDefinitions.add(xBeanDef);
        }

        return this;
    }

    /**
     * Set the context to delegate to when an ID cannot be found in the current
     * context.
     *
     * @param parent
     * @return
     * @throws ContextInitializationException
     */
    public ContextFactory withParentContext(final Context parent)
            throws ContextInitializationException {
        this.parent = parent;
        return this;
    }


    // ========================================================================================
    // Methods to read the context from an XML resource
    // ========================================================================================

    /**
     *
     * @param contextDefinition
     * @return
     * @throws JAXBException
     * @throws ContextInitializationException
     * @throws FileNotFoundException
     */
    public ContextFactory with(final File contextDefinition)
            throws JAXBException, ContextInitializationException, FileNotFoundException {
        FileInputStream fiS = new FileInputStream(contextDefinition);
        return with(fiS);
    }

    /**
     *
     * @param contextDefinition
     * @return
     * @throws JAXBException
     * @throws ContextInitializationException
     * @throws IOException
     */
    public ContextFactory with(final URL contextDefinition)
            throws JAXBException, ContextInitializationException, IOException {
        InputStream urlIS = contextDefinition.openStream();
        return with(urlIS);
    }

    /**
     *
     * @param inputStream
     * @return
     * @throws JAXBException
     * @throws ContextInitializationException
     */
    public ContextFactory with(final InputStream inputStream)
            throws JAXBException, ContextInitializationException {
        JAXBContext jaxbContext = getJaxbContext();
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        Beans ctx = (Beans) unmarshaller.unmarshal(inputStream);
        return with(ctx);
    }

    // ========================================================================================
    // Build method and its helpers
    // ========================================================================================

    /**
     *
     * @return
     * @throws ContextInitializationException
     */
    public Context build()
            throws ContextInitializationException {
        Context ctx = new Context(this.parent);
        PropertyFactory propertyFactory = new PropertyFactory(ctx);

        Set<ArtifactHolder> artifactHolders = extractArtifactReferences();
        ctx.setArtifacts(artifactHolders);

        // create the top-level beansType (those directly under the 'beansType' element)
        Map<String,AbstractReferencableProperty> contextObjectsNameMap = new HashMap<>();

        // add the externally defined bean definitions first
        if (this.externalBeanDefinitions != null) {
            for (ExternalBeanDefinition xBeanDef : this.externalBeanDefinitions) {
                AbstractReferencableProperty prop = propertyFactory.create(xBeanDef);
                contextObjectsNameMap.put(prop.getIdentifier(), prop);
            }
        }

        // for each of the top level beans, create a AbstractBeanInstanceFactory
        for (BeanType beanType : this.beanTypes) {
            // every bean has an identifier
            String id = beanType.getId();

            // default the scope to prototype
            if (beanType.getScope() == null) {
                beanType.setScope(ScopeType.PROTOTYPE);
            }

            AbstractBeanInstanceFactory beanReference = propertyFactory.createBeanInstanceFactory(beanType);
            // add the bean and an ID (perhaps synthetic) to the context Set
            populateReferencablePropertyMap(beanReference, contextObjectsNameMap);
        }

        ctx.setContextObjectsMap(contextObjectsNameMap);
        return ctx;
    }

    /**
     * A recursive
     * @param beanReference
     * @param contextObjectsNameMap
     */
    private void populateReferencablePropertyMap(AbstractReferencableProperty beanReference, Map<String, AbstractReferencableProperty> contextObjectsNameMap) {
        contextObjectsNameMap.put(beanReference.getIdentifier(), beanReference);

        if (beanReference instanceof AbstractBeanInstanceFactory) {
            List<AbstractProperty> constructorParameters =
                    ((AbstractBeanInstanceFactory)beanReference).getConstructorParameterProperties();

            if (constructorParameters != null) {
                for (AbstractProperty constructorParameter : constructorParameters) {
                    if (constructorParameter instanceof AbstractReferencableProperty) {
                        populateReferencablePropertyMap((AbstractReferencableProperty) constructorParameter, contextObjectsNameMap);
                    }
                }
            }
        }
    }

    /**
     * Extract the Artifact references in the XML elements into a Set of
     * ArtifactHolder instances.
     *
     * @throws InvalidArtifactSyntaxException
     */
    private Set<ArtifactHolder> extractArtifactReferences() throws InvalidArtifactSyntaxException {
        Set<ArtifactHolder> artifactHolders = new HashSet<>(this.artifacts.size());

        // if no ClassLoader was specified then use the ClassLoader of this class
        ClassLoader classLoader = this.classLoader == null ?
                this.getClass().getClassLoader() :
                this.classLoader;

        // loop through each of the artifacts in the BeansType (root element)
        for (ArtifactType artifactType : this.artifacts) {
            if (artifactType.getResource() != null) {
                try {
                    // an ArtifactHolder will load the Artifact
                    ArtifactHolder holder = new ArtifactHolder(artifactType.getId(), new URI(artifactType.getResource()), classLoader);
                    // note that the holder will not be added if it is a duplicate
                    artifactHolders.add(holder);
                } catch (MalformedURLException | URISyntaxException e) {
                    throw new InvalidArtifactSyntaxException(artifactType, e);
                }
            }
        }

        return artifactHolders;
    }

    /**
     * Where a Bean must be added to the context, while its lifecycle is outside of the control of the Context,
     * use this class as a holder and add it using withExternalBeanDefinition().
     */
    public static class ExternalBeanDefinition<T> {
        private final String identifier;
        private final T beanInstance;

        public ExternalBeanDefinition(final String identifier, final T beanInstance) {
            this.identifier = identifier;
            this.beanInstance = beanInstance;
        }

        public String getIdentifier() {
            return identifier;
        }

        public T getBeanInstance() {
            return beanInstance;
        }
    }

}
