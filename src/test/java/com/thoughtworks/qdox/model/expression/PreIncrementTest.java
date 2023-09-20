package com.thoughtworks.qdox.model.expression;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PreIncrementTest
{
    @Test
    public void testParameterValue()
    {
        AnnotationValue value = mock( AnnotationValue.class );
        when( value.getParameterValue() ).thenReturn( "index" );
        PreIncrement expr = new PreIncrement( value );
        Assertions.assertEquals("++index", expr.getParameterValue());
    }

    @Test
    public void testToString()
    {
        AnnotationValue value = mock( AnnotationValue.class );
        when( value.getParameterValue() ).thenReturn( "index" );
        PreIncrement expr = new PreIncrement( value );
        Assertions.assertEquals("++" + value.toString(), expr.toString());
    }
    
    @Test
    public void testAccept()
    {
        ExpressionVisitor visitor = mock( ExpressionVisitor.class );
        PreIncrement expr = new PreIncrement( null );
        Object visitResult = new Object();
        when( visitor.visit( expr ) ).thenReturn( visitResult );
        Assertions.assertSame(expr.accept( visitor ), visitResult);
    }
}