/**
 * This package contains the components that provide
 * a bridge between a processorbridge (REST, AMQP,SOAP, JMS, etc...) and the
 * core.
 * The processorbridge must declare an interface that extends ProcessorBridge,
 * which is used to generate or proxy to the core. Methods in the ProcessorBridge
 * derivation must be declared in the RouterMethodSemantics and the Application
 * object model must be available for class resolution when the ProcessBridge
 * is generated (or proxied).
 */
package com.paypal.credit.core.processorbridge;