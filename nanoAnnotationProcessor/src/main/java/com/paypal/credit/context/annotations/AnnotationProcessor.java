package com.paypal.credit.context.annotations;

import com.paypal.credit.context.xml.BeanType;
import com.paypal.credit.context.xml.Beans;
import com.paypal.credit.context.xml.ConstructorArgType;
import com.paypal.credit.context.xml.ReferenceType;
import com.paypal.credit.context.xml.ScopeType;

import javax.annotation.processing.Completion;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.AbstractElementVisitor8;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by cbeckey on 2/6/17.
 */
public class AnnotationProcessor implements Processor {
    private final static Set<String> supportedAnnotations;
    private final static Set<String> supportedOptions;
    public static final String DEFAULT_CONTEXT_XML = "context.xml";

    static {
        supportedAnnotations = new HashSet<>();

        supportedAnnotations.add(NanoBean.class.getCanonicalName());
        supportedAnnotations.add(NanoSingleton.class.getCanonicalName());

        supportedOptions = new HashSet<>();
        supportedOptions.add("context");
    }

    @Override
    public Set<String> getSupportedOptions() {
        return supportedOptions;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return supportedAnnotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    private ProcessingEnvironment processingEnv;
    private String contextFileName;

    /**
     *
     * @param processingEnv - environment for facilities the tool framework provides to the processor
     */
    @Override
    public void init(final ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        contextFileName = this.processingEnv.getOptions().get("context");
        if (contextFileName == null || contextFileName.length() == 0) {
            contextFileName = DEFAULT_CONTEXT_XML;
        }
    }

    /**
     * Processes a set of annotation types on type elements originating from the prior round and returns
     * whether or not these annotations are claimed by this processor. If true is returned, the annotations are
     * claimed and subsequent processors will not be asked to process them; if false is returned, the annotations
     * are unclaimed and subsequent processors may be asked to process them. A processor may always return the same
     * boolean value or may vary the result based on chosen criteria.
     *
     * @param annotations - the annotation types requested to be processed
     * @param roundEnv - environment for information about the current and prior round
     * @return - whether or not the set of annotations are claimed by this processor. always return
     *      false, we do not claim any annotations
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        List<BeanType> beans = new ArrayList<>();

        // if this is the last round then do not re-run the context file craetion process
        // it has already completed and running again generates an error because we'll
        // try to recreate the resources
        if (!roundEnv.processingOver()) {
            Beans beansType = new Beans();

            Set<? extends Element> nanoBeanElements = roundEnv.getElementsAnnotatedWith(NanoBean.class);
            if (nanoBeanElements != null) {
                for (Element element : nanoBeanElements) {
                    TypeElement typeElement = (TypeElement) element;
                    BeanType beanElement = generateXmlElement(typeElement, element.getAnnotation(NanoBean.class));
                    ConstructorElementVisitor visitor = new ConstructorElementVisitor();
                    List<ConstructorArgType> result = typeElement.accept(visitor, Collections.EMPTY_MAP);
                    if (result != null) {
                        for (ConstructorArgType ctorArg : result) {
                            beanElement.getConstructorArg().add(ctorArg);
                        }

                        beans.add(beanElement);
                    }
                }
            }

            Set<? extends Element> singletonBeanElements = roundEnv.getElementsAnnotatedWith(NanoSingleton.class);
            if (singletonBeanElements != null) {
                for (Element element : singletonBeanElements) {
                    TypeElement typeElement = (TypeElement) element;
                    BeanType beanElement = generateXmlElement(typeElement, element.getAnnotation(NanoSingleton.class));
                    ConstructorElementVisitor visitor = new ConstructorElementVisitor();
                    List<ConstructorArgType> result = typeElement.accept(visitor, Collections.EMPTY_MAP);
                    if (result != null) {
                        for (ConstructorArgType ctorArg : result) {
                            beanElement.getConstructorArg().add(ctorArg);
                        }

                        beans.add(beanElement);
                    }
                }
            }

            // sort the beans here
            Collections.sort(beans, new DependencyComparator());
            beansType.getBean().addAll(beans);

            // write the configuration file to the generated resources
            FileObject outputContextFile = null;
            OutputStream out = null;
            try {
                outputContextFile = this.processingEnv.getFiler()
                        .createResource(StandardLocation.CLASS_OUTPUT, "", contextFileName, null);
                out = outputContextFile.openOutputStream();

                JAXBContext jaxbContext = JAXBContext.newInstance("com.paypal.credit.context.xml");
                jaxbContext.createMarshaller().marshal(beansType, out);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JAXBException e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                } catch (Throwable t) {
                }
            }
        }
        return false;
    }

    private class DependencyComparator implements Comparator<BeanType> {

        @Override
        public int compare(BeanType beanOne, BeanType beanTwo) {
            if(beanOne == null) {
                return 1;
            }
            if(beanTwo == null) {
                return -1;
            }

            final String beanOneIdentifier = beanOne.getId();
            final String beanTwoIdentifier = beanTwo.getId();

            // if the constructor args for bean one include a reference to bean two then
            // bean two must come before bean one (i.e. return 1)
            for (ConstructorArgType ctorArg : beanOne.getConstructorArg()) {
                if (ctorArg.getRef() != null) {
                    if (beanTwoIdentifier.equals(ctorArg.getRef().getBean())) {
                        return 1;
                    }
                }
            }

            // if the constructor args for bean one include a reference to bean two then
            // bean two must come before bean one (i.e. return 1)
            for (ConstructorArgType ctorArg : beanTwo.getConstructorArg()) {
                if (ctorArg.getRef() != null) {
                    if (beanOneIdentifier.equals(ctorArg.getRef().getBean())) {
                        return -1;
                    }
                }
            }

            return 0;
        }
    }

    /**
     * R - the return type of this visitor's methods. Use Void for visitors that do not need to return results.
     * P - the type of the additional parameter to this visitor's methods.
     *     Use Void for visitors that do not need an additional parameter.
     */
    private class ConstructorElementVisitor extends SimpleElementVisitor8<List<ConstructorArgType>, Map<String, String>> {
        private List<ConstructorArgType> result = null;

        ConstructorElementVisitor() {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "ConstructorElementVisitor created."
            );
        }

        @Override
        protected List<ConstructorArgType> defaultAction(Element e, Map<String, String> options) {
            List<ConstructorArgType> result = super.defaultAction(e, options);
            if (result == null) {
                result = new ArrayList<>();
            }

            for (Element child : e.getEnclosedElements()) {
                result.addAll(child.accept(this, options));
            }

            return result;
        }

        /**
         *
         * @param executableElement
         * @param options
         * @return
         */
        @Override
        public List<ConstructorArgType> visitExecutable(ExecutableElement executableElement, Map<String, String> options) {
            List<ConstructorArgType> result = new ArrayList<>();
            final String elementName = executableElement.getSimpleName().toString();

            if (executableElement.getKind() == ElementKind.CONSTRUCTOR) {

                StringBuilder sb = new StringBuilder(elementName);
                sb.append('(');

                for (VariableElement parameter : executableElement.getParameters()) {
                    NanoInject injectAnnotation = parameter.getAnnotation(NanoInject.class);
                    if (injectAnnotation == null) {
                        result = null;
                        break;
                    }
                    String identifier = injectAnnotation.identifier();
                    TypeMirror parameterType = parameter.asType();
                    String parameterName = parameter.getSimpleName().toString();

                    ConstructorArgType ctorArg = new ConstructorArgType();
                    if (identifier == null) {
                        BeanType bean = new BeanType();
                        bean.setScope(ScopeType.PROTOTYPE);
                        bean.setClazz(parameterType.toString());
                        ctorArg.setBean(bean);
                    } else {
                        ReferenceType ref = new ReferenceType();
                        ref.setBean(identifier);
                        ctorArg.setRef(ref);
                    }
                    result.add(ctorArg);

                    sb.append(String.format("%s(identifier=%s) %s %s, ",
                        injectAnnotation.annotationType().getSimpleName(), identifier, parameterType.toString(), parameterName));
                }
                sb.append(')');

                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.NOTE,
                        String.format("%s", sb.toString())
                );

            }

            this.result = result;
            return result;
        }

