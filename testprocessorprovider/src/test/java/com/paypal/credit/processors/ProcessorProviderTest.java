package com.paypal.credit.processors;

import com.paypal.credit.processors.context.TestProcessorContext;
import com.paypal.credit.processors.exceptions.ProcessorProviderException;
import com.paypal.credit.processors.test.ProcessorFive;
import com.paypal.credit.processors.test.ProcessorFour;
import com.paypal.credit.processors.test.ProcessorOne;
import com.paypal.credit.processors.test.ProcessorThree;
import com.paypal.credit.processors.test.ProcessorTwo;
import com.paypal.credit.workflow.RSProcessorContext;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Set;

/**
 * Created by cbeckey on 1/13/16.
 */
public class ProcessorProviderTest {
    /**
     *
     */
    @Test
    public void testGetProcessorInfos() throws ProcessorProviderException {
        RootProcessorProvider processorProvider = RootProcessorProvider.getOrCreate();

        Set<ProcessorInfo> processorInfos = processorProvider.getProcessorInfos();
        Assert.assertNotNull(processorInfos);
        Assert.assertTrue(processorInfos.size() == 5);
    }

    @DataProvider
    public Object[][] singleInstanceFindByProcessorData() {
        return new Object[][] {
                new Object[]{"ProcessorOne", ProcessorOne.class, TestProcessorContext.class},
                new Object[]{"ProcessorTwo", ProcessorTwo.class, TestProcessorContext.class},
                new Object[]{"ProcessorThree", ProcessorThree.class, RSProcessorContext.class},
                new Object[]{"ProcessorFour", ProcessorFour.class, RSProcessorContext.class},
                new Object[]{"ProcessorFive", ProcessorFive.class, RSProcessorContext.class},
        };
    }

    @Test(dataProvider = "singleInstanceFindByProcessorData")
    public void testSingleInstanceFindByProcessor(final String processorName, final Class<?> processorClass, final Class<?> contextClass)
            throws ProcessorProviderException {
        RootProcessorProvider processorProvider = RootProcessorProvider.getOrCreate();

        Set<ProcessorInfo> processorInfo = processorProvider.findProcessorInfos(processorName, true, null);
        Assert.assertNotNull(processorInfo);
        Assert.assertTrue(processorInfo.size() == 1);
        ProcessorInfo processorOneInfo = processorInfo.iterator().next();
        Assert.assertEquals(processorOneInfo.getProcessorClass(), processorClass);
        Assert.assertEquals(processorOneInfo.getContextClass(), contextClass);
    }

    @DataProvider
    public Object[][] findProcessorsByContextDataProvider() {
        return new Object[][] {
                new Object[]{TestProcessorContext.class, new Class<?>[]{ProcessorOne.class, ProcessorTwo.class, ProcessorThree.class, ProcessorFour.class, ProcessorFive.class}},
                new Object[]{RSProcessorContext.class, new Class<?>[]{ProcessorThree.class, ProcessorFour.class, ProcessorFive.class}}
        };
    }

    @Test(dataProvider = "findProcessorsByContextDataProvider")
    public void testFindProcessorsByContext(final Class<?> contextClass, Class<?>[] expectedProcessorClasses)
            throws ProcessorProviderException {
        RootProcessorProvider processorProvider = RootProcessorProvider.getOrCreate();

        Set<ProcessorInfo> processorInfos = processorProvider.findProcessorInfos(null, true, contextClass);
        Assert.assertNotNull(processorInfos);

        Assert.assertEquals(processorInfos.size(), expectedProcessorClasses.length);
        for (Class<?> expectedProcessorClass : expectedProcessorClasses) {
            boolean processorInfoFound = false;
            for (ProcessorInfo processorInfo : processorInfos) {
                if (expectedProcessorClass.equals(processorInfo.getProcessorClass())) {
                    processorInfoFound = true;
                    break;
                }
            }
            if (! processorInfoFound) {
                Assert.fail(String.format("%s was not found in the Set<ProcessorInfo>", expectedProcessorClass));
            }
        }
    }
}
