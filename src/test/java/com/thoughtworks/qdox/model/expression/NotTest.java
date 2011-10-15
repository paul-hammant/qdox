package com.thoughtworks.qdox.model.expression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;


public class NotTest
{

    @Test
    public void testParameterValue()
    {
        AnnotationValue value = mock( AnnotationValue.class );
        when( value.getParameterValue() ).thenReturn( "valid" );
        Not expr = new Not( value );
        assertEquals( "~valid", expr.getParameterValue() );
    }

    @Test
    public void testToString()
    {
        AnnotationValue value = mock( AnnotationValue.class );
        Not expr = new Not( value );
        assertEquals( "~" + value, expr.toString() );
    }
    
    @Test
    public void testAccept()
    {
        ExpressionVisitor visitor = mock( ExpressionVisitor.class );
        Not expr = new Not( null );
        Object visitResult = new Object();
        when( visitor.visit( expr ) ).thenReturn( visitResult );
        assertSame( expr.accept( visitor ), visitResult );
    }
}
