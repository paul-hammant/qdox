package com.thoughtworks.qdox;

import java.io.StringReader;
import java.util.ListIterator;

import junit.framework.TestCase;

import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.expression.Add;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import com.thoughtworks.qdox.model.expression.AnnotationValueList;
import com.thoughtworks.qdox.model.expression.Constant;
import com.thoughtworks.qdox.model.expression.FieldRef;
import com.thoughtworks.qdox.model.expression.TypeRef;

public class AnnotationsModelTest
    extends TestCase
{

    private JavaProjectBuilder builder;

    @Override
	protected void setUp()
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
        assertEquals( "Annotations", 1, clazz.getAnnotations().size() );
        JavaAnnotation annotation = clazz.getAnnotations().get( 0 );
        assertEquals( "Annotation name", "Annotation", annotation.getType().getFullyQualifiedName() );
        return annotation;
    }

    public void testMarkerAnnotation()
    {
        checkClassAnnotation( "@Annotation\nclass Foo {}" );
    }

    public void testEmptyAnnotation()
    {
        checkClassAnnotation( "@Annotation()\nclass Foo {}" );
    }

    public void testAnnotationAnnotation()
    {
        checkClassAnnotation( "@Annotation(@NestedAnnotation)\nclass Foo {}" );
    }

    public void testConstAnnotation()
    {
        checkClassAnnotation( "@Annotation(1)\nclass Foo {}" );
    }

    public void testAnnotationConstants()
    {
        String source =
            "@Annotation( f = 1.0, d = 1.0d, i = 1, ix = 0x1, l = 1L, lx = 0x1L, c = 'c', s = \"string\" )\nclass Foo {}";
        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        assertEquals( "Annotations", 1, clazz.getAnnotations().size() );
        JavaAnnotation annotation = clazz.getAnnotations().get( 0 );
        assertEquals( "Annotation name", "Annotation", annotation.getType().getFullyQualifiedName() );
        assertEquals( "Properties", 8, annotation.getNamedParameterMap().size() );

        Constant f = (Constant) annotation.getProperty( "f" );
        assertEquals( "f", new Float( 1 ), f.getValue() );

        Constant d = (Constant) annotation.getProperty( "d" );
        assertEquals( "d", new Double( 1 ), d.getValue() );

        Constant i = (Constant) annotation.getProperty( "i" );
        assertEquals( "i", new Integer( 1 ), i.getValue() );

        Constant ix = (Constant) annotation.getProperty( "ix" );
        assertEquals( "ix", new Integer( 1 ), ix.getValue() );

        Constant l = (Constant) annotation.getProperty( "l" );
        assertEquals( "l", new Long( 1 ), l.getValue() );

        Constant lx = (Constant) annotation.getProperty( "lx" );
        assertEquals( "lx", new Long( 1 ), lx.getValue() );

        Constant c = (Constant) annotation.getProperty( "c" );
        assertEquals( "c", new Character( 'c' ), c.getValue() );

        Constant s = (Constant) annotation.getProperty( "s" );
        assertEquals( "s", "string", s.getValue() );
    }

    public void testAnnotationConstantsControlChars()
    {
        String source =
            "@Annotation( s1 = \"a\\nb\", s2 = \"a\\nb\", s3 = \"a\\rb\", s4 = \"a\\tb\", s5 = \"a\\u0009b\" ) class Foo {}";
        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        assertEquals( "Annotations", 1, clazz.getAnnotations().size() );
        JavaAnnotation annotation = clazz.getAnnotations().get( 0 );
        assertEquals( "Annotation name", "Annotation", annotation.getType().getFullyQualifiedName() );
        assertEquals( "Properties", 5, annotation.getPropertyMap().size() );

        Constant s1 = (Constant) annotation.getProperty( "s1" );
        assertEquals( "s1", "a\nb", s1.getValue() );

        Constant s2 = (Constant) annotation.getProperty( "s2" );
        assertEquals( "s2", "a\nb", s2.getValue() );

        Constant s3 = (Constant) annotation.getProperty( "s3" );
        assertEquals( "s3", "a\rb", s3.getValue() );

        Constant s4 = (Constant) annotation.getProperty( "s4" );
        assertEquals( "s4", "a\tb", s4.getValue() );

        Constant s5 = (Constant) annotation.getProperty( "s5" );
        assertEquals( "s5", "a\u0009b", s5.getValue() );
    }

    public void testNestedAnnotation()
    {
        String source = "@Annotation( { @Inner(1), @Inner(2) } ) class Foo {}";
        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        assertEquals( "Annotations", 1, clazz.getAnnotations().size() );
        JavaAnnotation annotation = clazz.getAnnotations().get( 0 );
        assertEquals( "Annotation name", "Annotation", annotation.getType().getFullyQualifiedName() );
        assertEquals( "Properties", 1, annotation.getPropertyMap().size() );

        AnnotationValueList list = (AnnotationValueList) annotation.getProperty( "value" );
        assertEquals( "Inner Annotations", 2, list.getValueList().size() );

        for ( ListIterator<AnnotationValue> i = list.getValueList().listIterator(); i.hasNext(); )
        {
            JavaAnnotation inner = (JavaAnnotation) i.next();
            assertEquals( "Inner " + i.previousIndex(), "Inner", inner.getType().getValue() );
        }
    }

    public void testExpressionAnnotation1()
    {
        String source = "@Annotation( 1 + 1 ) class Foo {}";
        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        assertEquals( "Annotations", 1, clazz.getAnnotations().size() );
        JavaAnnotation annotation = clazz.getAnnotations().get( 0 );
        assertEquals( "Annotation name", "Annotation", annotation.getType().getFullyQualifiedName() );
        assertEquals( "Properties", 1, annotation.getPropertyMap().size() );

        Add add = (Add) annotation.getProperty( "value" );
        assertEquals( "Left", new Integer( 1 ), ( (Constant) add.getLeft() ).getValue() );
        assertEquals( "Right", new Integer( 1 ), ( (Constant) add.getRight() ).getValue() );
    }

    public void testExpressionAnnotation2()
    {
        String source = "@Annotation( \"value = \" + 1 ) class Foo {}";
        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        assertEquals( "Annotations", 1, clazz.getAnnotations().size() );
        JavaAnnotation annotation = clazz.getAnnotations().get( 0 );
        assertEquals( "Annotation name", "Annotation", annotation.getType().getFullyQualifiedName() );
        assertEquals( "Properties", 1, annotation.getPropertyMap().size() );

        Add add = (Add) annotation.getProperty( "value" );
        assertEquals( "Left", "value = ", ( (Constant) add.getLeft() ).getValue() );
        assertEquals( "Right", new Integer( 1 ), ( (Constant) add.getRight() ).getValue() );
    }

    public void testFieldRefAnnotation()
    {
        String source = "@Annotation( java.lang.Math.E ) class Foo {}";
        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        assertEquals( "Annotations", 1, clazz.getAnnotations().size() );
        JavaAnnotation annotation = clazz.getAnnotations().get( 0 );
        assertEquals( "Annotation name", "Annotation", annotation.getType().getFullyQualifiedName() );
        assertEquals( "Properties", 1, annotation.getPropertyMap().size() );

        FieldRef value = (FieldRef) annotation.getProperty( "value" );
        assertEquals( "type", "double", value.getField().getType().getValue() );
        assertEquals( "class part", "java.lang.Math", value.getClassPart() );
        assertEquals( "field part", "E", value.getFieldPart() );
    }

    public void testPrimitiveClassAnnotation()
    {
        String source = "@Annotation( int.class ) class Foo {}";
        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        assertEquals( "Annotations", 1, clazz.getAnnotations().size() );
        JavaAnnotation annotation = clazz.getAnnotations().get( 0 );
        assertEquals( "Annotation name", "Annotation", annotation.getType().getFullyQualifiedName() );
        assertEquals( "Properties", 1, annotation.getPropertyMap().size() );

        Object value = annotation.getProperty( "value" );
        TypeRef ref = (TypeRef) value;
        assertEquals( "value", "int", ref.getType().getValue() );
    }

    public void testClassAnnotation()
    {
        String source = "@Annotation( java.util.Set.class ) class Foo {}";
        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        assertEquals( "Annotations", 1, clazz.getAnnotations().size() );
        JavaAnnotation annotation = clazz.getAnnotations().get( 0 );
        assertEquals( "Annotation name", "Annotation", annotation.getType().getFullyQualifiedName() );
        assertEquals( "Properties", 1, annotation.getPropertyMap().size() );

        Object value = annotation.getProperty( "value" );
        TypeRef ref = (TypeRef) value;
        assertEquals( "value", "java.util.Set", ref.getType().getValue() );
    }

    // from Qdox-98
    public void testPackageWithAnnotation()
    {
        String source =
            "@javax.xml.bind.annotation.XmlSchema(namespace = \"http://docs.oasis-open.org/wsn/br-2\")\n"
                + "package org.oasis_open.docs.wsn.br_2;\n" + "public class Foo {}";
        JavaClass cls = builder.addSource( new StringReader( source ) ).getClasses().get( 0 );
        JavaPackage jPackage = cls.getPackage();
        assertEquals( "org.oasis_open.docs.wsn.br_2", jPackage.getName() );
        assertEquals( "javax.xml.bind.annotation.XmlSchema", jPackage.getAnnotations().get( 0 ).getType().getValue() );
        assertEquals( 2, jPackage.getLineNumber() );

    }

    // http://jira.codehaus.org/browse/QDOX-135
    public void testAnnotationInMethodParamList()
    {
        String source = "" + "class Foo {\n"
        // + "    @X()\n" - does not affect test.
            + "    public String xyz(@Y(1) int blah) {\n" + "    }\n" + "}\n";

        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        assertEquals( "Foo", clazz.getName() );
        JavaMethod mth = clazz.getMethods().get( 0 );
        JavaAnnotation paramAnn = mth.getParameterByName( "blah" ).getAnnotations().get( 0 );
        assertEquals( "@Y(value=1)", paramAnn.toString() );
    }

}
