package com.paypal.credit.context.annotations;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.annotation.processing.Processor;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by cbeckey on 2/7/17.
 */
public class AnnotationProcessorTest {

    @DataProvider
    public Object[][] annotationProcessorTestData() {
        return new Object[][] {
                new Object[]{"singleBeanContext.xml", new String[]{"TestSubjectTwo.java"}},
                new Object[]{"doubleBeanContext.xml", new String[]{"TestSubject.java", "TestSubjectTwo.java"}},
        };
    }

    @Test(dataProvider = "annotationProcessorTestData")
    public void testAnnotationProcessor(final String outputContextFile, final String[] resourceNames) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        Processor processor = new AnnotationProcessor();
        List<Processor> processors = Collections.singletonList(processor);

        DiagnosticListener<JavaFileObject> diagnostic = new DiagnosticListener<JavaFileObject>() {
            @Override
            public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
                System.err.println(diagnostic.toString());
            }
        };

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(
                diagnostic,
                Locale.getDefault(),
                Charset.defaultCharset()
        );

        // prepare the source file(s) to compile
        List<File> sourceFileList = new ArrayList<File>();
        if (resourceNames != null) {
            for (String resourceName : resourceNames) {
                URL resourceLocation = this.getClass().getClassLoader().getResource(resourceName);
                Assert.assertNotNull(resourceLocation, String.format("Unable to find resource %s", resourceName));
                sourceFileList.add(new File(resourceLocation.getFile()));
            }

            List<String> options = Arrays.asList(String.format("-Acontext=%s", outputContextFile));

            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(sourceFileList);
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostic, options, null, compilationUnits);
            task.setProcessors(processors);

            Boolean result = task.call();
            Assert.assertTrue(result.booleanValue());
        }

        try {fileManager.close();}
        catch (IOException e) {}
    }
}