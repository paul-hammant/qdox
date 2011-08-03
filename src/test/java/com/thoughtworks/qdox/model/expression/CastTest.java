package com.thoughtworks.qdox.model.expression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.thoughtworks.qdox.model.Type;


public class CastTest
{
    @Test
    public void testParameterValue()
    {
        Type type = mock(Type.class);
        AnnotationValue value = mock( AnnotationValue.class );
        when( type.getCanonicalName() ).thenReturn( "int" );
        when( value.getParameterValue() ).thenReturn( "3" );
        Cast expr = new Cast( type, value );
        assertEquals( "(int) 3", expr.getParameterValue() );
    }

    @Test
    public void testToString()
    {
        Type type = mock(Type.class);
        AnnotationValue value = mock( AnnotationValue.class );
        Cast expr = new Cast( type, value );
        assertEquals( "(" +type+ ") " + value, expr.toString() );
    }
    
    @Test
    public void testAccept()
    {
        AnnotationVisitor visitor = mock( AnnotationVisitor.class );
        Cast expr = new Cast( null, null );
        Object visitResult = new Object();
        when( visitor.visit( expr ) ).thenReturn( visitResult );
        assertSame( expr.accept( visitor ), visitResult );
    }
}
