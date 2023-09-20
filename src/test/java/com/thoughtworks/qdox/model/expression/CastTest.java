package com.thoughtworks.qdox.model.expression;

import com.thoughtworks.qdox.model.JavaType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CastTest
{
    @Test
    public void testParameterValue()
    {
        JavaType type = mock(JavaType.class);
        AnnotationValue value = mock( AnnotationValue.class );
        when( type.getCanonicalName() ).thenReturn( "int" );
        when( value.getParameterValue() ).thenReturn( "3" );
        Cast expr = new Cast( type, value );
        Assertions.assertEquals("(int) 3", expr.getParameterValue());
    }

    @Test
    public void testToString()
    {
        JavaType type = mock(JavaType.class);
        AnnotationValue value = mock( AnnotationValue.class );
        Cast expr = new Cast( type, value );
        Assertions.assertEquals("(" +type+ ") " + value, expr.toString());
    }
    
    @Test
    public void testAccept()
    {
        ExpressionVisitor visitor = mock( ExpressionVisitor.class );
        Cast expr = new Cast( null, null );
        Object visitResult = new Object();
        when( visitor.visit( expr ) ).thenReturn( visitResult );
        Assertions.assertSame(expr.accept( visitor ), visitResult);
    }
}
