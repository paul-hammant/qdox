package com.thoughtworks.qdox;

import java.io.StringReader;
import java.util.ListIterator;

import junit.framework.TestCase;

import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.annotation.AnnotationAdd;
import com.thoughtworks.qdox.model.annotation.AnnotationConstant;
import com.thoughtworks.qdox.model.annotation.AnnotationFieldRef;
import com.thoughtworks.qdox.model.annotation.AnnotationTypeRef;
import com.thoughtworks.qdox.model.annotation.AnnotationValue;
import com.thoughtworks.qdox.model.annotation.AnnotationValueList;
import com.thoughtworks.qdox.model.annotation.EvaluatingVisitor;

public class AnnotationsModelTest extends TestCase {

    private JavaDocBuilder builder;

    private EvaluatingVisitor evaluatingVisitor = new EvaluatingVisitor() {
        protected Object getFieldReferenceValue( JavaField javaField ) {
            throw new UnsupportedOperationException();
        }
    };

    protected void setUp() throws Exception {
        super.setUp();
        builder = new JavaDocBuilder();
        builder.setDebugLexer( true );
        builder.setDebugParser( true );
    }

    protected Annotation checkClassAnnotation( String source ) {
        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        assertEquals( "Annotations", 1, clazz.getAnnotations().length );
        Annotation annotation = clazz.getAnnotations()[0];
        assertEquals( "Annotation name", "Annotation", annotation.getType().getJavaClass().getFullyQualifiedName() );

        return annotation;
    }

    public void testMarkerAnnotation() {
        checkClassAnnotation( "@Annotation\nclass Foo {}" );
    }

    public void testEmptyAnnotation() {
        checkClassAnnotation( "@Annotation()\nclass Foo {}" );
    }

    public void testAnnotationAnnotation() {
        checkClassAnnotation( "@Annotation(@NestedAnnotation)\nclass Foo {}" );
    }

    public void testConstAnnotation() {
        checkClassAnnotation( "@Annotation(1)\nclass Foo {}" );
    }

    public void testAnnotationConstants() {
        String source = "@Annotation( f = 1.0, d = 1.0d, i = 1, ix = 0x1, l = 1L, lx = 0x1L, c = 'c', s = \"string\" )\nclass Foo {}";
        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        assertEquals( "Annotations", 1, clazz.getAnnotations().length );
        Annotation annotation = clazz.getAnnotations()[0];
        assertEquals( "Annotation name", "Annotation", annotation.getType().getJavaClass().getFullyQualifiedName() );
        assertEquals( "Properties", 8, annotation.getNamedParameterMap().size() );

        AnnotationConstant f = (AnnotationConstant) annotation.getProperty( "f" );
        assertEquals( "f", new Float( 1 ), f.getValue() );

        AnnotationConstant d = (AnnotationConstant) annotation.getProperty( "d" );
        assertEquals( "d", new Double( 1 ), d.getValue() );

        AnnotationConstant i = (AnnotationConstant) annotation.getProperty( "i" );
        assertEquals( "i", new Integer( 1 ), i.getValue() );

        AnnotationConstant ix = (AnnotationConstant) annotation.getProperty( "ix" );
        assertEquals( "ix", new Integer( 1 ), ix.getValue() );

        AnnotationConstant l = (AnnotationConstant) annotation.getProperty( "l" );
        assertEquals( "l", new Long( 1 ), l.getValue() );

        AnnotationConstant lx = (AnnotationConstant) annotation.getProperty( "lx" );
        assertEquals( "lx", new Long( 1 ), lx.getValue() );

        AnnotationConstant c = (AnnotationConstant) annotation.getProperty( "c" );
        assertEquals( "c", new Character( 'c' ), c.getValue() );

        AnnotationConstant s = (AnnotationConstant) annotation.getProperty( "s" );
        assertEquals( "s", "string", s.getValue() );
    }

