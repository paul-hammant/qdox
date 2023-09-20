package com.thoughtworks.qdox.model.expression;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class FieldRefTest
{

    @Test
    public void testParameterValue()
    {
        FieldRef expr = new FieldRef( "aField" );
        Assertions.assertEquals("aField", expr.getParameterValue());
    }

    @Test
    public void testToString()
    {
        FieldRef expr = new FieldRef( "aField" );
        Assertions.assertEquals("aField", expr.toString());
    }
    
    @Test
    public void testAccept()
    {
        ExpressionVisitor visitor = mock( ExpressionVisitor.class );
        FieldRef expr = new FieldRef( "" );
        Object visitResult = new Object();
        when( visitor.visit( expr ) ).thenReturn( visitResult );
        Assertions.assertSame(expr.accept( visitor ), visitResult);
    }
}
