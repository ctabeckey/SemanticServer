package com.paypal.credit.core;

import com.paypal.credit.context.exceptions.ContextInitializationException;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;

/**
 * Created by cbeckey on 2/3/17.
 */
public class ApplicationTest {
    @Test
    public void testBaseApplicationCreation()
            throws FileNotFoundException, JAXBException, ContextInitializationException {
        Application baseApplication = Application.create(true);

        Assert.assertNotNull(baseApplication);

        Assert.assertNotNull(baseApplication.getApplicationSemantics());
        Assert.assertNotNull(baseApplication.getClassLoader());
        Assert.assertNotNull(baseApplication.getCommandProcessor());
        Assert.assertNotNull(baseApplication.getContext());
        Assert.assertNotNull(baseApplication.getRootCommandProvider());
        Assert.assertNotNull(baseApplication.getServiceProvider());
    }
}
