package com.thoughtworks.qdox.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.Type;
import com.thoughtworks.qdox.model.expression.Add;
import com.thoughtworks.qdox.model.expression.And;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import com.thoughtworks.qdox.model.expression.Cast;
import com.thoughtworks.qdox.model.expression.Divide;
import com.thoughtworks.qdox.model.expression.Equals;
import com.thoughtworks.qdox.model.expression.ExclusiveOr;
import com.thoughtworks.qdox.model.expression.GreaterEquals;
import com.thoughtworks.qdox.model.expression.GreaterThan;
import com.thoughtworks.qdox.model.expression.LessEquals;
import com.thoughtworks.qdox.model.expression.LessThan;
import com.thoughtworks.qdox.model.expression.LogicalAnd;
import com.thoughtworks.qdox.model.expression.LogicalNot;
import com.thoughtworks.qdox.model.expression.LogicalOr;
import com.thoughtworks.qdox.model.expression.MinusSign;
import com.thoughtworks.qdox.model.expression.Multiply;
import com.thoughtworks.qdox.model.expression.Not;
import com.thoughtworks.qdox.model.expression.NotEquals;
import com.thoughtworks.qdox.model.expression.Or;
import com.thoughtworks.qdox.model.expression.ParenExpression;
import com.thoughtworks.qdox.model.expression.PlusSign;
import com.thoughtworks.qdox.model.expression.Query;
import com.thoughtworks.qdox.model.expression.Remainder;
import com.thoughtworks.qdox.model.expression.ShiftLeft;
import com.thoughtworks.qdox.model.expression.ShiftRight;
import com.thoughtworks.qdox.model.expression.Subtract;
import com.thoughtworks.qdox.model.expression.UnsignedShiftRight;
import com.thoughtworks.qdox.model.impl.DefaultJavaAnnotation;

public class EvaluatingVisitorTest
{
    private EvaluatingVisitor visitor = new EvaluatingVisitorStub();
    
    @Test
    public void testUnaryNumericResultTypeInteger()
        throws Exception
    {
        assertEquals( Integer.class, EvaluatingVisitor.unaryNumericResultType( 0 ) );
        assertEquals( Integer.class, EvaluatingVisitor.unaryNumericResultType( (byte) 0 ) );
        assertEquals( Integer.class, EvaluatingVisitor.unaryNumericResultType( (short) 0 ) );
        assertEquals( Integer.class, EvaluatingVisitor.unaryNumericResultType( (char) 0 ) );
    }

    @Test
    public void testUnaryNumericResultTypeLong()
        throws Exception
    {
        assertEquals( Long.class, EvaluatingVisitor.unaryNumericResultType( 0L ) );
    }

    @Test
    public void testUnaryNumericResultTypeVoid()
        throws Exception
    {
        assertEquals( void.class, EvaluatingVisitor.unaryNumericResultType( new Object() ) );
        assertEquals( void.class, EvaluatingVisitor.unaryNumericResultType( (double) 0 ) );
        assertEquals( void.class, EvaluatingVisitor.unaryNumericResultType( (float) 0 ) );
        assertEquals( void.class, EvaluatingVisitor.unaryNumericResultType( null ) );
    }

    @Test
    public void testUnaryResultTypeInteger()
        throws Exception
    {
        assertEquals( Integer.class, EvaluatingVisitor.unaryResultType( 0 ) );
        assertEquals( Integer.class, EvaluatingVisitor.unaryResultType( (byte) 0 ) );
        assertEquals( Integer.class, EvaluatingVisitor.unaryResultType( (short) 0 ) );
        assertEquals( Integer.class, EvaluatingVisitor.unaryResultType( (char) 0 ) );
    }

    @Test
    public void testUnaryResultTypeLong()
        throws Exception
    {
        assertEquals( Long.class, EvaluatingVisitor.unaryResultType( 0L ) );
    }

    @Test
    public void testUnaryResultTypeDouble()
        throws Exception
    {
        assertEquals( Double.class, EvaluatingVisitor.unaryResultType( (double) 0 ) );
    }

    @Test
    public void testUnaryResultTypeFloat()
        throws Exception
    {
        assertEquals( Float.class, EvaluatingVisitor.unaryResultType( (float) 0 ) );
    }

    @Test
    public void testUnaryResultTypeVoid()
        throws Exception
    {
        assertEquals( void.class, EvaluatingVisitor.unaryResultType( new Object() ) );
        assertEquals( void.class, EvaluatingVisitor.unaryResultType( null ) );
    }
    
    @Test
    public void testNumericResultTypeLong()
        throws Exception
    {
        assertEquals( Long.class, EvaluatingVisitor.numericResultType( (long) 0, (long) 0 ) );
        assertEquals( Long.class, EvaluatingVisitor.numericResultType( (int) 0, (long) 0 ) );
        assertEquals( Long.class, EvaluatingVisitor.numericResultType( (long) 0, (int) 0 ) );
    }
    
