package com.paypal.credit.core.commandprocessor.exceptions;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 */
public class ProcessorBridgeInstantiationCompositeException
extends ProcessorBridgeInstantiationException
{
	private static final long serialVersionUID = 1L;

	private List<ProcessorBridgeInstantiationException> exceptions =
		new ArrayList<ProcessorBridgeInstantiationException>();
	
	public ProcessorBridgeInstantiationCompositeException()
	{
		
	}

	public void add(ProcessorBridgeInstantiationException x)
	{
		exceptions.add(x);
	}
	
	public int getCount()
	{
		return exceptions.size();
	}
	
	@Override
	public String getLocalizedMessage() 
	{
		return getMessage(true);
	}

	@Override
	public String getMessage() 
	{
		return getMessage(false);
	}

	private String getMessage(boolean localized)
	{
		if(getCount() == 0)
			return null;
		
		StringBuffer sb = new StringBuffer();
		
		for(ProcessorBridgeInstantiationException x : exceptions)
		{
			if(sb.length() > 0)
				sb.append(System.getProperty("line.separator"));
			
			sb.append(localized ? x.getLocalizedMessage() : x.getMessage());
		}
		
		return sb.toString();
		
	}
	
}
