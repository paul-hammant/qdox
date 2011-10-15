package com.thoughtworks.qdox.model.expression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;


public class AnnotationValueListTest
{

    @Test
    public void testParameterValue_emptyList()
    {
        AnnotationValueList  expr = new AnnotationValueList ( Collections.<AnnotationValue>emptyList() );
        assertEquals( Collections.<Object>emptyList(), expr.getParameterValue() );
    }

    @Test
    public void testToString_emptyList()
    {
        AnnotationValueList  expr = new AnnotationValueList ( Collections.<AnnotationValue>emptyList() );
        assertEquals( "{}", expr.toString() );
    }

    @Test
    public void testParameterValue_singletonList()
    {
        AnnotationValue value= mock( AnnotationValue.class );
        when( value.getParameterValue() ).thenReturn( "2" );
        AnnotationValueList  expr = new AnnotationValueList ( Collections.singletonList( value ) );
        assertEquals( Collections.singletonList( "2" ), expr.getParameterValue() );
    }

    @Test
    public void testToString_singletonList()
    {
        AnnotationValue value= mock( AnnotationValue.class );
        when( value.getParameterValue() ).thenReturn( "2" );
        AnnotationValueList  expr = new AnnotationValueList ( Collections.singletonList( value ) );
        assertEquals( "{" + value+ "}", expr.toString() );
    }
    
    @Test
    public void testParameterValue_twoElementsList()
    {
        AnnotationValue value1 = mock( AnnotationValue.class );
        when( value1.getParameterValue() ).thenReturn( "2" );
        AnnotationValue value2 = mock( AnnotationValue.class );
        when( value2.getParameterValue() ).thenReturn( "3" );
        List<AnnotationValue> actualList = new LinkedList<AnnotationValue>();
        actualList.add( value1 );
        actualList.add( value2 );
        AnnotationValueList expr = new AnnotationValueList( actualList );
        List<String> expectedParameterValue = new LinkedList<String>();
        expectedParameterValue.add( "2" );
        expectedParameterValue.add( "3" );
        assertEquals( expectedParameterValue, expr.getParameterValue() );
    }

    @Test
    public void testToString_twoElementsList()
    {
        AnnotationValue value1 = mock( AnnotationValue.class );
        when( value1.getParameterValue() ).thenReturn( "2" );
        AnnotationValue value2 = mock( AnnotationValue.class );
        when( value2.getParameterValue() ).thenReturn( "3" );
        List<AnnotationValue> actualList = new LinkedList<AnnotationValue>();
        actualList.add( value1 );
        actualList.add( value2 );
        AnnotationValueList expr = new AnnotationValueList( actualList );
        List<String> expectedParameterValue = new LinkedList<String>();
        expectedParameterValue.add( "2" );
        expectedParameterValue.add( "3" );
        assertEquals( "{" + value1+ ", " + value2 + "}", expr.toString() );
    }

    @Test
    public void testAccept()
    {
        ExpressionVisitor visitor = mock( ExpressionVisitor.class );
        AnnotationValueList  expr = new AnnotationValueList ( null );
        Object visitResult = new Object();
        when( visitor.visit( expr ) ).thenReturn( visitResult );
        assertSame( expr.accept( visitor ), visitResult );
    }

}