    @Test
    public void testNumericResultTypeInteger()
        throws Exception
    {
        assertEquals( Integer.class, EvaluatingVisitor.numericResultType( (int) 0, (int) 0 ) );
        assertEquals( Integer.class, EvaluatingVisitor.numericResultType( (short) 0, (int) 0 ) );
        assertEquals( Integer.class, EvaluatingVisitor.numericResultType( (int) 0, (short) 0 ) );
    }
    
    @Test
    public void testNumericResultTypeVoid()
        throws Exception
    {
        assertEquals( void.class, EvaluatingVisitor.numericResultType( (double) 0, (double) 0 ) );
        assertEquals( void.class, EvaluatingVisitor.numericResultType( (float) 0, (double) 0 ) );
        assertEquals( void.class, EvaluatingVisitor.numericResultType( (double) 0, (float) 0 ) );
        assertEquals( void.class, EvaluatingVisitor.numericResultType( (float) 0, (float) 0 ) );

        assertEquals( void.class, EvaluatingVisitor.numericResultType( (double) 0, new Object() ) );
        assertEquals( void.class, EvaluatingVisitor.numericResultType( (float) 0, new Object() ) );
        assertEquals( void.class, EvaluatingVisitor.numericResultType( (long) 0, new Object() ) );
        assertEquals( void.class, EvaluatingVisitor.numericResultType( (int) 0, new Object() ) );

        assertEquals( void.class, EvaluatingVisitor.numericResultType( new Object(), (double) 0 ) );
        assertEquals( void.class, EvaluatingVisitor.numericResultType( new Object(), (float) 0 ) );
        assertEquals( void.class, EvaluatingVisitor.numericResultType( new Object(), (long) 0 ) );
        assertEquals( void.class, EvaluatingVisitor.numericResultType( new Object(), (int) 0 ) );
        
        assertEquals( void.class, EvaluatingVisitor.numericResultType( (double) 0, null ) );
        assertEquals( void.class, EvaluatingVisitor.numericResultType( (float) 0, null ) );
        assertEquals( void.class, EvaluatingVisitor.numericResultType( (long) 0, null ) );
        assertEquals( void.class, EvaluatingVisitor.numericResultType( (int) 0, null ) );

        assertEquals( void.class, EvaluatingVisitor.numericResultType( null, (double) 0 ) );
        assertEquals( void.class, EvaluatingVisitor.numericResultType( null, (float) 0 ) );
        assertEquals( void.class, EvaluatingVisitor.numericResultType( null, (long) 0 ) );
        assertEquals( void.class, EvaluatingVisitor.numericResultType( null, (int) 0 ) );
    }

    @Test
    public void testResultTypeDouble()
        throws Exception
    {
        // If either operand is of type double, the other is converted to double.
        assertEquals( Double.class, EvaluatingVisitor.resultType( (double) 0, (double) 0 ) );
        assertEquals( Double.class, EvaluatingVisitor.resultType( (float) 0, (double) 0 ) );
        assertEquals( Double.class, EvaluatingVisitor.resultType( (int) 0, (double) 0 ) );
        assertEquals( Double.class, EvaluatingVisitor.resultType( (double) 0, (float) 0 ) );
        assertEquals( Double.class, EvaluatingVisitor.resultType( (double) 0, (int) 0 ) );
    }

    @Test
    public void testResultTypeFloat()
        throws Exception
    {
        // Otherwise, if either operand is of type float, the other is converted to float.
        assertEquals( Float.class, EvaluatingVisitor.resultType( (float) 0, (float) 0 ) );
        assertEquals( Float.class, EvaluatingVisitor.resultType( (int) 0, (float) 0 ) );
        assertEquals( Float.class, EvaluatingVisitor.resultType( (float) 0, (int) 0 ) );
    }

    @Test
    public void testResultTypeLong()
        throws Exception
    {
        // Otherwise, if either operand is of type long, the other is converted to long.
        assertEquals( Long.class, EvaluatingVisitor.resultType( (long) 0, (long) 0 ) );
        assertEquals( Long.class, EvaluatingVisitor.resultType( (int) 0, (long) 0 ) );
        assertEquals( Long.class, EvaluatingVisitor.resultType( (long) 0, (int) 0 ) );
    }

    @Test
    public void testResultTypeInteger()
        throws Exception
    {
        // Otherwise, if either operand is of type long, the other is converted to long.
        assertEquals( Integer.class, EvaluatingVisitor.resultType( (int) 0, (int) 0 ) );
        assertEquals( Integer.class, EvaluatingVisitor.resultType( (short) 0, (int) 0 ) );
        assertEquals( Integer.class, EvaluatingVisitor.resultType( (int) 0, (short) 0 ) );
    }
    
