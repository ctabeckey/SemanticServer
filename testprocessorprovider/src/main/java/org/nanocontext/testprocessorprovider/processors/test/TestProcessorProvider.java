package com.paypal.credit.processors.test;

import java.util.Collections;
import java.util.HashSet;

/**
 * Created by cbeckey on 1/11/16.
 */
public class TestProcessorProvider
extends ProcessorProviderImpl {
    private final static Set<ProcessorInfo> processorInfoSet;

    private static final Logger LOGGER = LoggerFactory.getLogger(TestProcessorProvider.class);

    static {
        Set<ProcessorInfo> tempProcessorInfoSet = new HashSet<>();

        try {
            tempProcessorInfoSet.add(ProcessorInfo.create(com.paypal.credit.processors.test.ProcessorOne.class));
            tempProcessorInfoSet.add(ProcessorInfo.create(com.paypal.credit.processors.test.ProcessorTwo.class));
            tempProcessorInfoSet.add(ProcessorInfo.create(com.paypal.credit.processors.test.ProcessorThree.class));
            tempProcessorInfoSet.add(ProcessorInfo.create(com.paypal.credit.processors.test.ProcessorFour.class));
            tempProcessorInfoSet.add(ProcessorInfo.create(com.paypal.credit.processors.test.ProcessorFive.class));

            processorInfoSet = Collections.unmodifiableSet(tempProcessorInfoSet);
        } catch (InvalidProcessorException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * The publisher is strictly for informational purposes.
     *
     * @return
     */
    @Override
    public String getPublisher() {
        return "Test";
    }

    /**
     * Return a Set of all available ProcessorDescriptor. Each represents
     * a single available Processor. This is the only method that must be implemented
     * by derived types.
     *
     * @return
     */
    @Override
    public Set<ProcessorInfo> getProcessorInfos()
            throws ProcessorProviderException {
        return processorInfoSet;
    }

}