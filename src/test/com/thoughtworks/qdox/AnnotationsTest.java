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

    public void testShouldIgnoreComplexClassAnnotations() {
        String source = "" 
            + "@Fnord(pi = 3.14, e = m*c*c)\n"
            + "public interface Foo extends Bar {}\n";

        builder.addSource(new StringReader(source));
        assertEquals("Foo", builder.getClassByName("Foo").getName());
    }

    public void testShouldIgnoreSingleMemberClassAnnotations() {
        String source = "" 
            + "@Fnord(\"xyz\")\n"
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

    public void testShouldIgnoreAnnotationDeclaration() {
        String source = "" 
            + "public @interface Note {\n"
            + "    String text;\n"
            + "}\n";

        builder.addSource(new StringReader(source));
        assertEquals(0, builder.getClasses().length);
    }
    
}
