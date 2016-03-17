package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.CircularReferenceException;
import com.paypal.credit.context.xml.BeanType;
import com.paypal.credit.context.xml.BeansType;
import com.paypal.credit.context.xml.ConstructorArgType;
import com.paypal.credit.context.xml.ReferenceType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Circular reference detection helpers
 */
public class CircularReferenceDetector {
    /**
     * This method MUST be called by derived classes whenever the derived class
     * is instantiating a bean instance.
     *
     * @throws CircularReferenceException
     */
    public void detectCircularReferences(final BeansType beans) throws CircularReferenceException {
        detectCircularReferences(beans.getBean(), new HashSet<String>());
    }

    /**
     *
     * @param beans
     * @param knownReferenceIds
     * @throws CircularReferenceException
     */
    private void detectCircularReferences(final List<BeanType> beans, final Set<String> knownReferenceIds)
            throws CircularReferenceException {
        for (BeanType currentBean : beans) {
            detectCircularReferences(currentBean, new HashSet<String>());
        }
    }

    /**
     *
     * @param bean
     * @param knownReferenceIds
     * @throws CircularReferenceException
     */
    private void detectCircularReferences(final BeanType bean, final Set<String> knownReferenceIds)
            throws CircularReferenceException {
        String currentReferenceIdentifier = bean.getId();

        if (knownReferenceIds.contains(currentReferenceIdentifier)) {
            throw new CircularReferenceException(currentReferenceIdentifier, bean.getClazz());

        } else {
            knownReferenceIds.add(currentReferenceIdentifier);

            for (ConstructorArgType ctorArg : bean.getConstructorArg()) {
                BeanType ctorArgBean = ctorArg.getBean();
                if (ctorArgBean != null) {
                    detectCircularReferences(ctorArgBean, knownReferenceIds);
                } else {
                    ReferenceType ctorArgRef = ctorArg.getRef();
                    if (ctorArgRef != null) {
                        String ctorArgBeanRef = ctorArgRef.getBean();
                        if (knownReferenceIds.contains(ctorArgBeanRef)) {
                            throw new CircularReferenceException(currentReferenceIdentifier, bean.getClazz());
                        }
                    }
                }
            }
        }
    }
}
