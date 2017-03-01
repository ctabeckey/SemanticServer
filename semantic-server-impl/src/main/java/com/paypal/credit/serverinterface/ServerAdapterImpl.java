package com.paypal.credit.serverinterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * The central gateway of the server agnostic mechanism.  Components of the semantic server
 * register interest in events here.  Server-specific lifecycle classes send messages 
 * to this singleton.
 *
 * This class also provides access to authentication/authorization services.
 * 
 */
public class ServerAdapterImpl 
implements ServerAdapter
{
	private static ServerAdapterImpl singleton = null;
	public static synchronized ServerAdapter getSingleton()
	{
		if(singleton == null)
			singleton = new ServerAdapterImpl();
		
		return singleton;
	}
	
	private Set<ServerLifecycleListener> serverEventListeners = new HashSet<ServerLifecycleListener>();

	private final static Logger LOGGER = LoggerFactory.getLogger(ServerAdapterImpl.class);

	/**
	 * 
	 */
	private ServerAdapterImpl()
	{ }
	
	

	/* (non-Javadoc)
	 * @see ext.domain.server.ServerAdapter#addServerLifecycleListener(ext.domain.server.ServerLifecycleListener)
	 */
	public void addServerLifecycleListener(ServerLifecycleListener listener)
	{
		LOGGER.info("Adding ServerLifeCycleListener '" + listener.toString() + "'.");
		serverEventListeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see ext.domain.server.ServerAdapter#removeServerLifecycleListener(ext.domain.server.ServerLifecycleListener)
	 */
	public void removeServerLifecycleListener(ServerLifecycleListener listener)
	{
		LOGGER.info("Removing ServerLifeCycleListener '" + listener.toString() + "'.");
		serverEventListeners.remove(listener);
	}

	private boolean serverStarted = false;
	/**
	 * Called from the server-specific adapters to notify us of a server event.
	 * @param applicationEvent
	 */
	public synchronized void serverLifecycleEvent(ServerLifecycleEvent applicationEvent)
	{
		if( applicationEvent.getEventType() == ServerLifecycleEvent.EventType.AFTER_START && !serverStarted)
		{
			serverStarted = true;
		}
		
		if( applicationEvent.getEventType() == ServerLifecycleEvent.EventType.AFTER_STOP && serverStarted)
		{
			serverStarted = false;
		}
		
		notifyServerLifecycleListeners(applicationEvent);
	}

	/**
	 * 
	 * @param event
	 */
	private void notifyServerLifecycleListeners(ServerLifecycleEvent event)
	{
		LOGGER.info("Notifying server lifecycle listeners, event is '" + event.toString() + "'.");
		for(ServerLifecycleListener listener : serverEventListeners)
			listener.serverLifecycleEvent(event);
	}
}