        public List<ConstructorArgType> getResult() {
            return result;
        }
    }

    private BeanType generateXmlElement(TypeElement typeElement, NanoBean nanoBeanAnnotation) {
        String beanClassName = typeElement.getQualifiedName().toString();

        return generateXmlElement(typeElement.getQualifiedName().toString(), nanoBeanAnnotation.identifier(), ScopeType.PROTOTYPE);
    }

    private BeanType generateXmlElement(TypeElement typeElement, NanoSingleton nanoBeanAnnotation) {
        String beanClassName = typeElement.getQualifiedName().toString();
        return generateXmlElement(typeElement.getQualifiedName().toString(), nanoBeanAnnotation.identifier(), ScopeType.SINGLETON);
    }

    private BeanType generateXmlElement(final String beanClassName, final String identifier, final ScopeType scopeType) {
        BeanType beanType = new BeanType();
        beanType.setClazz(beanClassName);
        beanType.setScope(scopeType);

        if (identifier != null && identifier.length() > 0) {
            beanType.setId(identifier);
        }

        return beanType;
    }

    /**
     *
     * @param element
     * @param annotation
     * @param member
     * @param userText
     * @return - always return null, this class does not provide Completions
     */
    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotation, ExecutableElement member, String userText) {
        return null;
    }
}