    public void testAnnotationConstantsControlChars() {
        String source = "@Annotation( s1 = \"a\\nb\", s2 = \"a\\nb\", s3 = \"a\\rb\", s4 = \"a\\tb\", s5 = \"a\\u0009b\" ) class Foo {}";
        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        assertEquals( "Annotations", 1, clazz.getAnnotations().length );
        Annotation annotation = clazz.getAnnotations()[0];
        assertEquals( "Annotation name", "Annotation", annotation.getType().getJavaClass().getFullyQualifiedName() );
        assertEquals( "Properties", 5, annotation.getPropertyMap().size() );

        AnnotationConstant s1 = (AnnotationConstant) annotation.getProperty( "s1" );
        assertEquals( "s1", "a\nb", s1.getValue() );

        AnnotationConstant s2 = (AnnotationConstant) annotation.getProperty( "s2" );
        assertEquals( "s2", "a\nb", s2.getValue() );

        AnnotationConstant s3 = (AnnotationConstant) annotation.getProperty( "s3" );
        assertEquals( "s3", "a\rb", s3.getValue() );

        AnnotationConstant s4 = (AnnotationConstant) annotation.getProperty( "s4" );
        assertEquals( "s4", "a\tb", s4.getValue() );

        AnnotationConstant s5 = (AnnotationConstant) annotation.getProperty( "s5" );
        assertEquals( "s5", "a\u0009b", s5.getValue() );
    }

    public void testNestedAnnotation() {
        String source = "@Annotation( { @Inner(1), @Inner(2) } ) class Foo {}";
        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        assertEquals( "Annotations", 1, clazz.getAnnotations().length );
        Annotation annotation = clazz.getAnnotations()[0];
        assertEquals( "Annotation name", "Annotation", annotation.getType().getJavaClass().getFullyQualifiedName() );
        assertEquals( "Properties", 1, annotation.getPropertyMap().size() );

        AnnotationValueList list = (AnnotationValueList) annotation.getProperty( "value" );
        assertEquals( "Inner Annotations", 2, list.getValueList().size() );

        for( ListIterator i = list.getValueList().listIterator(); i.hasNext(); ) {
            Annotation inner = (Annotation) i.next();
            assertEquals( "Inner " + i.previousIndex(), "Inner", inner.getType().getValue() );
        }
    }

    public void testExpressionAnnotation1() {
        String source = "@Annotation( 1 + 1 ) class Foo {}";
        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        assertEquals( "Annotations", 1, clazz.getAnnotations().length );
        Annotation annotation = clazz.getAnnotations()[0];
        assertEquals( "Annotation name", "Annotation", annotation.getType().getJavaClass().getFullyQualifiedName() );
        assertEquals( "Properties", 1, annotation.getPropertyMap().size() );

        AnnotationAdd add = (AnnotationAdd) annotation.getProperty( "value" );
        assertEquals( "Left", new Integer( 1 ), ((AnnotationConstant) add.getLeft()).getValue() );
        assertEquals( "Right", new Integer( 1 ), ((AnnotationConstant) add.getRight()).getValue() );
    }

    public void testExpressionAnnotation2() {
        String source = "@Annotation( \"value = \" + 1 ) class Foo {}";
        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        assertEquals( "Annotations", 1, clazz.getAnnotations().length );
        Annotation annotation = clazz.getAnnotations()[0];
        assertEquals( "Annotation name", "Annotation", annotation.getType().getJavaClass().getFullyQualifiedName() );
        assertEquals( "Properties", 1, annotation.getPropertyMap().size() );

        AnnotationAdd add = (AnnotationAdd) annotation.getProperty( "value" );
        assertEquals( "Left", "value = ", ((AnnotationConstant) add.getLeft()).getValue() );
        assertEquals( "Right", new Integer( 1 ), ((AnnotationConstant) add.getRight()).getValue() );
    }

