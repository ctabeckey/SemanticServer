package com.paypal.credit.core.semantics;

import com.paypal.credit.core.utility.ParameterCheckUtility;
import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;

/**
 * This class enumerates the packages in which a model object class
 * must reside.
 * 
 */
public class ModelVocabulary
{
	private final String[] packageNames;
    private final ClassLoader classLoader;
	
	ModelVocabulary(String... packageNames)
            throws CoreRouterSemanticsException {

        this(null, packageNames);
    }

    ModelVocabulary(ClassLoader classLoader, String... packageNames)
            throws CoreRouterSemanticsException {
        this.classLoader = classLoader == null ? ModelVocabulary.class.getClassLoader() : classLoader;
        ParameterCheckUtility.checkParameterNotNull(packageNames, "packageNames");
		this.packageNames = new String[packageNames.length];
        System.arraycopy(packageNames, 0, this.packageNames, 0, packageNames.length);
	}

	/**
	 * 
	 * @param simpleName
	 * @return
	 */
	public Class<?> getClass(String simpleName)
	{
		for(String packageName : this.packageNames)
		{
			String objectClassName =
                    packageName == null ? simpleName : packageName + "." + simpleName;
			try
			{
                Class<?> result = this.classLoader.loadClass(objectClassName);
				return result;      // note that this will not get executed if the class was found and loaded
			} 
			catch (ClassNotFoundException x)
			{
				// do nothing, this is just part of the searching
			}
		}
		
		return null;
	}
}
