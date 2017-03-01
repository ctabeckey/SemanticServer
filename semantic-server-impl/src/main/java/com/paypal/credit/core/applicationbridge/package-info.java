/**
 * This package contains the components that provide a bridge between
 * a client facade (REST, AMQP,SOAP, JMS, etc...) and the application.
 *
 * The application bridge declaration is an interface which is used to generate
 * code to call or to proxy calls to the command providers and the command processor.
 * Methods in the application bridge must be constrained to the Application Semantics
 * and the Application Domain Model. The application domain model must be available,
 * for class resolution, when the application bridge is generated (or proxied).
 *
 * The processor bridge is an interface only, the realization is generated at runtime.
 * The processor bridge has no requirements with regard to its class hierarchy.
 * The processor bridge must be an interface or an exception is thrown.
 * The methods declared in the processor bridge must be constrained to the application
 * domain model and semantics or an exception is thrown.
 *
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
package com.paypal.credit.core.applicationbridge;