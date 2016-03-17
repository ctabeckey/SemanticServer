package com.paypal.credit.context;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Holds references to late-loaded artifacts.
 */
class ArtifactHolder {
    private final String identifier;
    private final ClassLoader parentClassLoader;
    private URLClassLoader classLoader;
    private final URI resourceIdentifier;
    private boolean loaded = false;

    private final ReentrantLock loadedLock = new ReentrantLock();
    private final Condition notLoaded  = loadedLock.newCondition();

    /**
     *
     * @param identifier
     * @param resourceIdentifier
     * @param parentClassLoader
     * @throws MalformedURLException
     */
    public ArtifactHolder(final String identifier, final URI resourceIdentifier, final ClassLoader parentClassLoader)
            throws MalformedURLException {
        this.identifier = identifier;
        this.resourceIdentifier = resourceIdentifier;
        this.parentClassLoader = parentClassLoader;

        URL artifactLocation = this.resourceIdentifier.toURL();

        if ("http".equals(artifactLocation.getProtocol()) || "https".equals(artifactLocation.getProtocol())) {
            String tempPath = System.getenv("TMPDIR");
            File tempDirectory = new File(tempPath);
            System.out.println("Application "
                    + (tempDirectory.canRead() ? "CAN read" : "CANNOT read")
                    + " and "
                    + (tempDirectory.canWrite() ? "CAN write" : "CANNOT write")
                    + " to "
                    + tempPath
            );
        }

        this.classLoader = URLClassLoader.newInstance(new URL[]{artifactLocation}); // , parentClassLoader, null
    }


    public String getIdentifier() {
        return identifier;
    }

    private void dumpManifest(PrintStream out) {
        try (InputStreamReader isr = new InputStreamReader(
                this.classLoader.getResourceAsStream("META-INF/MANIFEST.MF"))) {
            if (isr != null) {
                try (LineNumberReader reader = new LineNumberReader(isr)) {

                    String line = null;
                    for (line = reader.readLine(); line != null; line = reader.readLine()) {
                        out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException x) {
            x.printStackTrace();
        }

    }

    /**
     *
     * @return
     */
    public URLClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtifactHolder that = (ArtifactHolder) o;
        return Objects.equals(identifier, that.identifier) &&
                Objects.equals(resourceIdentifier, that.resourceIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, resourceIdentifier);
    }
}
