package com.paypal.credit.context;

/**
 * The abstract super-class for all AbstractProperty classes that are referencable by name.
 * The known derivations are:
 * @see AbstractBeanInstanceFactory
 * @see PrototypeBeanInstanceFactory
 * @see SingletonBeanInstanceFactory
 * @see PreresolvedBean
 */
public abstract class AbstractReferencableProperty<T> extends AbstractProperty<T> {
    private final String identifier;

    /**
     * Required pass-through constructor
     * @param context
     */
    public AbstractReferencableProperty(final Context context, final String identifier) {
        super(context);
        this.identifier = identifier;
    }

    /**
     *
     * @return
     */
    public String getIdentifier() {
        return this.identifier;
    };

}
