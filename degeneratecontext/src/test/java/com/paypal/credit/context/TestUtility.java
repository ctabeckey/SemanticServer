package com.paypal.credit.context;

import com.paypal.credit.context.xml.BeanType;
import com.paypal.credit.context.xml.ConstructorArgType;
import com.paypal.credit.context.xml.ListType;
import com.paypal.credit.context.xml.ScopeType;

import java.util.Collection;

/**
 * Created by cbeckey on 2/8/16.
 */
public class TestUtility {
    private TestUtility() {}

    // ====================================================================================
    // Creational Helper Methods
    // ====================================================================================
    public static ConstructorArgType createConstructorArgType(final BeanType beanType, final Integer index) {
        ConstructorArgType cat = new ConstructorArgType();
        cat.setBean(beanType);
        cat.setIndex(index);
        return cat;
    }

    public static ConstructorArgType createConstructorArgType(final String value, final Integer index) {
        ConstructorArgType cat = new ConstructorArgType();
        cat.setValue(value);
        cat.setIndex(index);
        return cat;
    }

    public static ConstructorArgType createConstructorArgType(final ListType list, final Integer index) {
        ConstructorArgType cat = new ConstructorArgType();
        cat.setList(list);
        cat.setIndex(index);
        return cat;
    }

    public static BeanType createBeanType(final String clazzName, final String identifier, final ScopeType scope) {
        BeanType bt = new BeanType();
        bt.setClazz(clazzName);
        bt.setId(identifier);
        bt.setScope(scope);

        return bt;
    }

    public static BeanType addConstructorArg(final BeanType beanType, final ConstructorArgType ctorArg) {
        beanType.getConstructorArg().add(ctorArg);
        return beanType;
    }

    public static ListType createListType(Object ... listElements) {
        ListType list = new ListType();

        if (listElements != null) {
            for (Object listElement : listElements) {
                if (listElement instanceof BeanType || listElement instanceof String || listElement instanceof ListType) {
                    list.getBeanOrValueOrList().add(listElement);
                }
            }
        }

        return list;
    }

    // ====================================================================================
    // Comparator Helper Methods
    // ====================================================================================
    /**
     * Returns TRUE if both objects are null or if arg1.equals(arg2), else returns false
     * @param arg1
     * @param arg2
     * @return
     */
    public static boolean nullEquals(final Object arg1, final Object arg2) {
        if (arg1 == null && arg2 == null) {
            return true;
        }
        if (arg1 == null && arg2 != null || arg1 != null && arg2 == null) {
            return false;
        }

        return true;
    }

    /**
     * Returns TRUE if both objects are null or if arg1.size() == arg2.size(), else returns false
     * @param arg1
     * @param arg2
     * @return
     */
    public static boolean nullAndSizeSensitiveEquals(final Collection arg1, final Collection arg2) {
        return nullEquals(arg1, arg2) ?
                arg1 != null && arg1.size() == arg2.size()
                : false;
    }

    public static boolean isEquals(final ConstructorArgType arg1, final ConstructorArgType arg2) {
        if (! nullEquals(arg1, arg2)) {
            return false;
        }
        if (arg1 == null) {     // both are null, return true
            return true;
        }

        // arg1 and arg2 have both been determined to be non-null by this point
        return nullEquals(arg1.getIndex(), arg2.getIndex())
                && isEquals(arg1.getBean(), arg2.getBean())
                && isEquals(arg1.getList(),arg2.getList());
    }

    public static boolean isEquals(final ListType arg1, final ListType arg2) {
        if (! nullEquals(arg1, arg2)) {
            return false;
        }
        if (arg1 == null) {     // both are null, return true
            return true;
        }

        // arg1 and arg2 have both been determined to be non-null by this point
        if (!nullAndSizeSensitiveEquals(arg1.getBeanOrValueOrList(), arg2.getBeanOrValueOrList())) {
            return false;
        }

        for (int index=0; index < arg1.getBeanOrValueOrList().size(); ++index) {
            if (! arg1.getBeanOrValueOrList().get(index).equals(arg2.getBeanOrValueOrList().get(index))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEquals(final BeanType arg1, final BeanType arg2) {
        if (! nullEquals(arg1, arg2)) {
            return false;
        }
        if (arg1 == null) {     // both are null, return true
            return true;
        }

        // arg1 and arg2 have both been determined to be non-null by this point
        return arg1.getClazz().equals(arg2.getClazz())
                && arg1.getId().equals(arg2.getId())
                && arg1.getScope().equals(arg2.getScope());
    }
}
