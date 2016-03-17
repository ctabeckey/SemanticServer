package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.utility.references.Derivations;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * A very small and limited function IoC Context.
 */
public class Context {
    /**
     * The bean references that make up this context.
     * Immutable once created.
     */
    private Map<String, AbstractReferencableProperty> contextObjectsNameMap;
    private Set<ArtifactHolder> artifacts;

    /**
     * Package level scope is intentional, only the ContextFactory should
     * construct instances of this Class.
     */
    Context() {
    }

    /**
     * Package level scope is intentional, only the ContextFactory should
     * call this method.
     *
     * @param artifacts
     */
    void setArtifacts(Set<ArtifactHolder> artifacts) {
        this.artifacts = Collections.unmodifiableSet(artifacts);
    }

    /**
     * Package level scope is intentional, only the ContextFactory should
     * call this method.
     *
     * @param contextObjectsNameMap
     */
    void setContextObjectsMap(final Map<String, AbstractReferencableProperty> contextObjectsNameMap) {
        this.contextObjectsNameMap = Collections.unmodifiableMap(contextObjectsNameMap);
    }

    /**
     * @param identifier
     * @return
     */
    public ArtifactHolder getArtifactHolder(String identifier) {
        if (this.artifacts != null && identifier != null) {
            for (ArtifactHolder holder : this.artifacts) {
                if (identifier.equals(holder.getIdentifier())) {
                    return holder;
                }
            }
        }
        return null;
    }

    /**
     * Get a Bean from the context whose type most closely matches the given type.
     * Most closely matches is defined as the given type is the same or that the given type is
     * a superclass of the given type and that it is the most specific superclass
     * of the given type in the context.
     *
     * @param beanClass the desired bean type
     * @param <T>       The requested bean type
     * @return the bean of the requested type or derivation of the given type
     */
    public <T> T getBean(final Class<T> beanClass)
            throws ContextInitializationException {
        // find the most specific bean in the context by type
        int minDistance = Integer.MAX_VALUE;
        AbstractBeanInstanceFactory<T> selectedBeanReference = null;

        for (AbstractProperty<?> property : contextObjectsNameMap.values()) {
            int beanDistance = Derivations.instanceDistance(property.getValueType(), beanClass);

            if (beanDistance < minDistance) {
                minDistance = beanDistance;
                selectedBeanReference = (AbstractBeanInstanceFactory<T>) property;
            }
        }

        return (T) selectedBeanReference.getValue();
    }

    /**
     * @param name
     * @param beanClass
     * @param <T>
     * @return
     */
    public <T> T getBean(final String name, final Class<T> beanClass)
            throws ContextInitializationException {
        AbstractProperty<T> beanReference = getBeanReference(name);
        T bean = beanReference.getValue();
        if (beanClass == null || beanClass.isInstance(bean)) {
            return bean;
        }

        return null;
    }

    /**
     * @param name
     * @return
     * @throws ContextInitializationException
     */
    public AbstractProperty getBeanReference(final String name)
            throws ContextInitializationException {
        return contextObjectsNameMap.get(name);
    }
}