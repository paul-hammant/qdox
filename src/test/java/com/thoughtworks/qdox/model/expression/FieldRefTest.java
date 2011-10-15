package com.thoughtworks.qdox.model.expression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;


public class FieldRefTest
{

    @Test
    public void testParameterValue()
    {
        FieldRef expr = new FieldRef( "aField" );
        assertEquals( "aField", expr.getParameterValue() );
    }

    @Test
    public void testToString()
    {
        FieldRef expr = new FieldRef( "aField" );
        assertEquals( "aField", expr.toString() );
    }
    
    @Test
    public void testAccept()
    {
        ExpressionVisitor visitor = mock( ExpressionVisitor.class );
        FieldRef expr = new FieldRef( "" );
        Object visitResult = new Object();
        when( visitor.visit( expr ) ).thenReturn( visitResult );
        assertSame( expr.accept( visitor ), visitResult );
    }
}
