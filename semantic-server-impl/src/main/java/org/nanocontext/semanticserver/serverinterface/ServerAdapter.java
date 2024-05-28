package org.nanocontext.semanticserver.serverinterface;

/**
 * The interface that defines interaction with the host server, providing
 * access to lifecycle and authentication/authorization services
 * provided by the server.
 * 
 */
public interface ServerAdapter
{
	public abstract void addServerLifecycleListener(ServerLifecycleListener listener);

	public abstract void removeServerLifecycleListener(ServerLifecycleListener listener);

	/**
	 * 
	 * @param applicationEvent
	 */
	public abstract void serverLifecycleEvent(ServerLifecycleEvent applicationEvent);
	
}
