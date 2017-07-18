package com.thoughtworks.qdox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.thoughtworks.qdox.builder.impl.EvaluatingVisitor;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import com.thoughtworks.qdox.parser.ParseException;

@RunWith( Parameterized.class )
public class AnnotationExpressionTest
{

    private JavaProjectBuilder builder;

    private String expression;

    private Object parsedResult;

    public AnnotationExpressionTest( String expression, Object parsedResult )
    {
        this.expression = expression;
        this.parsedResult = parsedResult;
    }

    @Before
    public void setUp()
    {
        builder = new JavaProjectBuilder();
    }

    @Parameters
    public static Collection<?> createParameters()
    {
        return Arrays.asList( new Object[][] {
            // testPrecedence
            { "2 + 2 * 5", new Integer( 12 ) },
            { "2 * 5 + 2", new Integer( 12 ) },
            { "2+2*5", new Integer( 12 ) },
            { "2*5+2", new Integer( 12 ) },

            // testLogicalExpression
            { "true && false", Boolean.FALSE },
            { "true || false", Boolean.TRUE },
            { "!true", Boolean.FALSE },

            // testBitExpression
            { "1 & 3", new Integer( 1 & 3 ) },
            { "1 | 3", new Integer( 1 | 3 ) },
            { "1 ^ 3", new Integer( 1 ^ 3 ) },
            { "~1", new Integer( ~1 ) },

            // testSignExpression
            { "+1", new Integer( 1 ) },
            { "-1", new Integer( -1 ) },
            //{ "--1", new Integer( --1 ) },

            // testAddSubMultDivExpression
            { "8 / 3", new Integer( 8 / 3 ) },
            { "8 * 3", new Integer( 8 * 3 ) },
            { "8 + 3", new Integer( 8 + 3 ) },
            { "8 - 3", new Integer( 8 - 3 ) },
            { "8 % 3", new Integer( 8 % 3 ) },
            { "\"a\" + \"b\"", "a" + "b" },

            // testShiftExpression
            { "8 << 2", new Integer( 8 << 2 ) },
            { "8 >> 2", new Integer( 8 >> 2 ) },
            { "-1 >> 2", new Integer( -1 >> 2 ) },
            { "-1 >>> 2", new Integer( -1 >>> 2 ) },

            // testLiteral
            { "1", new Integer( 1 ) },
            { "1l", new Long( 1 ) },
            { "1.0", new Float( 1 ) },
            { "1.0d", new Double( 1 ) },

            // testParenExpression
            { "2 + (2 * 5)", new Integer( 12 ) },
            { "(2 + 2) * 5", new Integer( 20 ) },

            // testCompareExpression
            { "1 < 2", Boolean.TRUE },
            { "1 > 2", Boolean.FALSE },
            { "1 <= 2", Boolean.TRUE },
            { "1 >= 2", Boolean.FALSE },
            { "1 == 2", Boolean.FALSE },
            { "1 != 2", Boolean.TRUE },

            // testQueryExpression
            { "1 < 2 ? 0 : 3", new Integer( 0 ) },
            { "1 > 2 ? 0 : 3", new Integer( 3 ) },

            // testCastExpression
            { "(short)1", new Short( (short) 1 ) },
            { "(long)(short)1", new Long( 1 ) },
            { "(int)((short)1 + (long)3)", new Integer( 4 ) },

            // test Array-valued single-element annotation
            { "{\"Children\", \"Unscrupulous dentists\"}",
                Arrays.asList( new String[] { "Children", "Unscrupulous dentists" } ) },

            // test Array-valued annotation ending with a comma
            { "{\"deprecated\", }", Arrays.asList( new String[] { "deprecated" } ) },

            // Binary Literals (added to JDK7)
            { "(byte)0b00100001", Byte.parseByte( "100001", 2 ) },
            { "(short)0b0110000101000101", Short.parseShort( "0110000101000101", 2 ) },
            { "0b101", Integer.parseInt( "101", 2 ) },
            { "0B101", Integer.parseInt( "101", 2 ) },
            { "0b00010000101000101101000010100010110100001010001011010000101000101L",
                Long.parseLong( "00010000101000101101000010100010110100001010001011010000101000101", 2 ) },

            // Underscores in Numeric Literals (added to JDK7)
            { "1234_5678", Integer.parseInt( "12345678" ) },
            { "1234_5678_9012_3456L", Long.valueOf( "1234567890123456" ) },
            { "0xFF_EC_DE_5EL", Long.valueOf( "FFECDE5E", 16 ) },
            { "0x7fff_ffff_ffff_ffffL", Long.valueOf( "7fffffffffffffff", 16 ) },
            { "(byte) 0b0010_0101 ", Byte.parseByte( "00100101", 2 ) }, { "5_______2", Integer.parseInt( "52" ) },
            { "0x5_2", Integer.parseInt( "52", 16 ) }, { "0_52", Integer.parseInt( "52", 8 ) },
            { "05_2", Integer.parseInt( "52", 8 ) }, } );
    }

    @Test
    public void testExpression()
    {
        assertAnnotationExpression( expression, parsedResult );
    }

    protected void assertAnnotationExpression( String expression, Object expected )
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
        assertEquals( "Annotations", 1, clazz.getAnnotations().size() );
        JavaAnnotation annotation = clazz.getAnnotations().get( 0 );
        assertEquals( "Annotation name", "Annotation", annotation.getType().getFullyQualifiedName() );
        assertEquals( "Properties", 1, annotation.getPropertyMap().size() );

        AnnotationValue value = annotation.getProperty( "value" );
        Object v = value.accept( new EvaluatingVisitor() );
        assertEquals( "Value", expected, v );
    }
}