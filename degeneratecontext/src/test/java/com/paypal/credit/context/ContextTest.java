package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.BeanClassNotFoundException;
import com.paypal.credit.context.exceptions.CannotCreateObjectFromStringException;
import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.exceptions.SparseArgumentListDetectedException;
import com.paypal.credit.context.xml.BeanType;
import com.paypal.credit.context.xml.ConstructorArgType;
import com.paypal.credit.context.xml.ListType;
import com.paypal.credit.context.xml.ScopeType;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by cbeckey on 2/4/16.
 */
public class ContextTest {

    // ====================================================================================
    // createBeanDecoratorHierarchy tests
    // ====================================================================================

    @DataProvider
    public Object[][] beanTypeDecoratorCreationDataProvider() {
        List<Object[]> result = new ArrayList<>();

        List<BeanType> beanTypes = null;
        Integer expectedTopLevel = null;
        Integer[] expectedSecondLevelCounts = null;

        beanTypes = new ArrayList<>();
        beanTypes.add(createBeanType(String.class.getName(), "someId", ScopeType.PROTOTYPE));
        expectedTopLevel = new Integer(1);
        result.add(new Object[]{beanTypes, expectedTopLevel, expectedSecondLevelCounts});

        beanTypes = new ArrayList<>();
        BeanType bt = createBeanType(Integer.class.getName(), "topLevel", ScopeType.PROTOTYPE);
        ConstructorArgType ctorArg = new ConstructorArgType();
        ctorArg.setBean(createBeanType(String.class.getName(), "ctorArg", ScopeType.PROTOTYPE));
        bt.getConstructorArg().add(ctorArg);
        beanTypes.add(bt);
        expectedTopLevel = new Integer(1);
        expectedSecondLevelCounts = new Integer[]{new Integer(1)};
        result.add(new Object[]{beanTypes, expectedTopLevel, expectedSecondLevelCounts});

        Object[][] resultArray = new Object[result.size()][];

        for(int index=0; index<result.size(); ++index) {
            resultArray[index] = result.get(index);
        }

        return resultArray;
    }

    @Test(dataProvider = "beanTypeDecoratorCreationDataProvider")
    public void testBeanTypeDecoratorCreation(final List<BeanType> beanTypes, final Integer topLevelCount, final Integer[] secondLevelCounts) {
        Set<Context.BeanTypeDecorator> decorators = Context.createBeanDecoratorHierarchy(beanTypes);

        Assert.assertNotNull(decorators);
        Assert.assertEquals(decorators.size(), topLevelCount.intValue());

        if (secondLevelCounts != null) {
            Assert.assertEquals(secondLevelCounts.length, topLevelCount.intValue(), "Test is flawed, not the subject code");

            int index = 0;
            for (Context.BeanTypeDecorator decorator : decorators) {
                Assert.assertEquals(decorator.getDependencies().size(), secondLevelCounts[index].intValue());
                ++index;
            }
        }
    }

    // ====================================================================================
    // createInstanceFromStringValue tests
    // ====================================================================================
    @DataProvider
    public Object[][] createInstanceFromStringValueDataProvider() {
        return new Object[][] {
                new Object[]{Integer.class, "1", new Integer(1)},
                new Object[]{Integer.class, "-1", new Integer(-1)},
                new Object[]{Integer.class, "0", new Integer(0)},
                new Object[]{Float.class, "1.0", new Float(1.0)},
                new Object[]{Finteger.class, "1", new Finteger("1")},
        };
    }

    @Test(dataProvider = "createInstanceFromStringValueDataProvider")
    public void testCreateInstanceFromStringValue(final Class<?> clazz, final String value, final Object expectedValue)
            throws CannotCreateObjectFromStringException {
        Assert.assertEquals(Context.createInstanceFromStringValue(clazz, value), expectedValue);
    }

    /**
     * A simple class to test instance creation from a constructor.
     */
    public static class Finteger {
        private final Integer wrapped;

        public Finteger(final String s) throws NumberFormatException {
            wrapped = new Integer(s);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Finteger finteger = (Finteger) o;
            return Objects.equals(wrapped, finteger.wrapped);
        }

        @Override
        public int hashCode() {
            return Objects.hash(wrapped);
        }
    }

