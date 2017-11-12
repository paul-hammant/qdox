package com.thoughtworks.qdox;

import java.io.StringReader;
import java.util.Collections;

import junit.framework.TestCase;

import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.expression.FieldRef;

public class AnnotationsTest
    extends TestCase
{

    private JavaProjectBuilder builder;

    public AnnotationsTest()
    {
        builder = new JavaProjectBuilder();
        // builder.setDebugLexer( true );
        // builder.setDebugParser( true );
    }

    public void testShouldIgnoreSimpleClassAnnotation()
    {
        String source = "" + "@Fnord\n" + "public interface Foo extends Bar {}\n";

        builder.addSource( new StringReader( source ) );
        assertEquals( "Foo", builder.getClassByName( "Foo" ).getName() );
    }

    public void testShouldIgnoreSimpleMethodAnnotation()
    {
        String source = "" + "public class X {\n" + "    @Fnord public void snort() {}\n" + "}\n";

        builder.addSource( new StringReader( source ) );
        JavaClass fooClass = builder.getClassByName( "X" );
        assertEquals( "X", fooClass.getName() );
        assertEquals( 1, fooClass.getMethods().size() );
        assertEquals( "snort", fooClass.getMethods().get( 0 ).getName() );
    }

    public void testShouldIgnoreMethodParameterAnnotation()
    {
        String source =
            "" + "public class X {\n" + "    String field = new String( \"hey\" );\n"
                + "    public void setX(@name String x) {}\n" + "}\n";

        builder.addSource( new StringReader( source ) );
        JavaClass fooClass = builder.getClassByName( "X" );
        assertEquals( "X", fooClass.getName() );
        assertEquals( 1, fooClass.getMethods().size() );
        assertEquals( "setX", fooClass.getMethods().get( 0 ).getName() );
    }

    public void testShouldIgnoreComplexClassAnnotations()
    {
        String source =
            "" + "@Fnord(pi = 3.14, e = m*c*c)\n" + "public interface Foo extends Bar {\n"
                + "  @Fnord(pi = 3.14, e = m*c*c)\n" + "  void doStuff() { }\n" + "}\n";

        builder.addSource( new StringReader( source ) );
        assertEquals( "Foo", builder.getClassByName( "Foo" ).getName() );
    }

    public void testShouldIgnoreSingleMemberClassAnnotations()
    {
        String source =
            "" + "@Fnord(\"xyz\")\n" + "@Blat(Math.MAXINT)\n" + "public interface Foo extends Bar {\n"
                + "  @Fnord(\"xyz\")\n" + "  @Blat(Math.MAXINT)\n" + "  void doStuff() { }\n" + "}\n";

        builder.addSource( new StringReader( source ) );
        assertEquals( "Foo", builder.getClassByName( "Foo" ).getName() );
    }

    public void testShouldIgnoreArrayValuedSingleMemberClassAnnotations()
    {
        String source =
            "/** @hey=\"yo\" someval = \"yep\" */"
            + "@ Endorsers({(\"Children\"), \"Unscrupulous dentists\"})\n" + "public class Lollipop {\n"
                + "  @Cheese( hey=@ano({\"Edam\", \"Gruyere\", 2}), t=5.5f, c=4)\n" + "  void doStuff() { }\n" + "}\n";

        builder.addSource( new StringReader( source ) );
        assertNotNull( builder.getClassByName( "Lollipop" ) );
    }

    public void testShouldIgnoreComplexSingleMemberClassAnnotations()
    {
        String source =
            ""
                + "@Author(@Name(first = \"Joe\", last = true))\n" // I won't take it personally! ;) -joe
                + "public class BitTwiddle {\n" + "  @Author(@Name(first = \'c\', last = 2.5e3f))\n"
                + "  void doStuff() { }\n" + "}\n";

        builder.addSource( new StringReader( source ) );
        assertNotNull( builder.getClassByName( "BitTwiddle" ) );
        assertNotNull( builder.getClassByName( "BitTwiddle" ).getAnnotations().get( 0 ).getNamedParameter( "value" ) );
        assertEquals( "Author",
                      builder.getClassByName( "BitTwiddle" ).getMethodBySignature( "doStuff", Collections.<JavaType>emptyList() ).getAnnotations().get( 0 ).getType().getValue() );
    }

    public void testShouldIgnoreAnnotationDeclaration()
    {
        String source =
            "package org.jabba;\n" + "@MyAnno\n" + "public @interface Note {\n" + "    String text;\n" + "}\n";

        builder.addSource( new StringReader( source ) );
        assertEquals( 1, builder.getClasses().size() );
    }

    public void testShouldIgnoreAnnotationWithClassType()
    {
        String source = "" + "@Fnord(String.class)\n" + "public interface Foo extends Bar {}\n";

        builder.addSource( new StringReader( source ) );
        assertEquals( "Foo", builder.getClassByName( "Foo" ).getName() );
    }

    // from QDOX-97
    public void testShouldIgnoreAttributeAnnotation()
    {
        String source = "" + "public interface Foo {\n" + "   String echo(@WebParam java.lang.String msg);\n" + "}\n";
        builder.addSource( new StringReader( source ) );
        assertEquals( "Foo", builder.getClassByName( "Foo" ).getName() );
    }

    // from QDOX-101
    public void testShouldNotChokeOnDoubleAttributeAnnotationAndGenerics()
    {
        String source =
            "" + "public class Person {\n" + "    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)\n" + "    @XmlID\n"
                + "    protected String name;\n" + "    public List<String> getAddress() {\n"
                + "        return address;\n" + "    }" + "}\n";
        builder.addSource( new StringReader( source ) );
        assertEquals( "Person", builder.getClassByName( "Person" ).getName() );
    }

    // from QDOX-108
    public void testFQNAnnotations()
    {
        String source = "" + "@com.mycompany.Fnord(a=1)\n" + "public interface Foo extends Bar {}\n";

        builder.addSource( new StringReader( source ) );
        assertEquals( "Foo", builder.getClassByName( "Foo" ).getName() );
    }

    // from QDOX-113
    public void testAnnotationWithParameter()
    {
        String source =
            "public class Foo{\n" + "@Editor(FileEditor.class)\n"
                + "public void setFile(File file) { this.file = file; }" + "}";
        builder.addSource( new StringReader( source ) );
    }

    // from QDOX-128
    public void testQuotedStringAnnotation()
    {
        String source = "@Anno(run = \"1.0\")" + "public interface Foo {}";
        builder.addSource( new StringReader( source ) );
        assertEquals( "\"1.0\"",
                      builder.getClassByName( "Foo" ).getAnnotations().get( 0 ).getProperty( "run" ).getParameterValue() );
    }

    // from QDOX-135
    public void testAnnotationInMethodParamList()
    {
        String source =
            "" + "class Foo {\n" + "    @X()\n" + "    public String xyz(@Y(1) int blah) {\n" + "    }\n" + "}\n";

        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        JavaMethod mth = clazz.getMethods().get( 0 );
        assertEquals( "Foo", clazz.getName() );
        assertEquals( "X", mth.getAnnotations().get( 0 ).getType().getName() );
    }

    // from QDOX-142
    public void testEmptyParameterListAnnotation()
    {
        String source = "@MyAnnotation()\n" + "public class MyClass {}";
        JavaClass cls = builder.addSource( new StringReader( source ) ).getClasses().get( 0 );
        assertEquals( "MyAnnotation", cls.getAnnotations().get( 0 ).getType().getValue() );
    }

    public void testMethodAnnotationBeforeComment()
    {
        String source =
            "class Foo {\n" + "@Override\n" + "/**\n" + " * " + " */"
                + " public boolean isPostback() { return true;}\n" + "}";
        JavaClass cls = builder.addSource( new StringReader( source ) ).getClasses().get( 0 );
        assertEquals( "Override", cls.getMethods().get( 0 ).getAnnotations().get( 0 ).getType().getValue() );
        assertEquals( "java.lang.Override",
                      cls.getMethods().get( 0 ).getAnnotations().get( 0 ).getType().getFullyQualifiedName() );
    }

    public void testEnumsWithAnnotations()
    {
        String source =
            "class Foo {\n" + " public enum BasicType {\n" + " @XmlEnumValue(\"text\")\n" + " TEXT(\"text\"),\n" + ""
                + " @XmlEnumValue(\"value\")\n" + " VALUE(\"value\") }\n" + "}";
        builder.addSource( new StringReader( source ) );
    }

    public void testParameterAnnotations()
    {
        String source =
            "class Foo {\n"
                + "  @NativeAccessible\n"
                + "  static void get_tmp_dir( String targetfilename, @ParamInfo( direction = ParamInfo.Direction.OUT ) byte[] tmpDirOutput ) throws IOException {}\n"
                + "}";
        JavaClass cls = builder.addSource( new StringReader( source ) ).getClasses().get( 0 );
        JavaMethod jMethod = cls.getMethods().get( 0 );
        assertEquals( "NativeAccessible", jMethod.getAnnotations().get( 0 ).getType().getValue() );
        JavaAnnotation annotation = jMethod.getParameters().get( 1 ).getAnnotations().get( 0 );
        assertEquals( "ParamInfo", annotation.getType().getValue() );
        assertEquals( "ParamInfo.Direction.OUT", annotation.getProperty( "direction" ).getParameterValue() );
    }

    public void testFieldRefAnnotation()
    {
        String source =
            "public class Foo {\n" + "  final String s = \"unchecked\";\n" + "  @SuppressWarnings( s )\n"
                + "  public void testNothing() { }\n " + "}";
        JavaClass cls = builder.addSource( new StringReader( source ) ).getClasses().get( 0 );
        JavaMethod method = cls.getMethods().get( 0 );
        FieldRef suppressWarnings = (FieldRef) method.getAnnotations().get( 0 ).getProperty( "value" );
        assertEquals( cls.getFields().get( 0 ), suppressWarnings.getField() );
    }

    public void testDoubleEscapedString()
    {
        JavaProjectBuilder builder = new JavaProjectBuilder();
        String source = "public class Foo {\n" + "@SuppressWarnings({\"abc\\\\d\"})\n" + "private void bar() { } }";
        builder.addSource( new StringReader( source ) );
    }
}
