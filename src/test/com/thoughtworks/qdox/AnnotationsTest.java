package com.thoughtworks.qdox;

import com.thoughtworks.qdox.model.JavaClass;
import java.io.StringReader;
import junit.framework.TestCase;

public class AnnotationsTest extends TestCase {

    private JavaDocBuilder builder = new JavaDocBuilder();

    public void testShouldIgnoreSimpleClassAnnotation() {
        String source = "" 
            + "@Fnord\n"
            + "public interface Foo extends Bar {}\n";

        builder.addSource(new StringReader(source));
        assertEquals("Foo", builder.getClassByName("Foo").getName());
    }

    public void testShouldIgnoreSimpleMethodAnnotation() {
        String source = ""
            + "public class X {\n"
            + "    @Fnord public void snort() {}\n"
            + "}\n";

        builder.addSource(new StringReader(source));
        JavaClass fooClass = builder.getClassByName("X");
        assertEquals("X", fooClass.getName());
        assertEquals(1, fooClass.getMethods().length);
        assertEquals("snort", fooClass.getMethods()[0].getName());
    }

    public void testShouldIgnoreMethodParameterAnnotation() {
        String source = ""
            + "public class X {\n"
            + "    public void setX(@name String x) {}\n"
            + "}\n";

        builder.addSource(new StringReader(source));
        JavaClass fooClass = builder.getClassByName("X");
        assertEquals("X", fooClass.getName());
        assertEquals(1, fooClass.getMethods().length);
        assertEquals("setX", fooClass.getMethods()[0].getName());
    }

    public void testShouldIgnoreComplexClassAnnotations() {
        String source = "" 
            + "@Fnord(pi = 3.14, e = m*c*c)\n"
            + "public interface Foo extends Bar {\n"
            + "  @Fnord(pi = 3.14, e = m*c*c)\n"
            + "  void doStuff() { }\n"
            + "}\n";

        builder.addSource(new StringReader(source));
        assertEquals("Foo", builder.getClassByName("Foo").getName());
    }

    public void testShouldIgnoreSingleMemberClassAnnotations() {
        String source = "" 
            + "@Fnord(\"xyz\")\n"
            + "@Blat(Math.MAXINT)\n"
            + "public interface Foo extends Bar {\n"
            + "  @Fnord(\"xyz\")\n"
            + "  @Blat(Math.MAXINT)\n"
            + "  void doStuff() { }\n"
            + "}\n";

        builder.addSource(new StringReader(source));
        assertEquals("Foo", builder.getClassByName("Foo").getName());
    }

    public void testShouldIgnoreArrayValuedSingleMemberClassAnnotations() {
        String source = "" 
            + "@Endorsers({\"Children\", \"Unscrupulous dentists\"})\n"
            + "public class Lollipop {\n"
            + "  @Cheese({\"Edam\", \"Gruyere\"})\n"
            + "  void doStuff() { }\n"
            + "}\n";

        builder.addSource(new StringReader(source));
        assertNotNull(builder.getClassByName("Lollipop"));
    }

    public void testShouldIgnoreComplexSingleMemberClassAnnotations() {
        String source = "" 
            + "@Author(@Name(first = \"Joe\", last = \"Hacker\"))\n" // I won't take it personally! ;) -joe
            + "public class BitTwiddle {\n"
            + "  @Author(@Name(first = \"Joe\", last = \"Hacker\"))\n"
            + "  void doStuff() { }\n"
            + "}\n";

        builder.addSource(new StringReader(source));
        assertNotNull(builder.getClassByName("BitTwiddle"));
    }

    public void testShouldIgnoreAnnotationDeclaration() {
        String source = "" 
            + "public @interface Note {\n"
            + "    String text;\n"
            + "}\n";

        builder.addSource(new StringReader(source));
        assertEquals(0, builder.getClasses().length);
    }

    public void testShouldIgnoreAnnotationWithClassType() {
        String source = "" 
            + "@Fnord(String.class)\n"
            + "public interface Foo extends Bar {}\n";

        builder.addSource(new StringReader(source));
        assertEquals("Foo", builder.getClassByName("Foo").getName());
    }


    // from QDOX-97
    public void testShouldIgnoreAttributeAnnotation() {
        String source = ""
            + "public interface Foo {\n"
            + "   String echo(@WebParam java.lang.String msg);\n"
            + "}\n";
        builder.addSource(new StringReader(source));
        assertEquals("Foo", builder.getClassByName("Foo").getName());
    }




}
