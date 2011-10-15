package com.thoughtworks.qdox.model.expression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;


public class GreaterEqualsTest
{

    @Test
    public void testParameterValue()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );
        when( lhs.getParameterValue() ).thenReturn( "2" );
        when( rhs.getParameterValue() ).thenReturn( "3" );
        GreaterEquals expr = new GreaterEquals( lhs, rhs );
        assertEquals( "2 >= 3", expr.getParameterValue() );
    }

    @Test
    public void testToString()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );
        GreaterEquals expr = new GreaterEquals( lhs, rhs );
        assertEquals( lhs + " >= " + rhs, expr.toString() );
    }
    
    @Test
    public void testAccept()
    {
        ExpressionVisitor visitor = mock( ExpressionVisitor.class );
        GreaterEquals expr = new GreaterEquals( null, null );
        Object visitResult = new Object();
        when( visitor.visit( expr ) ).thenReturn( visitResult );
        assertSame( expr.accept( visitor ), visitResult );
    }
}
