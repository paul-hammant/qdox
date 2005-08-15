package com.thoughtworks.qdox;

import junit.framework.TestCase;

import java.io.StringReader;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.Type;

public class ClassResolutionTest extends TestCase {

    public void testNestedClassesResolvedAcrossPackageBoundaries() {

        // input sources
        String source1 = ""
                + "package package1;"
                + "public class Class1 {"
                + " public static final class NestedClass {}"
                + "}";

        String source2 = ""
                + "package package2;"
                + "import package1.Class1;"
                + "public class Class2 {"
                + " public void doStuff(Class1.NestedClass arg) {}"
                + "}";

        // parse
        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSource(new StringReader(source1));
        builder.addSource(new StringReader(source2));

        // find the parameter
        JavaClass class2 = builder.getClassByName("package2.Class2");
        JavaMethod method = class2.getMethods()[0];
        JavaParameter parameter = method.getParameters()[0];
        Type type = parameter.getType();

        // verify
        assertEquals("Should include fully qualified name", "package1.Class1$NestedClass", type.getValue());
    }
    
    public void testSurvivesStaticImports() {

        // input sources
        String source = ""
                + "package package2;"
                + "import static package1.Class1.VALUE;"
                + "public class Class2 {"
                + " public void doStuff(String arg) {}"
                + "}";

        // parse
        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSource(new StringReader(source));

        // find the parameter
        JavaClass class2 = builder.getClassByName("package2.Class2");
        assertNotNull(class2);
    }
}
