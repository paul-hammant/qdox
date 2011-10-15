package com.thoughtworks.qdox.model.expression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;


public class QueryTest
{

    @Test
    public void testParameterValue()
    {
        AnnotationValue condition = mock( AnnotationValue.class );
        AnnotationValue trueExpr = mock( AnnotationValue.class );
        AnnotationValue falseExpr = mock( AnnotationValue.class );
        when( condition.getParameterValue() ).thenReturn( "predicate" );
        when( trueExpr.getParameterValue() ).thenReturn( "consequent" );
        when( falseExpr.getParameterValue() ).thenReturn( "alternative" );
        Query expr = new Query( condition, trueExpr, falseExpr );
        assertEquals( "predicate ? consequent : alternative", expr.getParameterValue() );
    }

    @Test
    public void testToString()
    {
        AnnotationValue condition = mock( AnnotationValue.class );
        AnnotationValue trueExpr = mock( AnnotationValue.class );
        AnnotationValue falseExpr = mock( AnnotationValue.class );
        Query expr = new Query( condition, trueExpr, falseExpr );
        assertEquals( condition + " ? " + trueExpr + " : " + falseExpr, expr.toString() );
    }
    
    @Test
    public void testAccept()
    {
        ExpressionVisitor visitor = mock( ExpressionVisitor.class );
        Query expr = new Query( null, null, null );
        Object visitResult = new Object();
        when( visitor.visit( expr ) ).thenReturn( visitResult );
        assertSame( expr.accept( visitor ), visitResult );
    }
}
