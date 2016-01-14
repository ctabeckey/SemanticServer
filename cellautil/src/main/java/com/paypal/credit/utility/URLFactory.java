package com.paypal.credit.utility;

import com.paypal.credit.utility.rsc.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.Map;

/**
 * Create a URL from the given String. If creating a URL throws a MalformedURLException
 * then try to create the URL with the resource handler (com.paypal.credit.protocol.rsc.Handler)
 */
public final class URLFactory {
    /**
     * The class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(URLFactory.class);

    public final static String CONNECTION_HANDLER_PACKAGES = "java.protocol.handler.pkgs";

    /**
     * The map of protocol labels to stream handlers
     */
    private static Map<String, Class<? extends URLStreamHandler>> connectionHandlers;

    /**
     * Register the URLStreamHandler that this factory provides.
     */
    static {
        connectionHandlers = new HashMap<String, Class<? extends URLStreamHandler>>();

        connectionHandlers.put("rsc", Handler.class);
    }

    /**
     * Constructor.
     */
    private URLFactory() {
        // default
    }

    /**
     * Use this to register the handlers, if the calls to create a URL cannot use
     * URLFactory.createURL() then this method (registerHandlers()) must be run before creating
     * a URL.
     */
    public static void registerHandlers() {
        final String originalValue = System.getProperty(CONNECTION_HANDLER_PACKAGES);

        StringBuilder newHandlerPackages = new StringBuilder();
        for (Map.Entry<String, Class<? extends URLStreamHandler>> entry : connectionHandlers.entrySet()) {
            if (newHandlerPackages.length() > 0) {
                newHandlerPackages.append('|');
            }
            String parentPackage = entry.getValue().getPackage().getName();
            int lastDot = parentPackage.lastIndexOf('.');
            parentPackage = parentPackage.substring(0, lastDot);

            newHandlerPackages.append(parentPackage);
        }

        String newValue = newHandlerPackages.toString();
        if ( originalValue != null )
        {
            newValue += "|" + originalValue;
        }

        System.setProperty( CONNECTION_HANDLER_PACKAGES, newValue );
    }

    /**
     * Create a URL from the given String. The URL may use protocols that are NOT
     * defined using the Java defined search mechanism. This factory currently
     * includes an implemenattion for the "rsc" protocol, which finds resources
     * using the class loaders.
     *
     * @param urlAsString the String from which to create a URL.
     * @return a URL built from the given String
     * @throws MalformedURLException if the URL cannot be built
     */
    public static URL create(String urlAsString)
    throws MalformedURLException {
        if (urlAsString == null) {
            return null;
        }
        if (urlAsString.isEmpty()) {
            throw new MalformedURLException("Zero length URL is not valid.");
        }
        URL url = null;
        try {
            url = new URL(urlAsString);
        } catch (MalformedURLException muX) {
            url = createWithResourceConnectionHandler(urlAsString);
        }

        if (url == null) {
            throw new MalformedURLException(String.format("No valid URLStreamHandler registered for '%s'.", urlAsString));
        }

        return url;
    }

    /**
     * If the URL was not a valid URL format then try creating the URL with the resource connection handler.
     * @param urlAsString the String from which to create a URL.
     * @return a URL built from the given String
     * @throws MalformedURLException if the URL cannot be built
     */
    @SuppressWarnings("PMD.PreserveStackTrace")
    private static URL createWithResourceConnectionHandler(final String urlAsString) throws MalformedURLException {
        String scheme;

        try {
            URI uri = new URI(urlAsString);
            scheme = uri.getScheme();
        } catch (URISyntaxException urisX) {
            throw new MalformedURLException(String.format("Unable to parse '%s' as a URI to extract scheme.", urlAsString));
        }

        URL url = null;
        for (Map.Entry<String, Class<? extends URLStreamHandler>> connectionHandlerMapEntry : connectionHandlers.entrySet()) {
            if (connectionHandlerMapEntry.getKey().equals(scheme)) {
                try {
                    url = new URL(null, urlAsString, connectionHandlerMapEntry.getValue().newInstance());
                } catch (InstantiationException | IllegalAccessException x) {
                    url = null;
                    LOGGER.info(String.format("Unable to create instance of '%s' for URL '%s'.",
                            connectionHandlerMapEntry.getValue().getName(), urlAsString));
                }
            }
        }
        return url;
    }

}
