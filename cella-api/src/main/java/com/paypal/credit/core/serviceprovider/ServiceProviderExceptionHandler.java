package com.paypal.credit.core.serviceprovider;

/**
 * Interface type for handling exceptions. By implementing this interface the object will decide if it can
 * handle a specific exception and then attempt to handle it
 * 
 */
public interface ServiceProviderExceptionHandler
{	
	/**
	 * Determines if a specific exceptionk can be handled.  Usually implemented by looking at the class
	 * of the exception
	 * @param ex
	 * @return
	 */
	public boolean isExceptionHandled(Exception ex);
	
	/**
	 * Attempt to handle the exception
	 * @param ex
	 * @return True if the exception was handled in some way, false if it was not handled and the exception should then be thrown.
	 */
	public boolean handleException(Exception ex);

}
