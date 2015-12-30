package com.paypal.credit;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A Composition of Throwable, used to collect a number
 * of Throwable where many of them may occur and presenting
 * them individually would be inconvenient for the user.
 * @param <X> the type of the element in the composite
 */
public class CompositeExceptionDelegate<X extends Throwable> {
    /**
     * The list of exceptions that make up this instance
     */
    private final List<X> exceptions = new ArrayList<X>();

    /**
     * The VM line separator String, here for optimization only
     */
    private final String lineSeparator = System.getProperty("line.separator");

    /**
     * Create a composition of X instances
     *
     * @param xs an array of X instances
     */
    public CompositeExceptionDelegate(X... xs) {
        for (X x : xs) {
            addException(x);
        }
    }

    /**
     * Create a composition of X instances
     *
     * @param xs a Collection of MalformedURLException instances
     */
    public CompositeExceptionDelegate(Collection<X> xs) {
        if (xs != null) {
            for (X x : xs) {
                addException(x);       // call method to invoke validation
            }
        }
    }

    /**
     * return true if this instance has any child exceptions
     * @return TRUE iff this instance has any child exceptions
     */
    public boolean hasExceptions() {
        return this.exceptions.size() > 0;
    }

    /**
     * Add an exception to the collection
     *
     * @param x a MalformedURLException instance
     */
    public void addException(X x) {
        if (x != null) {
            exceptions.add(x);
        }
    }

    /**
     * generate and return a message from all the constituent exceptions
     * @return
     */
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        for (X x : exceptions) {
            if (sb.length() > 0) {
                sb.append(lineSeparator);
            }
            sb.append(x.getMessage());
        }
        return sb.toString();
    }

    /**
     * generate and return a localized message from all the constituent exceptions
     * @return
     */
    public String getLocalizedMessage() {
        StringBuilder sb = new StringBuilder();
        for (X x : exceptions) {
            if (sb.length() > 0) {
                sb.append(lineSeparator);
            }
            sb.append(x.getLocalizedMessage());
        }
        return sb.toString();
    }

    /**
     * Create and return a concatenation of the constituent exceptions
     * @return a String with all the exceptions error messages
     */
    @Override
    public String toString() {
        return this.getClass().getName() + "-" + getMessage();
    }

    /**
     * Print the stack traces for all the constituent exceptions
     * {@inheritDoc}
     */
    public void printStackTrace() {
        for (X x : exceptions) {
            x.printStackTrace();
        }
    }

    /**
     * Print the stack traces for all the constituent exceptions
     * {@inheritDoc}
     */
    public void printStackTrace(final PrintStream s) {
        for (X x : exceptions) {
            x.printStackTrace(s);
        }
    }

    /**
     * Print the stack traces for all the constituent exceptions
     * {@inheritDoc}
     */
    public void printStackTrace(final PrintWriter s) {
        for (X x : exceptions) {
            x.printStackTrace(s);
        }
    }
}