    @Test
    public void testResultTypeVoid()
        throws Exception
    {
        // Otherwise, if either operand is of type long, the other is converted to long.
        assertEquals( void.class, EvaluatingVisitor.resultType( (double) 0, new Object() ) );
        assertEquals( void.class, EvaluatingVisitor.resultType( (float) 0, new Object() ) );
        assertEquals( void.class, EvaluatingVisitor.resultType( (long) 0, new Object() ) );
        assertEquals( void.class, EvaluatingVisitor.resultType( (int) 0, new Object() ) );

        assertEquals( void.class, EvaluatingVisitor.resultType( new Object(), (double) 0 ) );
        assertEquals( void.class, EvaluatingVisitor.resultType( new Object(), (float) 0 ) );
        assertEquals( void.class, EvaluatingVisitor.resultType( new Object(), (long) 0 ) );
        assertEquals( void.class, EvaluatingVisitor.resultType( new Object(), (int) 0 ) );
        
        assertEquals( void.class, EvaluatingVisitor.resultType( (double) 0, null ) );
        assertEquals( void.class, EvaluatingVisitor.resultType( (float) 0, null ) );
        assertEquals( void.class, EvaluatingVisitor.resultType( (long) 0, null ) );
        assertEquals( void.class, EvaluatingVisitor.resultType( (int) 0, null ) );

        assertEquals( void.class, EvaluatingVisitor.resultType( null, (double) 0 ) );
        assertEquals( void.class, EvaluatingVisitor.resultType( null, (float) 0 ) );
        assertEquals( void.class, EvaluatingVisitor.resultType( null, (long) 0 ) );
        assertEquals( void.class, EvaluatingVisitor.resultType( null, (int) 0 ) );
    }
    
    @Test
    public void testVisitAdd()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Doubles
        when( lhs.accept( any( EvaluatingVisitor.class ) ) ).thenReturn( 7.0D );
        when( rhs.accept( any( EvaluatingVisitor.class ) ) ).thenReturn( 2.0D );
        assertEquals( 7.0D + 2.0D, visitor.visit(  new Add( lhs, rhs ) ) );

