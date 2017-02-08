package com.paypal.credit.context.exceptions;

import com.paypal.credit.utility.CompositeExceptionDelegate;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Created by cbeckey on 2/4/16.
 */
public class CompositeContextInitializationException extends ContextInitializationException {
    private CompositeExceptionDelegate<ContextInitializationException> compositeDelegate =
            new CompositeExceptionDelegate<>();

    public void add(ContextInitializationException ciX) {
        compositeDelegate.addException(ciX);
    }

    @Override
    public String getMessage() {
        return compositeDelegate.getMessage();
    }

    @Override
    public String getLocalizedMessage() {
        return compositeDelegate.getLocalizedMessage();
    }

    @Override
    public String toString() {
        return compositeDelegate.toString();
    }


    @Override
    public void printStackTrace() {
        compositeDelegate.printStackTrace();
    }

    @Override
    public void printStackTrace(final PrintStream s) {
        compositeDelegate.printStackTrace(s);
    }

    @Override
    public void printStackTrace(final PrintWriter s) {
        compositeDelegate.printStackTrace(s);
    }
}
