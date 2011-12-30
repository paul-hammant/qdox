package com.thoughtworks.qdox.model.expression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

public class AssignmentTest
{
    @Test
    public void testParameterValue()
    {
        Expression lhs = mock( Expression.class );
        Expression rhs = mock( Expression.class );
        when( lhs.getParameterValue() ).thenReturn( "2" );
        when( rhs.getParameterValue() ).thenReturn( "3" );
        Assignment expr = new Assignment( lhs, ">>>=", rhs );
        assertEquals( "2 >>>= 3", expr.getParameterValue() );
    }

    @Test
    public void testToString()
    {
        Expression lhs = mock( Expression.class );
        Expression rhs = mock( Expression.class );
        Assignment expr = new Assignment( lhs, "+=",rhs );
        assertEquals( lhs + " += " + rhs, expr.toString() );
    }
    
    @Test
    public void testAccept()
    {
        ExpressionVisitor visitor = mock( ExpressionVisitor.class );
        Assignment expr = new Assignment( null, null, null );
        Object visitResult = new Object();
        when( visitor.visit( expr ) ).thenReturn( visitResult );
        assertSame( expr.accept( visitor ), visitResult );
    }
}