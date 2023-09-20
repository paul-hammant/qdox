package com.thoughtworks.qdox.model.expression;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class PlusSignTest
{

    @Test
    public void testParameterValue()
    {
        AnnotationValue value = mock( AnnotationValue.class );
        when( value.getParameterValue() ).thenReturn( "2" );
        PlusSign expr = new PlusSign( value );
        Assertions.assertEquals("+2", expr.getParameterValue());
    }

    @Test
    public void testToString()
    {
        AnnotationValue value = mock( AnnotationValue.class );
        PlusSign expr = new PlusSign( value );
        Assertions.assertEquals("+" + value, expr.toString());
    }
    
    @Test
    public void testAccept()
    {
        ExpressionVisitor visitor = mock( ExpressionVisitor.class );
        PlusSign expr = new PlusSign( null );
        Object visitResult = new Object();
        when( visitor.visit( expr ) ).thenReturn( visitResult );
        Assertions.assertSame(expr.accept( visitor ), visitResult);
    }
}
