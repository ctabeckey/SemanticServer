package com.paypal.credit.core.semantics;

import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;
import com.paypal.utility.ParameterCheckUtility;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * This class enumerates the packages in which a model object class
 * must reside.
 * 
 */
public class ModelVocabularyImpl implements ModelVocabulary {
	private final String[] packageNames;
    private final ClassLoader classLoader;

    /**
     *
     * @param packageNames
     * @return
     * @throws CoreRouterSemanticsException
     */
	public static ModelVocabularyImpl create(@NotNull final String packageNames)
			throws CoreRouterSemanticsException {

		return create(null, packageNames.split(","));
	}

    /**
     *
     * @param packageNames
     * @return
     * @throws CoreRouterSemanticsException
     */
    public static ModelVocabularyImpl create(@NotNull @Min(1) final String[] packageNames)
            throws CoreRouterSemanticsException {
        return create(null, packageNames);
    }

    /**
     *
     * @param classLoader
     * @param packageNames
     * @return
     * @throws CoreRouterSemanticsException
     */
    public static ModelVocabularyImpl create(final ClassLoader classLoader, final @NotNull @Min(1) String[] packageNames)
            throws CoreRouterSemanticsException {
        ParameterCheckUtility.checkParameterNotNull(packageNames, "packageNames");

        ClassLoader effectiveClassLoader = classLoader == null ? ModelVocabularyImpl.class.getClassLoader() : classLoader;
        String[] packages = new String[packageNames.length];
        for (int n=0; n<packageNames.length; ++n) {
        	packages[n] = packageNames[n].trim();
		}

        return new ModelVocabularyImpl(effectiveClassLoader, packages);
    }

	/**
	 *
	 * @param classLoader
	 * @param packageNames
	 * @throws CoreRouterSemanticsException
	 */
    private ModelVocabularyImpl(ClassLoader classLoader, String... packageNames)
            throws CoreRouterSemanticsException {
        this.classLoader = classLoader;
		this.packageNames = packageNames;
	}

	/**
	 * 
	 * @param simpleName
	 * @return
	 */
	@Override
	public Class<?> getClass(String simpleName)
	{
		for(String packageName : this.packageNames)
		{
			String objectClassName =
                    packageName == null ? simpleName : packageName + "." + simpleName;
			try
			{
                Class<?> result = this.classLoader.loadClass(objectClassName);
				return result;      // note that this will not get executed if the class was not found and loaded
			} 
			catch (ClassNotFoundException x)
			{
				// do nothing, this is just part of the searching
			}
		}
		
		return null;
	}
}
