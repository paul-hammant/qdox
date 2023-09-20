package com.thoughtworks.qdox;

import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.expression.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.ListIterator;

public class AnnotationsModelTest {

    private JavaProjectBuilder builder;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        builder = new JavaProjectBuilder();
        //builder.setDebugLexer( true );
        //builder.setDebugParser( true );
    }

    protected JavaAnnotation checkClassAnnotation( String source )
    {
        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        Assertions.assertEquals(1, clazz.getAnnotations().size(), "Annotations");
        JavaAnnotation annotation = clazz.getAnnotations().get( 0 );
        Assertions.assertEquals("Annotation", annotation.getType().getFullyQualifiedName(), "Annotation name");
        return annotation;
    }

    @Test
    public void testMarkerAnnotation()
    {
        checkClassAnnotation( "@Annotation\nclass Foo {}" );
    }

    @Test
    public void testEmptyAnnotation()
    {
        checkClassAnnotation( "@Annotation()\nclass Foo {}" );
    }

    @Test
    public void testAnnotationAnnotation()
    {
        checkClassAnnotation( "@Annotation(@NestedAnnotation)\nclass Foo {}" );
    }

    @Test
    public void testConstAnnotation()
    {
        checkClassAnnotation( "@Annotation(1)\nclass Foo {}" );
    }

    @Test
    public void testAnnotationConstants()
    {
        String source =
            "@Annotation( f = 1.0, d = 1.0d, i = 1, ix = 0x1, l = 1L, lx = 0x1L, c = 'c', s = \"string\" )\nclass Foo {}";
        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        Assertions.assertEquals(1, clazz.getAnnotations().size(), "Annotations");
        JavaAnnotation annotation = clazz.getAnnotations().get( 0 );
        Assertions.assertEquals("Annotation", annotation.getType().getFullyQualifiedName(), "Annotation name");
        Assertions.assertEquals(8, annotation.getNamedParameterMap().size(), "Properties");

        Constant f = (Constant) annotation.getProperty( "f" );
        Assertions.assertEquals(new Float( 1 ), f.getValue(), "f");

        Constant d = (Constant) annotation.getProperty( "d" );
        Assertions.assertEquals(new Double( 1 ), d.getValue(), "d");

        Constant i = (Constant) annotation.getProperty( "i" );
        Assertions.assertEquals(new Integer( 1 ), i.getValue(), "i");

        Constant ix = (Constant) annotation.getProperty( "ix" );
        Assertions.assertEquals(new Integer( 1 ), ix.getValue(), "ix");

        Constant l = (Constant) annotation.getProperty( "l" );
        Assertions.assertEquals(new Long( 1 ), l.getValue(), "l");

        Constant lx = (Constant) annotation.getProperty( "lx" );
        Assertions.assertEquals(new Long( 1 ), lx.getValue(), "lx");

        Constant c = (Constant) annotation.getProperty( "c" );
        Assertions.assertEquals(new Character( 'c' ), c.getValue(), "c");

        Constant s = (Constant) annotation.getProperty( "s" );
        Assertions.assertEquals("string", s.getValue(), "s");
    }

    @Test
    public void testAnnotationConstantsControlChars()
    {
        String source =
            "@Annotation( s1 = \"a\\nb\", s2 = \"a\\nb\", s3 = \"a\\rb\", s4 = \"a\\tb\", s5 = \"a\\u0009b\" ) class Foo {}";
        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        Assertions.assertEquals(1, clazz.getAnnotations().size(), "Annotations");
        JavaAnnotation annotation = clazz.getAnnotations().get( 0 );
        Assertions.assertEquals("Annotation", annotation.getType().getFullyQualifiedName(), "Annotation name");
        Assertions.assertEquals(5, annotation.getPropertyMap().size(), "Properties");

        Constant s1 = (Constant) annotation.getProperty( "s1" );
        Assertions.assertEquals("a\nb", s1.getValue(), "s1");

        Constant s2 = (Constant) annotation.getProperty( "s2" );
        Assertions.assertEquals("a\nb", s2.getValue(), "s2");

        Constant s3 = (Constant) annotation.getProperty( "s3" );
        Assertions.assertEquals("a\rb", s3.getValue(), "s3");

        Constant s4 = (Constant) annotation.getProperty( "s4" );
        Assertions.assertEquals("a\tb", s4.getValue(), "s4");

        Constant s5 = (Constant) annotation.getProperty( "s5" );
        Assertions.assertEquals("a\u0009b", s5.getValue(), "s5");
    }

    @Test
    public void testNestedAnnotation()
    {
        String source = "@Annotation( { @Inner(1), @Inner(2) } ) class Foo {}";
        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        Assertions.assertEquals(1, clazz.getAnnotations().size(), "Annotations");
        JavaAnnotation annotation = clazz.getAnnotations().get( 0 );
        Assertions.assertEquals("Annotation", annotation.getType().getFullyQualifiedName(), "Annotation name");
        Assertions.assertEquals(1, annotation.getPropertyMap().size(), "Properties");

        AnnotationValueList list = (AnnotationValueList) annotation.getProperty( "value" );
        Assertions.assertEquals(2, list.getValueList().size(), "Inner Annotations");

        for ( ListIterator<AnnotationValue> i = list.getValueList().listIterator(); i.hasNext(); )
        {
            JavaAnnotation inner = (JavaAnnotation) i.next();
            Assertions.assertEquals("Inner", inner.getType().getValue(), "Inner " + i.previousIndex());
        }
    }

    @Test
    public void testExpressionAnnotation1()
    {
        String source = "@Annotation( 1 + 1 ) class Foo {}";
        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        Assertions.assertEquals(1, clazz.getAnnotations().size(), "Annotations");
        JavaAnnotation annotation = clazz.getAnnotations().get( 0 );
        Assertions.assertEquals("Annotation", annotation.getType().getFullyQualifiedName(), "Annotation name");
        Assertions.assertEquals(1, annotation.getPropertyMap().size(), "Properties");

        Add add = (Add) annotation.getProperty( "value" );
        Assertions.assertEquals(new Integer( 1 ), ( (Constant) add.getLeft() ).getValue(), "Left");
        Assertions.assertEquals(new Integer( 1 ), ( (Constant) add.getRight() ).getValue(), "Right");
    }

    @Test
    public void testExpressionAnnotation2()
    {
        String source = "@Annotation( \"value = \" + 1 ) class Foo {}";
        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        Assertions.assertEquals(1, clazz.getAnnotations().size(), "Annotations");
        JavaAnnotation annotation = clazz.getAnnotations().get( 0 );
        Assertions.assertEquals("Annotation", annotation.getType().getFullyQualifiedName(), "Annotation name");
        Assertions.assertEquals(1, annotation.getPropertyMap().size(), "Properties");

        Add add = (Add) annotation.getProperty( "value" );
        Assertions.assertEquals("value = ", ( (Constant) add.getLeft() ).getValue(), "Left");
        Assertions.assertEquals(new Integer( 1 ), ( (Constant) add.getRight() ).getValue(), "Right");
    }

    @Test
    public void testFieldRefAnnotation()
    {
        String source = "@Annotation( java.lang.Math.E ) class Foo {}";
        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        Assertions.assertEquals(1, clazz.getAnnotations().size(), "Annotations");
        JavaAnnotation annotation = clazz.getAnnotations().get( 0 );
        Assertions.assertEquals("Annotation", annotation.getType().getFullyQualifiedName(), "Annotation name");
        Assertions.assertEquals(1, annotation.getPropertyMap().size(), "Properties");

        FieldRef value = (FieldRef) annotation.getProperty( "value" );
        Assertions.assertEquals("double", value.getField().getType().getValue(), "type");
        Assertions.assertEquals("java.lang.Math", value.getClassPart(), "class part");
        Assertions.assertEquals("E", value.getFieldPart(), "field part");
    }

    @Test
    public void testPrimitiveClassAnnotation()
    {
        String source = "@Annotation( int.class ) class Foo {}";
        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        Assertions.assertEquals(1, clazz.getAnnotations().size(), "Annotations");
        JavaAnnotation annotation = clazz.getAnnotations().get( 0 );
        Assertions.assertEquals("Annotation", annotation.getType().getFullyQualifiedName(), "Annotation name");
        Assertions.assertEquals(1, annotation.getPropertyMap().size(), "Properties");

        Object value = annotation.getProperty( "value" );
        TypeRef ref = (TypeRef) value;
        Assertions.assertEquals("int", ref.getType().getValue(), "value");
    }

    @Test
    public void testClassAnnotation()
    {
        String source = "@Annotation( java.util.Set.class ) class Foo {}";
        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        Assertions.assertEquals(1, clazz.getAnnotations().size(), "Annotations");
        JavaAnnotation annotation = clazz.getAnnotations().get( 0 );
        Assertions.assertEquals("Annotation", annotation.getType().getFullyQualifiedName(), "Annotation name");
        Assertions.assertEquals(1, annotation.getPropertyMap().size(), "Properties");

        Object value = annotation.getProperty( "value" );
        TypeRef ref = (TypeRef) value;
        Assertions.assertEquals("java.util.Set", ref.getType().getValue(), "value");
    }

    // from Qdox-98
    @Test
    public void testPackageWithAnnotation()
    {
        String source =
            "@javax.xml.bind.annotation.XmlSchema(namespace = \"http://docs.oasis-open.org/wsn/br-2\")\n"
                + "package org.oasis_open.docs.wsn.br_2;\n" + "public class Foo {}";
        JavaClass cls = builder.addSource( new StringReader( source ) ).getClasses().get( 0 );
        JavaPackage jPackage = cls.getPackage();
        Assertions.assertEquals("org.oasis_open.docs.wsn.br_2", jPackage.getName());
        Assertions.assertEquals("javax.xml.bind.annotation.XmlSchema", jPackage.getAnnotations().get( 0 ).getType().getValue());
        Assertions.assertEquals(2, jPackage.getLineNumber());

    }

    // http://jira.codehaus.org/browse/QDOX-135
    @Test
    public void testAnnotationInMethodParamList()
    {
        String source = "" + "class Foo {\n"
        // + "    @X()\n" - does not affect test.
            + "    public String xyz(@Y(1) int blah) {\n" + "    }\n" + "}\n";

        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        Assertions.assertEquals("Foo", clazz.getName());
        JavaMethod mth = clazz.getMethods().get( 0 );
        JavaAnnotation paramAnn = mth.getParameterByName( "blah" ).getAnnotations().get( 0 );
        Assertions.assertEquals("@Y(value=1)", paramAnn.toString());
    }

}
