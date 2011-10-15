package com.thoughtworks.qdox.model.expression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;


public class LogicalNotTest
{

    @Test
    public void testParameterValue()
    {
        AnnotationValue value = mock( AnnotationValue.class );
        when( value.getParameterValue() ).thenReturn( "valid" );
        LogicalNot expr = new LogicalNot( value );
        assertEquals( "!valid", expr.getParameterValue() );
    }

    @Test
    public void testToString()
    {
        AnnotationValue value = mock( AnnotationValue.class );
        LogicalNot expr = new LogicalNot( value );
        assertEquals( "!" + value, expr.toString() );
    }
    
    @Test
    public void testAccept()
    {
        ExpressionVisitor visitor = mock( ExpressionVisitor.class );
        LogicalNot expr = new LogicalNot( null );
        Object visitResult = new Object();
        when( visitor.visit( expr ) ).thenReturn( visitResult );
        assertSame( expr.accept( visitor ), visitResult );
    }
}
