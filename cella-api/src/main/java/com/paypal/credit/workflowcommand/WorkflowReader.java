package com.paypal.credit.workflowcommand;

import com.paypal.credit.workflowcommand.workflow.WorkflowType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by cbeckey on 11/13/15.
 */
public class WorkflowReader {

    private static Unmarshaller createUnmarshaller() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance("com.paypal.credit.workflowcommand.workflow");
        Unmarshaller u = jc.createUnmarshaller();

        return u;
    }

    public static boolean exists(final InputStream inStream) {
        return inStream != null;
    }

    public static boolean exists(final URL workflowLocation) {
        URLConnection conn = null;
        try {
            conn = workflowLocation.openConnection();
            conn.connect();

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean exists(final File workflowFile) {
        return workflowFile.exists();
    }

    public static WorkflowType create(final InputStream inStream)
            throws JAXBException {
        Unmarshaller unmarshaller = createUnmarshaller();

        return (WorkflowType) unmarshaller.unmarshal(inStream);
    }

    public static WorkflowType create(final File workflowFile)
            throws JAXBException {
        Unmarshaller unmarshaller = createUnmarshaller();

        return (WorkflowType) unmarshaller.unmarshal(workflowFile);
    }

    public static WorkflowType create(final URL workflowLocation)
            throws JAXBException, MalformedURLException {
        Unmarshaller unmarshaller = createUnmarshaller();

        JAXBElement<WorkflowType> start = (JAXBElement<WorkflowType>) unmarshaller.unmarshal(workflowLocation);
        return (WorkflowType) start.getValue();
    }
}
