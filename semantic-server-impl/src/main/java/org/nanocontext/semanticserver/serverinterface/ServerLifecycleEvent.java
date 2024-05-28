/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created:
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package org.nanocontext.semanticserver.serverinterface;

import java.text.DateFormat;
import java.util.Date;

/**
 * A generic representation for server events.
 * This class is not specific to Tomcat and may be used
 * in application classes.
 * 
 */
public class ServerLifecycleEvent
{
	public enum EventType
	{
		INIT, BEFORE_START, START, AFTER_START, BEFORE_STOP, STOP, AFTER_STOP
	}
	
	private final EventType eventType;
	private final Date date;
	
	public ServerLifecycleEvent(EventType eventType)
	{
		this.eventType = eventType;
		this.date = new Date();
	}

	public EventType getEventType()
    {
    	return eventType;
    }

	public Date getDate()
    {
    	return date;
    }

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.getClass().getSimpleName());
		sb.append('-');
		sb.append(getEventType().toString());
		sb.append('@');
		sb.append( DateFormat.getDateTimeInstance().format(getDate()) );
		
		return sb.toString();
	}
	
	
}
