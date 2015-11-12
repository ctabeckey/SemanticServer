package com.paypal.credit.core.processorbridge;

/**
 * "The bridge pattern is a design pattern used in software engineering which is meant to
 * 'decouple an abstraction from its implementation so that the two can vary independently'.
 * The bridge uses encapsulation, aggregation, and can use inheritance to separate responsibilities
 * into different classes."
 *
 * A marker interface for interfaces that are:
 * 1.) specific to an application
 * 2.) fronts the core command processor
 * 3.) usually implemented by generated code or a dynamic proxy
 */
public interface ProcessorBridge
{

}
