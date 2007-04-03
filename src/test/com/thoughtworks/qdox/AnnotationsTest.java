package com.thoughtworks.qdox;

import java.io.StringReader;

import junit.framework.TestCase;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.Type;

public class AnnotationsTest extends TestCase {

    private JavaDocBuilder builder;
    public AnnotationsTest()
    {
        builder = new JavaDocBuilder();
        //builder.setDebugLexer( true );
        //builder.setDebugParser( true );
    }

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
            + "    String field = new String( \"hey\" );\n"
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
        String source = "" /** @hey=\"yo\" someval = \"yep\" */
            + "@ Endorsers({(\"Children\"), \"Unscrupulous dentists\"})\n"
            + "public class Lollipop {\n"
            + "  @Cheese( hey=@ano({\"Edam\", \"Gruyere\", 2}), t=5.5f, c=4)\n"
            + "  void doStuff() { }\n"
            + "}\n";

        builder.addSource(new StringReader(source));
        assertNotNull(builder.getClassByName("Lollipop"));
    }

    public void testShouldIgnoreComplexSingleMemberClassAnnotations() {
        String source = "" 
            + "@Author(@Name(first = \"Joe\", last = true))\n" // I won't take it personally! ;) -joe
            + "public class BitTwiddle {\n"
            + "  @Author(@Name(first = \'c\', last = 2.5e3f))\n"
            + "  void doStuff() { }\n"
            + "}\n";

        builder.addSource(new StringReader(source));
        assertNotNull(builder.getClassByName("BitTwiddle"));
        assertNotNull( builder.getClassByName("BitTwiddle").getAnnotations()[0].getNamedParameter("value") );
        assertEquals( "Author", builder.getClassByName("BitTwiddle")
        	.getMethodBySignature("doStuff", new Type[] {})
        		.getAnnotations()[0].getType().getValue() );
    }

    public void testShouldIgnoreAnnotationDeclaration() {
        String source = "package org.jabba;\n"
        	+ "@MyAnno\n"
            + "public @interface Note {\n"
            + "    String text;\n"
            + "}\n";

        builder.addSource(new StringReader(source));
        assertEquals(1, builder.getClasses().length);
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

    // from QDOX-101
    public void todo_testShouldNotChokeOnDoubleAttributeAnnotationAndGenerics() {
        String source = ""
            + "public class Person {\n" +
              "    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)\n" +
              "    @XmlID\n" +
              "    protected String name;\n" +
              "    public List<String> getAddress() {\n" +
              "        return address;\n" +
              "    }" +
              "}\n";
        builder.addSource(new StringReader(source));
        assertEquals("Person", builder.getClassByName("Person").getName());
    }

    // from QDOX-108
    public void testFQNAnnotations() {
        String source = "" 
            + "@com.mycompany.Fnord(a=1)\n"
            + "public interface Foo extends Bar {}\n";

        builder.addSource(new StringReader(source));
        assertEquals("Foo", builder.getClassByName("Foo").getName());
    }
}
