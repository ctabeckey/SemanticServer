package com.paypal.credit.utility.rsc;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * Note that this class is in the "rsc" package so that it does not conflict with the
 * Spring "resource" protocol handler.
 */
public class Handler
        extends URLStreamHandler {
    @Override
    public URLConnection openConnection(final URL u)
            throws IOException {
        String protocol = u.getProtocol();
        if (!"rsc".equals(protocol)) {
            throw new IOException(String.format("Unable to open connection for URL [%s], protocol [%s] not supported.", u.toString(), protocol));
        }
        String path = u.getPath();
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        URL resourceUrl = classLoader.getResource(path);
        //
        if (resourceUrl == null) {
            classLoader = this.getClass().getClassLoader();
            if (classLoader == ClassLoader.getSystemClassLoader()) {
                throw new IOException(String.format("unable to load resource from '%s, context and system ClassLoader are the same'.", path));
            }
            resourceUrl = classLoader.getResource(path);
            if (resourceUrl == null) {
                throw new IOException(String.format("Checked System and Context ClassLoader, but unable to load resource from '%s'.", path));
            }
        }
        return resourceUrl.openConnection();
    }
}