    // ====================================================================================
    // static boolean isApplicableConstructor(final Constructor<?> ctor, final List<ConstructorArgType> orderedParameters)
    // ====================================================================================

    @DataProvider
    public Object[][] isApplicableConstructorDataProvider() throws NoSuchMethodException {
        return new Object[][] {
                new Object[]{
                        ConstructorTestSubject.class.getDeclaredConstructor(new Class<?>[]{}),
                        (List<ConstructorArgType>)null,
                        Boolean.TRUE
                },
                new Object[]{
                        ConstructorTestSubject.class.getDeclaredConstructor(new Class<?>[]{String.class}),
                        Arrays.asList(createConstructorArgType("value", null)),
                        Boolean.TRUE
                },
                new Object[]{
                        ConstructorTestSubject.class.getDeclaredConstructor(new Class<?>[]{Integer.class}),
                        Arrays.asList(createConstructorArgType("1", null)),
                        Boolean.TRUE
                },
                new Object[]{
                        ConstructorTestSubject.class.getDeclaredConstructor(new Class<?>[]{Number.class}),
                        Arrays.asList(createConstructorArgType("1", null)),
                        Boolean.FALSE
                },
                new Object[]{
                        ConstructorTestSubject.class.getDeclaredConstructor(new Class<?>[]{String.class, Integer.class}),
                        Arrays.asList(createConstructorArgType("value", null), createConstructorArgType("1", null)),
                        Boolean.TRUE
                },
                new Object[]{
                        ConstructorTestSubject.class.getDeclaredConstructor(new Class<?>[]{String.class, Number.class}),
                        Arrays.asList(createConstructorArgType("value", null), createConstructorArgType("1", null)),
                        Boolean.FALSE
                },
        };
    }

    @Test(dataProvider="isApplicableConstructorDataProvider")
    public void testIsApplicableConstructor(final Constructor<?> ctor, final List<ConstructorArgType> orderedParameters, final Boolean expectedResult)
            throws BeanClassNotFoundException {
        Assert.assertEquals(Context.isApplicableConstructor(ctor, orderedParameters), expectedResult.booleanValue());
    }

    // ====================================================================================
    // static <T> Constructor<T> selectConstructor(
    //   final Class<T> beanClazz,
    //   final List<ConstructorArgType> orderedParameters)
    // ====================================================================================

    @DataProvider
    public Object[][] selectConstructorDataProvider() throws NoSuchMethodException {
        return new Object[][] {
                new Object[]{
                        ConstructorTestSubject.class,
                        (List<ConstructorArgType>)null,
                        ConstructorTestSubject.class.getDeclaredConstructor(new Class<?>[]{})
                },
                new Object[]{
                        ConstructorTestSubject.class,
                        Arrays.asList(createConstructorArgType("1", null)),
                        ConstructorTestSubject.class.getDeclaredConstructor(new Class<?>[]{Integer.class})
                }
        };
    }

    @Test(dataProvider = "selectConstructorDataProvider")
    public <T> void testSelectConstructor(final Class<T> beanClazz, final List<ConstructorArgType> orderedParameters, final Constructor<T> expectCtor) throws BeanClassNotFoundException {
        Assert.assertEquals(Context.selectConstructor(beanClazz, orderedParameters), expectCtor);
    }

    // ====================================================================================
    // static List<ConstructorArgType> createOrderedParameterList(final BeanType beanType)
    // throws SparseArgumentListDetectedException {
    // ====================================================================================

