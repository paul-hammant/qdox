package com.thoughtworks.qdox.builder;

import static org.junit.Assert.*;
import org.junit.Test;

public class EvaluatingVisitorTest
{

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
}
