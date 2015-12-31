package com.paypal.credit.workflow;

/**
 * Created by cbeckey on 12/29/15.
 */
public enum GraphNodeType {
    Start ("start"),
    End ("end"),
    SerialStart ("serial-start"),
    SerialEnd ("serial-end"),
    ParallelStart ("parallel-start"),
    ParallelEnd ("parallel-end"),
    ConditionalStart ("conditional-start"),
    ConditionalEnd ("conditional-end"),
    Business ("business");

    private final String displayKey;
    GraphNodeType(String displayKey) {
        this.displayKey = displayKey;
    }

    public String getDisplayKey() {
        return this.displayKey;
    }
}