        // Floats
        when( lhs.accept( any( EvaluatingVisitor.class ) ) ).thenReturn( 7.0F );
        when( rhs.accept( any( EvaluatingVisitor.class ) ) ).thenReturn( 2.0F );
        assertEquals( 7.0F + 2.0F, visitor.visit(  new Add( lhs, rhs ) ) );

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        assertEquals( 7L + 2L, visitor.visit(  new Add( lhs, rhs ) ) );

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        assertEquals( 7 + 2, visitor.visit(  new Add( lhs, rhs ) ) );

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new Add( lhs, rhs ) );
            fail( "Additive operations (+  and -) can only be performed on numeric types" );
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitAnd()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        assertEquals( 7L & 2L, visitor.visit(  new And( lhs, rhs ) ) );

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        assertEquals( 7 & 2, visitor.visit(  new And( lhs, rhs ) ) );

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new And( lhs, rhs ) );
            fail( "The and(&) operator can only be performed on integral types" );
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }

    @Test
    public void testVisitAnnotation()
    {
        try{
            visitor.visit( new DefaultJavaAnnotation( new Type("Ignore"), -1 ) );
            fail( "Visiting an annotation is not supported and should throw an UnsupportedOperationException" );
        }
        catch (UnsupportedOperationException e) {
        }
    }
    
    @Ignore
    @Test
    public void testVisitAnnotationValueList() {
    }

    @Test
    public void testVisitCast()
    {
        AnnotationValue value = mock( AnnotationValue.class );
        when( value.accept( visitor ) ).thenReturn( 7 );

        assertEquals( (byte) 7, visitor.visit( new Cast( new Type( "byte" ), value ) ) );
        assertEquals( (char) 7, visitor.visit( new Cast( new Type( "char" ), value ) ) );
        assertEquals( (short) 7, visitor.visit( new Cast( new Type( "short" ), value ) ) );
        assertEquals( (int) 7, visitor.visit( new Cast( new Type( "int" ), value ) ) );
        assertEquals( (long) 7, visitor.visit( new Cast( new Type( "long" ), value ) ) );
        assertEquals( (float) 7, visitor.visit( new Cast( new Type( "float" ), value ) ) );
        assertEquals( (double) 7, visitor.visit( new Cast( new Type( "double" ), value ) ) );

        try
        {
            visitor.visit( new Cast( new Type( "void" ), value ) );
            fail("Although 'void' is a primitive, you can't cast to it");
        }
        catch( IllegalArgumentException iae) 
        {            
        }
        
        when( value.accept( visitor ) ).thenReturn( "hello world" );
        assertEquals( (String) "hello world", visitor.visit( new Cast( new Type( "java.lang.String" ), value ) ) );

        Object list = Collections.EMPTY_LIST;
        when( value.accept( visitor ) ).thenReturn( list );
        assertEquals( (List<?>) list, visitor.visit( new Cast( new Type( "java.util.List" ), value ) ) );

    }
    
    @Test
    public void testVisitDivide()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Doubles
        when( lhs.accept( visitor ) ).thenReturn( 7.0D );
        when( rhs.accept( visitor ) ).thenReturn( 2.0D );
        assertEquals( 7.0D / 2.0D, visitor.visit(  new Divide( lhs, rhs ) ) );

        // Floats
        when( lhs.accept( visitor ) ).thenReturn( 7.0F );
        when( rhs.accept( visitor ) ).thenReturn( 2.0F );
        assertEquals( 7.0F / 2.0F, visitor.visit(  new Divide( lhs, rhs ) ) );

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        assertEquals( 7L / 2L, visitor.visit(  new Divide( lhs, rhs ) ) );

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        assertEquals( 7 / 2, visitor.visit(  new Divide( lhs, rhs ) ) );

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new Divide( lhs, rhs ) );
            fail( "The divide(/) operator can only be performed on numeric types" );
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitEquals() 
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Doubles
        when( lhs.accept( visitor ) ).thenReturn( 7.0D );
        when( rhs.accept( visitor ) ).thenReturn( 2.0D );
        assertEquals( 7.0D == 2.0D, visitor.visit(  new Equals( lhs, rhs ) ) );

        when( rhs.accept( visitor ) ).thenReturn( 7.0D );
        assertEquals( 7.0D == 7.0D, visitor.visit(  new Equals( lhs, rhs ) ) );

        // Floats
        when( lhs.accept( visitor ) ).thenReturn( 7.0F );
        when( rhs.accept( visitor ) ).thenReturn( 2.0F );
        assertEquals( 7.0F == 2.0F, visitor.visit(  new Equals( lhs, rhs ) ) );

        when( rhs.accept( visitor ) ).thenReturn( 7.0F );
        assertEquals( 7.0F == 7.0F, visitor.visit(  new Equals( lhs, rhs ) ) );

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        assertEquals( 7L == 2L, visitor.visit(  new Equals( lhs, rhs ) ) );

        when( rhs.accept( visitor ) ).thenReturn( 7L );
        assertEquals( 7L == 7L, visitor.visit(  new Equals( lhs, rhs ) ) );

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        assertEquals( 7 == 2, visitor.visit(  new Equals( lhs, rhs ) ) );

        when( rhs.accept( visitor ) ).thenReturn( 7 );
        assertEquals( 7 == 7, visitor.visit(  new Equals( lhs, rhs ) ) );

        // Objects
        Object object1 = new Object();
        Object object2 = new Object();
        when( lhs.accept( visitor ) ).thenReturn( object1 );
        when( rhs.accept( visitor ) ).thenReturn( object2 );
        assertEquals( object1 == object2, visitor.visit(  new Equals( lhs, rhs ) ) );

        when( rhs.accept( visitor ) ).thenReturn( object1 );
        assertEquals( object1 == object1, visitor.visit(  new Equals( lhs, rhs ) ) );
    }

    @Test
    public void testVisitExlusiveOr() 
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        assertEquals( 7L ^ 2L, visitor.visit(  new ExclusiveOr( lhs, rhs ) ) );

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        assertEquals( 7 ^ 2, visitor.visit(  new ExclusiveOr( lhs, rhs ) ) );

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new ExclusiveOr( lhs, rhs ) );
            fail( "The exclusive-or(^) operator can only be performed on integral types" );
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Ignore
    @Test
    public void testVisitFieldRef() {
    }
    
    
    @Test
    public void testVisitGreaterEquals()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Doubles
        when( lhs.accept( visitor ) ).thenReturn( 7.0D );
        when( rhs.accept( visitor ) ).thenReturn( 2.0D );
        assertEquals( 7.0D >= 2.0D, visitor.visit(  new GreaterEquals( lhs, rhs ) ) );

        when( rhs.accept( visitor ) ).thenReturn( 7.0D );
        assertEquals( 7.0D >= 7.0D, visitor.visit(  new GreaterEquals( lhs, rhs ) ) );
        
        when( lhs.accept( visitor ) ).thenReturn( 2.0D );
        assertEquals( 2.0D >= 7.0D, visitor.visit(  new GreaterEquals( lhs, rhs ) ) );

        // Floats
        when( lhs.accept( visitor ) ).thenReturn( 7.0F );
        when( rhs.accept( visitor ) ).thenReturn( 2.0F );
        assertEquals( 7.0F >= 2.0F, visitor.visit(  new GreaterEquals( lhs, rhs ) ) );

        when( rhs.accept( visitor ) ).thenReturn( 7.0F );
        assertEquals( 7.0F >= 7.0F, visitor.visit(  new GreaterEquals( lhs, rhs ) ) );

        when( lhs.accept( visitor ) ).thenReturn( 2.0F );
        assertEquals( 2.0F >= 7.0F, visitor.visit(  new GreaterEquals( lhs, rhs ) ) );

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        assertEquals( 7L >= 2L, visitor.visit(  new GreaterEquals( lhs, rhs ) ) );

        when( rhs.accept( visitor ) ).thenReturn( 7L );
        assertEquals( 7L >= 7L, visitor.visit(  new GreaterEquals( lhs, rhs ) ) );

        when( lhs.accept( visitor ) ).thenReturn( 2L );
        assertEquals( 2L >= 7L, visitor.visit(  new GreaterEquals( lhs, rhs ) ) );

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        assertEquals( 7 >= 2, visitor.visit(  new GreaterEquals( lhs, rhs ) ) );

        when( rhs.accept( visitor ) ).thenReturn( 7 );
        assertEquals( 7 >=7, visitor.visit(  new GreaterEquals( lhs, rhs ) ) );

        when( lhs.accept( visitor ) ).thenReturn( 2 );
        assertEquals( 2 >= 7, visitor.visit(  new GreaterEquals( lhs, rhs ) ) );

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new GreaterEquals( lhs, rhs ) );
            fail( "The greater-equals(>=) operator can only be performed on numeric types" );
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitGreaterThan()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Doubles
        when( lhs.accept( visitor ) ).thenReturn( 7.0D );
        when( rhs.accept( visitor ) ).thenReturn( 2.0D );
        assertEquals( 7.0D > 2.0D, visitor.visit(  new GreaterThan( lhs, rhs ) ) );

        when( rhs.accept( visitor ) ).thenReturn( 7.0D );
        assertEquals( 7.0D > 7.0D, visitor.visit(  new GreaterThan( lhs, rhs ) ) );
        
        when( lhs.accept( visitor ) ).thenReturn( 2.0D );
        assertEquals( 2.0D > 7.0D, visitor.visit(  new GreaterThan( lhs, rhs ) ) );

        // Floats
        when( lhs.accept( visitor ) ).thenReturn( 7.0F );
        when( rhs.accept( visitor ) ).thenReturn( 2.0F );
        assertEquals( 7.0F > 2.0F, visitor.visit(  new GreaterThan( lhs, rhs ) ) );

        when( rhs.accept( visitor ) ).thenReturn( 7.0F );
        assertEquals( 7.0F > 7.0F, visitor.visit(  new GreaterThan( lhs, rhs ) ) );

        when( lhs.accept( visitor ) ).thenReturn( 2.0F );
        assertEquals( 2.0F > 7.0F, visitor.visit(  new GreaterThan( lhs, rhs ) ) );

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        assertEquals( 7L > 2L, visitor.visit(  new GreaterThan( lhs, rhs ) ) );

        when( rhs.accept( visitor ) ).thenReturn( 7L );
        assertEquals( 7L > 7L, visitor.visit(  new GreaterThan( lhs, rhs ) ) );

        when( lhs.accept( visitor ) ).thenReturn( 2L );
        assertEquals( 2L > 7L, visitor.visit(  new GreaterThan( lhs, rhs ) ) );

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        assertEquals( 7 > 2, visitor.visit(  new GreaterThan( lhs, rhs ) ) );

        when( rhs.accept( visitor ) ).thenReturn( 7 );
        assertEquals( 7 > 7, visitor.visit(  new GreaterThan( lhs, rhs ) ) );

        when( lhs.accept( visitor ) ).thenReturn( 2 );
        assertEquals( 2 > 7, visitor.visit(  new GreaterThan( lhs, rhs ) ) );

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new GreaterThan( lhs, rhs ) );
            fail( "The greater-than(>) operator can only be performed on numeric types" );
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitLessEquals()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Doubles
        when( lhs.accept( visitor ) ).thenReturn( 7.0D );
        when( rhs.accept( visitor ) ).thenReturn( 2.0D );
        assertEquals( 7.0D <= 2.0D, visitor.visit(  new LessEquals( lhs, rhs ) ) );

        when( rhs.accept( visitor ) ).thenReturn( 7.0D );
        assertEquals( 7.0D <= 7.0D, visitor.visit(  new LessEquals( lhs, rhs ) ) );
        
        when( lhs.accept( visitor ) ).thenReturn( 2.0D );
        assertEquals( 2.0D <= 7.0D, visitor.visit(  new LessEquals( lhs, rhs ) ) );

        // Floats
        when( lhs.accept( visitor ) ).thenReturn( 7.0F );
        when( rhs.accept( visitor ) ).thenReturn( 2.0F );
        assertEquals( 7.0F <= 2.0F, visitor.visit(  new LessEquals( lhs, rhs ) ) );

        when( rhs.accept( visitor ) ).thenReturn( 7.0F );
        assertEquals( 7.0F <= 7.0F, visitor.visit(  new LessEquals( lhs, rhs ) ) );

        when( lhs.accept( visitor ) ).thenReturn( 2.0F );
        assertEquals( 2.0F <= 7.0F, visitor.visit(  new LessEquals( lhs, rhs ) ) );

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        assertEquals( 7L <= 2L, visitor.visit(  new LessEquals( lhs, rhs ) ) );

        when( rhs.accept( visitor ) ).thenReturn( 7L );
        assertEquals( 7L <= 7L, visitor.visit(  new LessEquals( lhs, rhs ) ) );

        when( lhs.accept( visitor ) ).thenReturn( 2L );
        assertEquals( 2L <= 7L, visitor.visit(  new LessEquals( lhs, rhs ) ) );

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        assertEquals( 7 <= 2, visitor.visit(  new LessEquals( lhs, rhs ) ) );

        when( rhs.accept( visitor ) ).thenReturn( 7 );
        assertEquals( 7 <= 7, visitor.visit(  new LessEquals( lhs, rhs ) ) );

        when( lhs.accept( visitor ) ).thenReturn( 2 );
        assertEquals( 2 <= 7, visitor.visit(  new LessEquals( lhs, rhs ) ) );

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new LessEquals( lhs, rhs ) );
            fail( "The less-equals(<=) operator can only be performed on numeric types" );
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitLessThan()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Doubles
        when( lhs.accept( visitor ) ).thenReturn( 7.0D );
        when( rhs.accept( visitor ) ).thenReturn( 2.0D );
        assertEquals( 7.0D < 2.0D, visitor.visit(  new LessThan( lhs, rhs ) ) );

        when( rhs.accept( visitor ) ).thenReturn( 7.0D );
        assertEquals( 7.0D < 7.0D, visitor.visit(  new LessThan( lhs, rhs ) ) );
        
        when( lhs.accept( visitor ) ).thenReturn( 2.0D );
        assertEquals( 2.0D < 7.0D, visitor.visit(  new LessThan( lhs, rhs ) ) );

        // Floats
        when( lhs.accept( visitor ) ).thenReturn( 7.0F );
        when( rhs.accept( visitor ) ).thenReturn( 2.0F );
        assertEquals( 7.0F < 2.0F, visitor.visit(  new LessThan( lhs, rhs ) ) );

        when( rhs.accept( visitor ) ).thenReturn( 7.0F );
        assertEquals( 7.0F < 7.0F, visitor.visit(  new LessThan( lhs, rhs ) ) );

        when( lhs.accept( visitor ) ).thenReturn( 2.0F );
        assertEquals( 2.0F < 7.0F, visitor.visit(  new LessThan( lhs, rhs ) ) );

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        assertEquals( 7L < 2L, visitor.visit(  new LessThan( lhs, rhs ) ) );

        when( rhs.accept( visitor ) ).thenReturn( 7L );
        assertEquals( 7L < 7L, visitor.visit(  new LessThan( lhs, rhs ) ) );

        when( lhs.accept( visitor ) ).thenReturn( 2L );
        assertEquals( 2L < 7L, visitor.visit(  new LessThan( lhs, rhs ) ) );

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        assertEquals( 7 < 2, visitor.visit(  new LessThan( lhs, rhs ) ) );

        when( rhs.accept( visitor ) ).thenReturn( 7 );
        assertEquals( 7 < 7, visitor.visit(  new LessThan( lhs, rhs ) ) );

        when( lhs.accept( visitor ) ).thenReturn( 2 );
        assertEquals( 2 < 7, visitor.visit(  new LessThan( lhs, rhs ) ) );

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new LessThan( lhs, rhs ) );
            fail( "The less-than(<) operator can only be performed on numeric types" );
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }

    @Test
    public void visitLogicalAnd()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        when( lhs.accept( visitor ) ).thenReturn( true );
        when( rhs.accept( visitor ) ).thenReturn( true );
        assertEquals( true && true, visitor.visit( new LogicalAnd( lhs, rhs ) ) );


        when( lhs.accept( visitor ) ).thenReturn( false );
        when( rhs.accept( visitor ) ).thenReturn( false );
        assertEquals( false && false, visitor.visit( new LogicalAnd( lhs, rhs ) ) );

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new LogicalAnd( lhs, rhs ) );
            fail( "The logical and(&&) operator can only be performed on booleans" );
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void visitLogicalNot()
    {
        AnnotationValue value = mock( AnnotationValue.class );

        when( value.accept( visitor ) ).thenReturn( true );
        assertEquals( !true, visitor.visit( new LogicalNot( value ) ) );

        when( value.accept( visitor ) ).thenReturn( false );
        assertEquals( !false, visitor.visit( new LogicalNot( value ) ) );

        // Objects
        when( value.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit( new LogicalNot( value ) );
            fail( "The logical not(!) operator can only be performed on booleans" );
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }

    @Test
    public void visitLogicalOr()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        when( lhs.accept( visitor ) ).thenReturn( true );
        when( rhs.accept( visitor ) ).thenReturn( true );
        assertEquals( true || true, visitor.visit( new LogicalOr( lhs, rhs ) ) );


        when( lhs.accept( visitor ) ).thenReturn( false );
        when( rhs.accept( visitor ) ).thenReturn( false );
        assertEquals( false || false, visitor.visit( new LogicalOr( lhs, rhs ) ) );

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new LogicalOr( lhs, rhs ) );
            fail( "The logical or(||) operator can only be performed on booleans" );
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitMinusSign()
    {
        AnnotationValue value = mock( AnnotationValue.class );
        
        // Double
        when( value.accept( visitor ) ).thenReturn( 7.0D );
        assertEquals( -7.0D, visitor.visit( new MinusSign( value ) ) );
        
        // Float
        when( value.accept( visitor ) ).thenReturn( 7.0F );
        assertEquals( -7.0F, visitor.visit( new MinusSign( value ) ) );
        
        // Long
        when( value.accept( visitor ) ).thenReturn( 7L );
        assertEquals( -7L, visitor.visit( new MinusSign( value ) ) );

        // Integer
        when( value.accept( visitor ) ).thenReturn( 7 );
        assertEquals( -7, visitor.visit( new MinusSign( value ) ) );
        
        when( value.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit( new MinusSign( value ) );
            fail( "The minus(-) sign operator can only be performed on numeric types" );
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitMultiply()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Doubles
        when( lhs.accept( visitor ) ).thenReturn( 7.0D );
        when( rhs.accept( visitor ) ).thenReturn( 2.0D );
        assertEquals( 7.0D * 2.0D, visitor.visit(  new Multiply( lhs, rhs ) ) );

        // Floats
        when( lhs.accept( visitor ) ).thenReturn( 7.0F );
        when( rhs.accept( visitor ) ).thenReturn( 2.0F );
        assertEquals( 7.0F * 2.0F, visitor.visit(  new Multiply( lhs, rhs ) ) );

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        assertEquals( 7L * 2L, visitor.visit(  new Multiply( lhs, rhs ) ) );

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        assertEquals( 7 * 2, visitor.visit(  new Multiply( lhs, rhs ) ) );

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new Multiply( lhs, rhs ) );
            fail( "The multiply(*) operator can only be performed on numeric types" );
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitNot() 
    {
        AnnotationValue value = mock( AnnotationValue.class );
        
        // Longs
        when( value.accept( visitor ) ).thenReturn( 7L );
        assertEquals( ~7L, visitor.visit(  new Not( value ) ) );

        // Integers
        when( value.accept( visitor ) ).thenReturn( 7 );
        assertEquals( ~7, visitor.visit(  new Not( value) ) );

        // Objects
        when( value.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new Not( value ) );
            fail( "The not(~) operator can only be performed on integral types" );
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitNotEquals()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Doubles
        when( lhs.accept( visitor ) ).thenReturn( 7.0D );
        when( rhs.accept( visitor ) ).thenReturn( 2.0D );
        assertEquals( 7.0D != 2.0D, visitor.visit(  new NotEquals( lhs, rhs ) ) );

        when( rhs.accept( visitor ) ).thenReturn( 7.0D );
        assertEquals( 7.0D != 7.0D, visitor.visit(  new NotEquals( lhs, rhs ) ) );

        // Floats
        when( lhs.accept( visitor ) ).thenReturn( 7.0F );
        when( rhs.accept( visitor ) ).thenReturn( 2.0F );
        assertEquals( 7.0F != 2.0F, visitor.visit(  new NotEquals( lhs, rhs ) ) );

        when( rhs.accept( visitor ) ).thenReturn( 7.0F );
        assertEquals( 7.0F != 7.0F, visitor.visit(  new NotEquals( lhs, rhs ) ) );

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        assertEquals( 7L != 2L, visitor.visit(  new NotEquals( lhs, rhs ) ) );

        when( rhs.accept( visitor ) ).thenReturn( 7L );
        assertEquals( 7L != 7L, visitor.visit(  new NotEquals( lhs, rhs ) ) );

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        assertEquals( 7 != 2, visitor.visit(  new NotEquals( lhs, rhs ) ) );

        when( rhs.accept( visitor ) ).thenReturn( 7 );
        assertEquals( 7 != 7, visitor.visit(  new NotEquals( lhs, rhs ) ) );

        // Objects
        Object object1 = new Object();
        Object object2 = new Object();
        when( lhs.accept( visitor ) ).thenReturn( object1 );
        when( rhs.accept( visitor ) ).thenReturn( object2 );
        assertEquals( object1 != object2, visitor.visit(  new NotEquals( lhs, rhs ) ) );

        when( rhs.accept( visitor ) ).thenReturn( object1 );
        assertEquals( object1 != object1, visitor.visit(  new NotEquals( lhs, rhs ) ) );
    }
    
    @Test
    public void testVisitOr()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        assertEquals( 7L | 2L, visitor.visit(  new Or( lhs, rhs ) ) );

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        assertEquals( 7 | 2, visitor.visit(  new Or( lhs, rhs ) ) );

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new Or( lhs, rhs ) );
            fail( "The or(|) operator can only be performed on integral types" );
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitParenExpression()
    {
        AnnotationValue value = mock( AnnotationValue.class );
        
        Object acceptResult = new Object();
        when( value.accept( visitor ) ).thenReturn( acceptResult );
        assertSame( acceptResult, visitor.visit( new ParenExpression( value ) ) );
    }
    
    @Test
    public void testVisitPlusSign() 
    {
        AnnotationValue value = mock( AnnotationValue.class );
        
        // Double
        when( value.accept( visitor ) ).thenReturn( 7.0D );
        assertEquals( 7.0D, visitor.visit( new PlusSign( value ) ) );
        
        // Float
        when( value.accept( visitor ) ).thenReturn( 7.0F );
        assertEquals( 7.0F, visitor.visit( new PlusSign( value ) ) );
        
        // Long
        when( value.accept( visitor ) ).thenReturn( 7L );
        assertEquals( 7L, visitor.visit( new PlusSign( value ) ) );

        // Integer
        when( value.accept( visitor ) ).thenReturn( 7 );
        assertEquals( 7, visitor.visit( new PlusSign( value ) ) );
        
        when( value.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit( new PlusSign( value ) );
            fail( "The plus sign operator can only be performed on numeric types" );
        }
        catch ( IllegalArgumentException iae )
        {

        }
    }
    
    @Test
    public void testVisitQuery()
    {
        AnnotationValue condition = mock( AnnotationValue.class );
        AnnotationValue trueExpr = mock( AnnotationValue.class );
        AnnotationValue falseExpr = mock( AnnotationValue.class );

        when( trueExpr.accept( visitor ) ).thenReturn( "consequent" );
        when( falseExpr.accept( visitor ) ).thenReturn( "alternative" );

        // true condition
        when( condition.accept( visitor ) ).thenReturn( Boolean.TRUE );
        assertEquals( "consequent", visitor.visit( new Query( condition, trueExpr, falseExpr ) ) );

        when( condition.accept( visitor ) ).thenReturn( true );
        assertEquals( "consequent", visitor.visit( new Query( condition, trueExpr, falseExpr ) ) );

        // false condition
        when( condition.accept( visitor ) ).thenReturn( Boolean.FALSE );
        assertEquals( "alternative", visitor.visit( new Query( condition, trueExpr, falseExpr ) ) );

        when( condition.accept( visitor ) ).thenReturn( false );
        assertEquals( "alternative", visitor.visit( new Query( condition, trueExpr, falseExpr ) ) );

        when( condition.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit( new Query( condition, trueExpr, falseExpr ) );
            fail( "The condition of the query( ? : ) must be a boolean" );
        }
        catch ( IllegalArgumentException iae )
        {

        }
    }
    
    @Test
    public void testVisitRemainder()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Doubles
        when( lhs.accept( visitor ) ).thenReturn( 7.0D );
        when( rhs.accept( visitor ) ).thenReturn( 2.0D );
        assertEquals( 7.0D % 2.0D, visitor.visit(  new Remainder( lhs, rhs ) ) );

        // Floats
        when( lhs.accept( visitor ) ).thenReturn( 7.0F );
        when( rhs.accept( visitor ) ).thenReturn( 2.0F );
        assertEquals( 7.0F % 2.0F, visitor.visit(  new Remainder( lhs, rhs ) ) );

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        assertEquals( 7L % 2L, visitor.visit(  new Remainder( lhs, rhs ) ) );

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        assertEquals( 7 % 2, visitor.visit(  new Remainder( lhs, rhs ) ) );

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new Remainder( lhs, rhs ) );
            fail( "The remainder(%) operator can only be performed on numeric types" );
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitShiftLeft()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        assertEquals( 7L << 2L, visitor.visit( new ShiftLeft( lhs, rhs ) ) );

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        assertEquals( 7 << 2, visitor.visit( new ShiftLeft( lhs, rhs ) ) );

        // Objects
        when( lhs.accept( any( EvaluatingVisitor.class ) ) ).thenReturn( new Object() );
        when( rhs.accept( any( EvaluatingVisitor.class ) ) ).thenReturn( new Object() );
        try
        {
            visitor.visit( new ShiftLeft( lhs, rhs ) );
            fail( "Bitwise and bit shift operations can only be performed on integral types" );
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitShiftRight()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );
        
        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        assertEquals( 7L >> 2L, visitor.visit( new ShiftRight( lhs, rhs ) ) );

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        assertEquals( 7 >> 2, visitor.visit( new ShiftRight( lhs, rhs ) ) );

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit( new ShiftRight( lhs, rhs ) );
            fail( "Bitwise and bit shift operations can only be performed on integral types" );
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitSubtract()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Doubles
        when( lhs.accept( any( EvaluatingVisitor.class ) ) ).thenReturn( 7.0D );
        when( rhs.accept( any( EvaluatingVisitor.class ) ) ).thenReturn( 2.0D );
        assertEquals( 7.0D - 2.0D, visitor.visit(  new Subtract( lhs, rhs ) ) );

        // Floats
        when( lhs.accept( any( EvaluatingVisitor.class ) ) ).thenReturn( 7.0F );
        when( rhs.accept( any( EvaluatingVisitor.class ) ) ).thenReturn( 2.0F );
        assertEquals( 7.0F - 2.0F, visitor.visit(  new Subtract( lhs, rhs ) ) );

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        assertEquals( 7L - 2L, visitor.visit(  new Subtract( lhs, rhs ) ) );

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        assertEquals( 7 - 2, visitor.visit(  new Subtract( lhs, rhs ) ) );

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new Subtract( lhs, rhs ) );
            fail( "Additive operations (+  and -) can only be performed on numeric types" );
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Ignore
    @Test
    public void testVisitTypeRef() {
        
    }

    @Test
    public void testVisitUnsignedShiftRight()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        assertEquals( 7L >>> 2L, visitor.visit(  new UnsignedShiftRight( lhs, rhs ) ) );

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        assertEquals( 7 >>> 2, visitor.visit(  new UnsignedShiftRight( lhs, rhs ) ) );

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new UnsignedShiftRight( lhs, rhs ) );
            fail( "Bitwise and bit shift operations can only be performed on integral types" );
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    private class EvaluatingVisitorStub extends EvaluatingVisitor {
        
        @Override
        protected Object getFieldReferenceValue( JavaField javaField )
        {
            return null;
        }
    }
}