    @DataProvider
    public Object[][] createOrderedParameterListDataProvider() {
        return new Object[][] {
                new Object[]{
                        addConstructorArg(
                            createBeanType("com.paypal.credit.context.ContextTest.ConstructorTestSubject", "id1", ScopeType.PROTOTYPE),
                            createConstructorArgType("hello", null)),
                        Arrays.asList(createConstructorArgType("hello", null))
                },
                new Object[]{
                        addConstructorArg(
                            addConstructorArg(
                                createBeanType("com.paypal.credit.context.ContextTest.ConstructorTestSubject", "id1", ScopeType.PROTOTYPE),
                                createConstructorArgType("hello", null)),
                            createConstructorArgType("world", new Integer(0))),
                        Arrays.asList(
                                createConstructorArgType("world", new Integer(0)),
                                createConstructorArgType("hello", null))
                }
                ,
                new Object[]{
                        addConstructorArg(
                            addConstructorArg(
                                    addConstructorArg(
                                            createBeanType("com.paypal.credit.context.ContextTest.ConstructorTestSubject", "id1", ScopeType.PROTOTYPE),
                                            createConstructorArgType("hello", null)),
                                    createConstructorArgType("world", new Integer(0))),
                                createConstructorArgType("peas", new Integer(2))),
                        Arrays.asList(
                                createConstructorArgType("world", new Integer(0)),
                                createConstructorArgType("hello", null),
                                createConstructorArgType("peas", new Integer(2)))
                }
        };
    }

    @Test(dataProvider = "createOrderedParameterListDataProvider")
    public void testCreateOrderedParameterList(final BeanType beanType, List<ConstructorArgType> expected)
    throws SparseArgumentListDetectedException {
        List<ConstructorArgType> actual = Context.createOrderedParameterList(beanType);
        Assert.assertEquals(actual.size(), expected.size());
        for (int index=0; index<expected.size(); ++index) {
            Assert.assertTrue(isEquals(actual.get(index), expected.get(index)));
        }
    }

    // ====================================================================================
    // static Object[] createArguments(final List<ConstructorArgType> orderedParameters, final Class<?>[] parameterTypes)
    // throws ContextInitializationException
    // ====================================================================================

    @DataProvider
    public Object[][] createArgumentsDataProvider() {
        return new Object[][] {
                // test of no-arg method
                new Object[]{
                        Arrays.asList(),
                        new Class<?>[]{},
                        new Object[]{}
                },
                // test of one String argument
                new Object[]{
                        Arrays.asList(createConstructorArgType("hello", null)),
                        new Class<?>[]{String.class},
                        new Object[]{"hello"}
                },
                // test of two String argument
                new Object[]{
                        Arrays.asList(createConstructorArgType("hello", null), createConstructorArgType("world", null)),
                        new Class<?>[]{String.class, String.class},
                        new Object[]{"hello", "world"}
                },
                // test of one argument requiring conversion
                new Object[]{
                        Arrays.asList(createConstructorArgType("42", null)),
                        new Class<?>[]{Integer.class},
                        new Object[]{new Integer(42)}
                },
        };
    }

    @Test(dataProvider = "createArgumentsDataProvider")
    public void testCreateArguments(final List<ConstructorArgType> orderedParameters, final Class<?>[] parameterTypes, final Object[] expected) throws ContextInitializationException {
        Object[] actual = Context.createArguments(orderedParameters, parameterTypes);
        Assert.assertEquals(actual, expected);
    }

    // ====================================================================================
    // Creational Helper Methods
    // ====================================================================================
    private static ConstructorArgType createConstructorArgType(final BeanType beanType, final Integer index) {
        ConstructorArgType cat = new ConstructorArgType();
        cat.setBean(beanType);
        cat.setIndex(index);
        return cat;
    }

    private static ConstructorArgType createConstructorArgType(final String value, final Integer index) {
        ConstructorArgType cat = new ConstructorArgType();
        cat.setValue(value);
        cat.setIndex(index);
        return cat;
    }

    private static ConstructorArgType createConstructorArgType(final ListType list, final Integer index) {
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

    private static BeanType addConstructorArg(final BeanType beanType, final ConstructorArgType ctorArg) {
        beanType.getConstructorArg().add(ctorArg);
        return beanType;
    }

    private static ListType createListType(Object ... listElements) {
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
    private static boolean nullEquals(final Object arg1, final Object arg2) {
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
    private static boolean nullAndSizeSensitiveEquals(final Collection arg1, final Collection arg2) {
        return nullEquals(arg1, arg2) ?
                arg1 != null && arg1.size() == arg2.size()
                : false;
    }

    private static boolean isEquals(final ConstructorArgType arg1, final ConstructorArgType arg2) {
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

    private static boolean isEquals(final ListType arg1, final ListType arg2) {
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

    private static boolean isEquals(final BeanType arg1, final BeanType arg2) {
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
