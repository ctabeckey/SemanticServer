package org.nanocontext.semanticserver.semanticserver.commandprocessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Utility class to provide access to commandprocessor facades without having to know the
 * commandprocessor applicationbridge implementation name.
 */
public class FacadeRouterUtility
{
	/**
	 * The class logger instance
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(FacadeRouterUtility.class);

	/**
	 *
	 */
	private static Map<Class<?>, ?> routers = new java.util.HashMap<Class<?>, Object>();

	/**
	 *
	 * @param interfaceClass
	 * @param <R>
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static <R> R getFacadeRouter(Class<R> interfaceClass)
	throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		synchronized(routers)
		{
			R cachedRouter = (R)routers.get(interfaceClass);
			if(cachedRouter != null)
				return (R)cachedRouter;
			
			String interfaceName = interfaceClass.getSimpleName();
			String packageName = interfaceClass.getPackage().getName();
			String implementationName = interfaceName + "Impl";
			String implementationClassName = packageName + "." + implementationName;
	
			// load the implementation class through the interface's class loader
			// else it probably will not find it
			Class<R> implementationClass = (Class<R>) Class.forName(implementationClassName, true, interfaceClass.getClassLoader());
			
			try
			{
				Method singletonAccessor = implementationClass.getMethod("getSingleton", (Class<?>[])null);
				
				// return the singleton accessor result (should be the singleton commandprocessor applicationbridge
				return (R) singletonAccessor.invoke(null, (Object[])null);
			} 
			catch (SecurityException x)
			{
				LOGGER.error("The applicationbridge commandprocessor implementation '{}' singleton accessor method is inaccessible.", implementationClassName);
				throw x;
			} 
			catch (NoSuchMethodException x) {
				LOGGER.warn("The implementation for applicationbridge commandprocessor '{}' has no singleton accessor method, i.e. public static getSingleton(), returning new instance", implementationClassName);
				return implementationClass.newInstance();
			}
		}
	}
}
