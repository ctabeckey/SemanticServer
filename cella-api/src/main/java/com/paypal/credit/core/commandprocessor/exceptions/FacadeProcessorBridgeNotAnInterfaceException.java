package com.paypal.credit.core.commandprocessor.exceptions;

/**
 *
 */
public class FacadeProcessorBridgeNotAnInterfaceException
extends ProcessorBridgeInstantiationException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static String intfNameToken = "%1";
	public final static String msg = "The class '%1' is not an interface and cannot be use to instantiate a commandprocessor processorbridge.";

	/**
	 * 
	 * @param intf
	 */
	public FacadeProcessorBridgeNotAnInterfaceException(Class<?> intf)
	{
		super(buildMessage(intf));
	}

	/**
	 * 
	 * @param intf
	 * @return
	 */
	private static String buildMessage(Class<?> intf) 
	{
		StringBuilder message = new StringBuilder();
		String intfName = intf == null ? "<null interface>" : intf.getName();
		
		int tokenIndex = msg.indexOf(intfNameToken);
		int tokenLength = intfNameToken.length();
		
		if(tokenIndex >= 0)
		{
			message.append(msg.substring(0, tokenIndex));
			message.append(intfName);
			if(tokenIndex + tokenLength <= msg.length())
				message.append(msg.substring(tokenIndex + tokenLength));
			
			return message.toString();
		}
		
		return msg;
	}
}
