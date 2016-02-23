import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by cbeckey on 2/19/16.
 */
public class ClassLoaderTest {
    private final static String JAR_FILE_PATH =
            "file:///Users/cbeckey/.m2/raptor2/commons-io/commons-io/2.4/commons-io-2.4.jar";

    @Test
    public void loadClass() throws MalformedURLException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        URLClassLoader cl = new URLClassLoader(new URL[]{new URL(JAR_FILE_PATH)});
        Assert.assertNotNull(cl);

        Class<?> clazz = cl.loadClass("org.apache.commons.io.input.NullInputStream");
        Assert.assertNotNull(clazz);

        Constructor<?> ctor = clazz.getConstructor(new Class[]{long.class});
        Object instance = ctor.newInstance(new Object[]{42});

        Assert.assertNotNull(instance);
    }
}
