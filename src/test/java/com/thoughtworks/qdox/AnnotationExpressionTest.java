package com.thoughtworks.qdox;

import static org.junit.Assert.*;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.parser.expression.AnnotationValue;
import com.thoughtworks.qdox.parser.expression.EvaluatingVisitor;

@RunWith(Parameterized.class)
public class AnnotationExpressionTest {

	private JavaProjectBuilder builder;
	
	private String expression;
	private Object parsedResult;
	
	public AnnotationExpressionTest(String expression, Object parsedResult) {
		this.expression = expression;
		this.parsedResult = parsedResult;
	}

	@Before
	public void setUp() throws Exception {
        builder = new JavaProjectBuilder();
        builder.setDebugLexer( true );
        builder.setDebugParser( true );
    }

	@Parameters
	public static Collection<?> createParameters() {
		return Arrays.asList(new Object[][] {
				// testPrecedence
				{"2 + 2 * 5", new Integer( 12 )},
				{"2 * 5 + 2", new Integer( 12 )},
				{"2+2*5", new Integer( 12 )},
				{"2*5+2", new Integer( 12 )},

			    // testLogicalExpression
				{"true && false", Boolean.FALSE},
				{"true || false", Boolean.TRUE},
				{"!true", Boolean.FALSE},
				
			    // testBitExpression
				{"1 & 3", new Integer( 1 & 3 )},
				{"1 | 3", new Integer( 1 | 3 )},
				{"1 ^ 3", new Integer( 1 ^ 3 )},
				{"~1", new Integer( ~1 )},
				
			    // testSignExpression
				{"+1", new Integer( 1 )},
				{"-1", new Integer( -1 )},
			    {"--1", new Integer( 1 )},
				
			    // testAddSubMultDivExpression
			    {"8 / 3", new Integer( 8 / 3 )},
			    {"8 * 3", new Integer( 8 * 3 )},
			    {"8 + 3", new Integer( 8 + 3 )},
			    {"8 - 3", new Integer( 8 - 3 )},
			    {"8 % 3", new Integer( 8 % 3 )},
			    {"\"a\" + \"b\"", "a" + "b"},


			    // testShiftExpression
			    {"8 << 2", new Integer( 8 << 2 )},
			    {"8 >> 2", new Integer( 8 >> 2 )},
			    {"-1 >> 2", new Integer( -1 >> 2 )},
			    {"-1 >>> 2", new Integer( -1 >>> 2 )},

			    // testLiteral
			    {"1", new Integer( 1 )},
			    {"1l", new Long( 1 )},
			    {"1.0", new Float( 1 )},
			    {"1.0d", new Double( 1 )},

			    // testParenExpression
			    {"2 + (2 * 5)", new Integer( 12 )},
			    {"(2 + 2) * 5", new Integer( 20 )},

			    // testCompareExpression
			    {"1 < 2", Boolean.TRUE},
			    {"1 > 2", Boolean.FALSE},
			    {"1 <= 2", Boolean.TRUE},
			    {"1 >= 2", Boolean.FALSE},
			    {"1 == 2", Boolean.FALSE},
			    {"1 != 2", Boolean.TRUE},
			    
			    // testQueryExpression
			    {"1 < 2 ? 0 : 3", new Integer( 0 )},
			    {"1 > 2 ? 0 : 3", new Integer( 3 )},

			    // testCastExpression
			    {"(short)1", new Short( (short) 1 )},
			    {"(long)(short)1", new Long( 1 )},
			    {"(int)((short)1 + (long)3)", new Integer( 4 )},
			    
			    //test Array-valued single-element annotation
			    { "{\"Children\", \"Unscrupulous dentists\"}", Arrays.asList(new String[]{"Children", "Unscrupulous dentists"})},

			    //test Array-valued annotation ending with a comma
			    { "{\"deprecated\", }", Arrays.asList(new String[]{"deprecated"})}
		});
	}

	@Test
    public void testExpression() {
        assertAnnotationExpression( expression, parsedResult );
    }

    protected void assertAnnotationExpression( String expression, Object expected ) {
        String source = "@Annotation(\n" + expression + "\n) class Foo {}";
        builder.addSource( new StringReader( source ) );
        assertAnnotationValue( expected );
    }

    protected void assertAnnotationValue( Object expected ) {
        JavaClass clazz = builder.getClassByName( "Foo" );
        assertEquals( "Annotations", 1, clazz.getAnnotations().size() );
        JavaAnnotation annotation = clazz.getAnnotations().get(0);
        assertEquals( "Annotation name", "Annotation", annotation.getType().getJavaClass().getFullyQualifiedName() );
        assertEquals( "Properties", 1, annotation.getPropertyMap().size() );

        AnnotationValue value = annotation.getProperty( "value" );
        Object v = value.accept( evaluatingVisitor );
        assertEquals( "Value", expected, v );
    }

    private EvaluatingVisitor evaluatingVisitor = new EvaluatingVisitor() {
        protected Object getFieldReferenceValue( JavaField javaField ) {
            throw new UnsupportedOperationException();
        }
    };

}