    public void testFieldRefAnnotation() {
        String source = "@Annotation( java.lang.Math.E ) class Foo {}";
        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        assertEquals( "Annotations", 1, clazz.getAnnotations().length );
        Annotation annotation = clazz.getAnnotations()[0];
        assertEquals( "Annotation name", "Annotation", annotation.getType().getJavaClass().getFullyQualifiedName() );
        assertEquals( "Properties", 1, annotation.getPropertyMap().size() );

        AnnotationFieldRef value = (AnnotationFieldRef) annotation.getProperty( "value" );
        assertEquals( "type", "double", value.getField().getType().getValue() );
        assertEquals( "class part", "java.lang.Math", value.getClassPart() );
        assertEquals( "field part", "E", value.getFieldPart() );
    }

    public void testPrimitiveClassAnnotation() {
        String source = "@Annotation( int.class ) class Foo {}";
        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        assertEquals( "Annotations", 1, clazz.getAnnotations().length );
        Annotation annotation = clazz.getAnnotations()[0];
        assertEquals( "Annotation name", "Annotation", annotation.getType().getJavaClass().getFullyQualifiedName() );
        assertEquals( "Properties", 1, annotation.getPropertyMap().size() );

        Object value = annotation.getProperty( "value" );
        AnnotationTypeRef ref = (AnnotationTypeRef) value;
        assertEquals( "value", "int", ref.getType().getValue() );
    }

    public void testClassAnnotation() {
        String source = "@Annotation( java.util.Set.class ) class Foo {}";
        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.getClassByName( "Foo" );
        assertEquals( "Annotations", 1, clazz.getAnnotations().length );
        Annotation annotation = clazz.getAnnotations()[0];
        assertEquals( "Annotation name", "Annotation", annotation.getType().getJavaClass().getFullyQualifiedName() );
        assertEquals( "Properties", 1, annotation.getPropertyMap().size() );

        Object value = annotation.getProperty( "value" );
        AnnotationTypeRef ref = (AnnotationTypeRef) value;
        assertEquals( "value", "java.util.Set", ref.getType().getValue() );
    }

    protected void assertAnnotationValue( Object expected ) {
        JavaClass clazz = builder.getClassByName( "Foo" );
        assertEquals( "Annotations", 1, clazz.getAnnotations().length );
        Annotation annotation = clazz.getAnnotations()[0];
        assertEquals( "Annotation name", "Annotation", annotation.getType().getJavaClass().getFullyQualifiedName() );
        assertEquals( "Properties", 1, annotation.getPropertyMap().size() );

        AnnotationValue value = annotation.getProperty( "value" );
        Object v = value.accept( evaluatingVisitor );
        assertEquals( "Value", expected, v );
    }

    protected void assertAnnotationExpression( String expression, Object expected ) {
        String source = "@Annotation(\n" + expression + "\n) class Foo {}";
        builder.addSource( new StringReader( source ) );
        assertAnnotationValue( expected );
    }

    public void testPrecedence() {
        assertAnnotationExpression( "2 + 2 * 5", new Integer( 12 ) );
        assertAnnotationExpression( "2 * 5 + 2", new Integer( 12 ) );
    }

    public void testLogicalExpression() {
        assertAnnotationExpression( "true && false", Boolean.FALSE );
        assertAnnotationExpression( "true || false", Boolean.TRUE );
        assertAnnotationExpression( "!true", Boolean.FALSE );
    }

    public void testBitExpression() {
        assertAnnotationExpression( "1 & 3", new Integer( 1 & 3 ) );
        assertAnnotationExpression( "1 | 3", new Integer( 1 | 3 ) );
        assertAnnotationExpression( "1 ^ 3", new Integer( 1 ^ 3 ) );
        assertAnnotationExpression( "~1", new Integer( ~1 ) );
    }

    public void testSignExpression() {
        assertAnnotationExpression( "+1", new Integer( 1 ) );
        assertAnnotationExpression( "-1", new Integer( -1 ) );
        assertAnnotationExpression( "--1", new Integer( 1 ) );
    }

