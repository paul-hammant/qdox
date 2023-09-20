package com.thoughtworks.qdox;

import com.thoughtworks.qdox.builder.impl.EvaluatingVisitor;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import com.thoughtworks.qdox.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.StringReader;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class AnnotationExpressionTest
{
    private JavaProjectBuilder builder;

    @BeforeEach
    public void setUp()
    {
        builder = new JavaProjectBuilder();
    }

    static Stream<Arguments> expressions()
    {
        return Stream.of(
            // testPrecedence
            Arguments.arguments( "2 + 2 * 5", new Integer( 12 ) ),
            Arguments.arguments( "2 * 5 + 2", new Integer( 12 ) ),
            Arguments.arguments( "2+2*5", new Integer( 12 ) ),
            Arguments.arguments( "2*5+2", new Integer( 12 ) ),

            // testLogicalExpression
            Arguments.arguments( "true && false", Boolean.FALSE ),
            Arguments.arguments( "true || false", Boolean.TRUE ),
            Arguments.arguments( "!true", Boolean.FALSE ),

            // testBitExpression
            Arguments.arguments( "1 & 3", new Integer( 1 & 3 ) ),
            Arguments.arguments( "1 | 3", new Integer( 1 | 3 ) ),
            Arguments.arguments( "1 ^ 3", new Integer( 1 ^ 3 ) ),
            Arguments.arguments( "~1", new Integer( ~1 ) ),

            // testSignExpression
            Arguments.arguments( "+1", new Integer( 1 ) ),
            Arguments.arguments( "-1", new Integer( -1 ) ),
            //{ "--1", new Integer( --1 ) ),

            // testAddSubMultDivExpression
            Arguments.arguments( "8 / 3", new Integer( 8 / 3 ) ),
            Arguments.arguments( "8 * 3", new Integer( 8 * 3 ) ),
            Arguments.arguments( "8 + 3", new Integer( 8 + 3 ) ),
            Arguments.arguments( "8 - 3", new Integer( 8 - 3 ) ),
            Arguments.arguments( "8 % 3", new Integer( 8 % 3 ) ),
            Arguments.arguments( "\"a\" + \"b\"", "a" + "b" ),

            // testShiftExpression
            Arguments.arguments( "8 << 2", new Integer( 8 << 2 ) ),
            Arguments.arguments( "8 >> 2", new Integer( 8 >> 2 ) ),
            Arguments.arguments( "-1 >> 2", new Integer( -1 >> 2 ) ),
            Arguments.arguments( "-1 >>> 2", new Integer( -1 >>> 2 ) ),

            // testLiteral
            Arguments.arguments( "1", new Integer( 1 ) ),
            Arguments.arguments( "1l", new Long( 1 ) ),
            Arguments.arguments( "1.0", new Float( 1 ) ),
            Arguments.arguments( "1.0d", new Double( 1 ) ),

            // testParenExpression
            Arguments.arguments( "2 + (2 * 5)", new Integer( 12 ) ),
            Arguments.arguments( "(2 + 2) * 5", new Integer( 20 ) ),

            // testCompareExpression
            Arguments.arguments( "1 < 2", Boolean.TRUE ),
            Arguments.arguments( "1 > 2", Boolean.FALSE ),
            Arguments.arguments( "1 <= 2", Boolean.TRUE ),
            Arguments.arguments( "1 >= 2", Boolean.FALSE ),
            Arguments.arguments( "1 == 2", Boolean.FALSE ),
            Arguments.arguments( "1 != 2", Boolean.TRUE ),

            // testQueryExpression
            Arguments.arguments( "1 < 2 ? 0 : 3", new Integer( 0 ) ),
            Arguments.arguments( "1 > 2 ? 0 : 3", new Integer( 3 ) ),

            // testCastExpression
            Arguments.arguments( "(short)1", new Short( (short) 1 ) ),
            Arguments.arguments( "(long)(short)1", new Long( 1 ) ),
            Arguments.arguments( "(int)((short)1 + (long)3)", new Integer( 4 ) ),

            // test Array-valued single-element annotation
            Arguments.arguments( "{\"Children\", \"Unscrupulous dentists\"}",
                Arrays.asList( new String[] { "Children", "Unscrupulous dentists" } ) ),

            // test Array-valued annotation ending with a comma
            Arguments.arguments( "{\"deprecated\", }", Arrays.asList( new String[] { "deprecated" } ) ),

            // Binary Literals (added to JDK7)
            Arguments.arguments( "(byte)0b00100001", Byte.parseByte( "100001", 2 ) ),
            Arguments.arguments( "(short)0b0110000101000101", Short.parseShort( "0110000101000101", 2 ) ),
            Arguments.arguments( "0b101", Integer.parseInt( "101", 2 ) ),
            Arguments.arguments( "0B101", Integer.parseInt( "101", 2 ) ),
            Arguments.arguments( "0b00010000101000101101000010100010110100001010001011010000101000101L",
                Long.parseLong( "00010000101000101101000010100010110100001010001011010000101000101", 2 ) ),

            // Underscores in Numeric Literals (added to JDK7)
            Arguments.arguments( "1234_5678", Integer.parseInt( "12345678" ) ),
            Arguments.arguments( "1234_5678_9012_3456L", Long.valueOf( "1234567890123456" ) ),
            Arguments.arguments( "0xFF_EC_DE_5EL", Long.valueOf( "FFECDE5E", 16 ) ),
            Arguments.arguments( "0x7fff_ffff_ffff_ffffL", Long.valueOf( "7fffffffffffffff", 16 ) ),
            Arguments.arguments( "(byte) 0b0010_0101 ", Byte.parseByte( "00100101", 2 ) ),
            Arguments.arguments( "5_______2", Integer.parseInt( "52" ) ),
            Arguments.arguments( "0x5_2", Integer.parseInt( "52", 16 ) ),
            Arguments.arguments( "0_52", Integer.parseInt( "52", 8 ) ),
            Arguments.arguments( "05_2", Integer.parseInt( "52", 8 ) ) );
    }

    @ParameterizedTest
    @MethodSource
    void expressions( String expression, Object expected )
    {
        String source = "@Annotation(\n" + expression + "\n) class Foo {}";
        try
        {
            builder.addSource( new StringReader( source ) );
            assertAnnotationValue( expected );
        }
        catch ( ParseException pe )
        {
            fail( pe.getMessage() + "Failed to parse '" + source + "'" );
        }
    }

    protected void assertAnnotationValue( Object expected )
    {
        JavaClass clazz = builder.getClassByName( "Foo" );
        assertEquals( 1, clazz.getAnnotations().size(), "Annotations" );
        JavaAnnotation annotation = clazz.getAnnotations().get( 0 );
        assertEquals( "Annotation", annotation.getType().getFullyQualifiedName(), "Annotation name" );
        assertEquals( 1, annotation.getPropertyMap().size(), "Properties" );

        AnnotationValue value = annotation.getProperty( "value" );
        Object v = value.accept( new EvaluatingVisitor() );
        assertEquals( expected, v, "Value" );
    }
}