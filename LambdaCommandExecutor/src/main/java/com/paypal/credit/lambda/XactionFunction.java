package com.paypal.credit.lambda;

import com.amazonaws.services.lambda.runtime.Context;

/**
 * Created by cbeckey on 2/1/16.
 */
public class XactionFunction {

    public String process(String msg, Context context) {
        return String.format("Hello World %s", msg);
    }

}