    public void testAddSubMultDivExpression() {
        assertAnnotationExpression( "8 / 3", new Integer( 8 / 3 ) );
        assertAnnotationExpression( "8 * 3", new Integer( 8 * 3 ) );
        assertAnnotationExpression( "8 + 3", new Integer( 8 + 3 ) );
        assertAnnotationExpression( "8 - 3", new Integer( 8 - 3 ) );
        assertAnnotationExpression( "8 % 3", new Integer( 8 % 3 ) );
        assertAnnotationExpression( "\"a\" + \"b\"", "a" + "b" );
    }

    public void testShiftExpression() {
        assertAnnotationExpression( "8 << 2", new Integer( 8 << 2 ) );
        assertAnnotationExpression( "8 >> 2", new Integer( 8 >> 2 ) );
        assertAnnotationExpression( "-1 >> 2", new Integer( -1 >> 2 ) );
        assertAnnotationExpression( "-1 >>> 2", new Integer( -1 >>> 2 ) );
    }

    public void testLiteral() {
        assertAnnotationExpression( "1", new Integer( 1 ) );
        assertAnnotationExpression( "1l", new Long( 1 ) );
        assertAnnotationExpression( "1.0", new Float( 1 ) );
        assertAnnotationExpression( "1.0d", new Double( 1 ) );
    }

    public void testParenExpression() {
        assertAnnotationExpression( "2 + (2 * 5)", new Integer( 12 ) );
        assertAnnotationExpression( "(2 + 2) * 5", new Integer( 20 ) );
    }

    public void testCompareExpression() {
        assertAnnotationExpression( "1 < 2", Boolean.TRUE );
        assertAnnotationExpression( "1 > 2", Boolean.FALSE );
        assertAnnotationExpression( "1 <= 2", Boolean.TRUE );
        assertAnnotationExpression( "1 >= 2", Boolean.FALSE );
        assertAnnotationExpression( "1 == 2", Boolean.FALSE );
        assertAnnotationExpression( "1 != 2", Boolean.TRUE );
    }

    public void testQueryExpression() {
        assertAnnotationExpression( "1 < 2 ? 0 : 3", new Integer( 0 ) );
        assertAnnotationExpression( "1 > 2 ? 0 : 3", new Integer( 3 ) );
    }

    public void testCastExpression() {
        assertAnnotationExpression( "(short)1", new Short( (short) 1 ) );
        assertAnnotationExpression( "(long)(short)1", new Long( 1 ) );
        assertAnnotationExpression( "(int)((short)1 + (long)3)", new Integer( 4 ) );
    }
    
    //from Qdox-98
    public void testPackageWithAnnotation() throws Exception {
    	String source = "@javax.xml.bind.annotation.XmlSchema(namespace = \"http://docs.oasis-open.org/wsn/br-2\")\n" +
    			"package org.oasis_open.docs.wsn.br_2;\n" +
    			"public class Foo {}";
    	builder.addSource(new StringReader(source));
    	JavaPackage jPackage = builder.getClasses()[0].getPackage();
    	assertEquals("org.oasis_open.docs.wsn.br_2", jPackage.getName());
    	assertEquals("javax.xml.bind.annotation.XmlSchema", jPackage.getAnnotations()[0].getType().getValue());
    	assertEquals(2, jPackage.getLineNumber());
    	
    	
    }

    // http://jira.codehaus.org/browse/QDOX-135
    public void testAnnotationInMethodParamList() {
        String source = ""
                + "class Foo {\n"
            //    + "    @X()\n"  - does not affect test.
                + "    public String xyz(@Y(1) int blah) {\n"
                + "    }\n"
                + "}\n";

        builder.addSource(new StringReader(source));
        JavaClass clazz = builder.getClassByName("Foo");
        JavaMethod mth = clazz.getMethods()[0];
        assertEquals("Foo", clazz.getName());
    }

}
