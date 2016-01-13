package com.paypal.credit.processors.test;

import com.paypal.credit.processors.ProcessorInfo;
import com.paypal.credit.processors.ProcessorProviderImpl;
import com.paypal.credit.processors.exceptions.InvalidProcessorException;
import com.paypal.credit.processors.exceptions.ProcessorProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
            tempProcessorInfoSet.add(ProcessorInfo.create(ProcessorOne.class));
            tempProcessorInfoSet.add(ProcessorInfo.create(ProcessorTwo.class));
            tempProcessorInfoSet.add(ProcessorInfo.create(ProcessorThree.class));
            tempProcessorInfoSet.add(ProcessorInfo.create(ProcessorFour.class));
            tempProcessorInfoSet.add(ProcessorInfo.create(ProcessorFive.class));

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