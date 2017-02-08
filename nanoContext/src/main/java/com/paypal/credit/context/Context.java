package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.utility.references.Derivations;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * A very small and limited function IoC Context.
 * Implements simple hierarchical delegation, whereas if a bean cannot be found
 * within this context it will delegate to a parent context.
 */
public class Context {
    /**
     * The bean references that make up this context.
     * Immutable once created.
     */
    private Map<String, AbstractReferencableProperty> contextObjectsNameMap;
    private Set<ArtifactHolder> artifacts;
    private final ThreadGroup contextThreadGroup;
    private final Context parent;

    /**
     * Package level scope is intentional, only the ContextFactory should
     * construct instances of this Class.
     */
    Context() {
        this(null);
    }

    /**
     * Package level scope is intentional, only the ContextFactory should
     * construct instances of this Class.
     */
    Context(final Context parent) {
        this.contextThreadGroup = new ThreadGroup("ContextThreadGroup");
        this.parent = parent;
    }

    /**
     * Get the parent context if there is one, else return null.
     *
     * @return
     */
    public Context getParent() {
        return parent;
    }

    /**
     * Returns the ThreadGroup under which Threads for Active beans will be
     * members of.
     * @return
     */
    public ThreadGroup getContextThreadGroup() {
        return contextThreadGroup;
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

        T bean = selectedBeanReference == null ? null : (T) selectedBeanReference.getValue();

        // delegate to the parent if the bean was not found
        if (bean == null && this.parent != null) {
            bean = this.parent.getBean(beanClass);
        }

        return bean;
    }

    /**
     * @param id
     * @param beanClass
     * @param <T>
     * @return
     */
    public <T> T getBean(final String id, final Class<T> beanClass)
            throws ContextInitializationException {
        AbstractProperty<T> beanReference = getBeanReference(id);
        T bean = beanReference.getValue();

        // validate that the bean is of the expected class or that the class was not specified
        bean = beanClass == null || beanClass.isInstance(bean) ? bean : null;

        // delegate to the parent if the bean was not found
        if (bean == null && this.parent != null) {
            bean = this.parent.getBean(beanClass);
        }

        return bean;
    }

    /**
     * @param id
     * @return
     * @throws ContextInitializationException
     */
    public AbstractProperty getBeanReference(final String id)
            throws ContextInitializationException {
        AbstractProperty ap = contextObjectsNameMap.get(id);

        if (ap == null && this.parent != null) {
            ap = this.parent.getBeanReference(id);
        }

        return ap;
    }

    // ========================================================================================
    // A simple mechanism for managing ActiveBean instances.
    // ========================================================================================
    private Set<ActiveBean> activeBeans = new ConcurrentSkipListSet<>();

    void registerActiveBean(ActiveBean activeBean) {
        activeBeans.add(activeBean);
    }

    public void shutdown() {
        for (ActiveBean activeBean : activeBeans) {
            activeBean.shutdown();
        }
    }

}