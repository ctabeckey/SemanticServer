package com.paypal.credit.xlator;

import io.undertow.Undertow;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;

import java.net.InetAddress;

/**
 * Created by cbeckey on 2/2/16.
 */
public class RestXlatorMain {

    // ===========================================================================================
    // Static Members
    // ===========================================================================================
    public static Log LOGGER = LogFactory.getLog(RestXlatorMain.class);

    /**
     *
     * @param argv
     */
    public static void main(String[] argv) {
        RestXlatorMain main = new RestXlatorMain();
        try {
            main.start();

        } catch(Exception x) {
            LOGGER.error(x.getMessage(), x);
        }
    }

    // ===========================================================================================
    // Instance Members
    // ===========================================================================================

    private UndertowJaxrsServer server;

    /**
     *
     * @throws Exception
     */
    public void start() throws Exception
    {
        String hostName = InetAddress.getLocalHost().getHostName();
        LOGGER.info(String.format("Starting service on host %s", hostName));
        server = new UndertowJaxrsServer();
        Undertow.Builder builder = Undertow.builder()
                .addHttpListener(8080, hostName)
                .addHttpListener(8080, "localhost")
                .addHttpListener(80, hostName)          // NOTE: opening port 80 requires 'root' privilege
                .addHttpListener(80, "localhost")       // NOTE: opening port 80 requires 'root' privilege
        ;
        server.start(builder);
        server.deploy(StatementXlatorApplication.class);
    }

    public void stop() throws Exception
    {
        server.stop();
    }
}
