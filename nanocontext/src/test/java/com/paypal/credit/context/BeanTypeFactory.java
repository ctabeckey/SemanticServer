package com.paypal.credit.context;

import com.paypal.credit.context.xml.BeanType;
import com.paypal.credit.context.xml.ConstructorArgType;
import com.paypal.credit.context.xml.ScopeType;

import java.util.List;

/**
 * Created by cbeckey on 1/23/17.
 */
public class BeanTypeFactory {
    public static BeanType create (
            String id, String clazz, ScopeType scope, String artifact,
            String factory, String factoryClass, String factoryMethod,
            Boolean active,
            List<ConstructorArgType> constructorArg) {
        BeanType beanType = new BeanType();

        beanType.setId(id);
        beanType.setClazz(clazz);
        beanType.setScope(scope);
        beanType.setArtifact(artifact);
        beanType.setFactory(factory);
        beanType.setFactoryClass(factoryClass);
        beanType.setFactoryMethod(factoryMethod);
        beanType.setActive(active);

        beanType.getConstructorArg().addAll(constructorArg);

        return beanType;
    }


}
