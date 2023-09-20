package com.thoughtworks.qdox;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

public class ClassResolutionTest {

    @Test
    public void testNestedClassesResolvedAcrossPackageBoundaries()
    {

        // input sources
        String source1 =
            "" + "package package1;" + "public class Class1 {" + " public static final class NestedClass {}" + "}";

        String source2 =
            "" + "package package2;" + "import package1.Class1;" + "public class Class2 {"
                + " public void doStuff(Class1.NestedClass arg) {}" + "}";

        // parse
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSource( new StringReader( source1 ) );
        builder.addSource( new StringReader( source2 ) );

        // find the parameter
        JavaClass class2 = builder.getClassByName( "package2.Class2" );
        JavaMethod method = class2.getMethods().get( 0 );
        JavaParameter parameter = method.getParameters().get( 0 );
        JavaType type = parameter.getType();

        // verify
        Assertions.assertEquals("package1.Class1$NestedClass", type.getBinaryName(), "Should include fully qualified name");
        Assertions.assertEquals("package1.Class1.NestedClass", type.getFullyQualifiedName(), "Should include fully qualified name");
    }

    @Test
    public void testSurvivesStaticImports()
    {

        // input sources
        String source =
            "" + "package package2;" + "import static package1.Class1.VALUE;" + "public class Class2 {"
                + " public void doStuff(String arg) {}" + "}";

        // parse
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSource( new StringReader( source ) );

        // find the parameter
        JavaClass class2 = builder.getClassByName( "package2.Class2" );
        Assertions.assertNotNull(class2);
    }

    @Test
    public void testAnonymousClass()
    {
        JavaProjectBuilder builder = new JavaProjectBuilder();

        String source =
            "" + "public class AnimatedAlgorithm {\n"
                + "    private SelectionListener mySelectionListener = new SelectionListenerAdapter() {\n"
                + "        public void selectionEvent() {\n"
                + "            for (int i = 0; i < recalcers.size(); i++) {\n" + "              int something = 5;"
                + "            }\n" + "        }\n" + "    };\n" + "}";

        builder.addSource( new StringReader( source ) );
    }

    // from QDOX-86
    @Test
    public void testInnerClassInMethod()
    {
        JavaProjectBuilder builder = new JavaProjectBuilder();
        String source =
            "package some.pack;\n" + "class Test {\n" + "void some(Inner.Inner2 a) {}\n" + "static interface Inner {\n"
                + "static interface Inner2 { }\n" + "}\n" + "}";
        builder.addSource( new StringReader( source ) );
        JavaMethod method = builder.getClassByName( "some.pack.Test" ).getMethods().get( 0 );
        JavaParameter parameter = method.getParameters().get( 0 );
        Assertions.assertEquals("some.pack.Test$Inner$Inner2", parameter.getJavaClass().getBinaryName());
        Assertions.assertEquals("some.pack.Test$Inner$Inner2", parameter.getType().getBinaryName());
        Assertions.assertEquals("some.pack.Test$Inner$Inner2", parameter.getBinaryName());
        Assertions.assertEquals("some.pack.Test.Inner.Inner2", parameter.getJavaClass().getFullyQualifiedName());
        Assertions.assertEquals("some.pack.Test.Inner.Inner2", parameter.getType().getFullyQualifiedName());
        Assertions.assertEquals("some.pack.Test.Inner.Inner2", parameter.getFullyQualifiedName());
    }

    @Test
    public void testIsAWithPrimitives()
    {
        JavaProjectBuilder builder = new JavaProjectBuilder();
        String source = "class Foo { public byte[] bar() { return null; } }";
        builder.addSource( new StringReader( source ) );
        JavaMethod method = builder.getClassByName("Foo").getMethods().get(0);
        JavaClass returns = method.getReturns();
        Assertions.assertFalse(returns.isA(builder.getClassByName("java.lang.Object")));
    }
}
