package com.thoughtworks.qdox.builder.impl;

import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.expression.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EvaluatingVisitorTest
{
    private EvaluatingVisitor visitor = new EvaluatingVisitorStub();
    
    @Test
    public void testUnaryNumericResultTypeInteger()
    {
        Assertions.assertEquals(Integer.class, EvaluatingVisitor.unaryNumericResultType( 0 ));
        Assertions.assertEquals(Integer.class, EvaluatingVisitor.unaryNumericResultType( (byte) 0 ));
        Assertions.assertEquals(Integer.class, EvaluatingVisitor.unaryNumericResultType( (short) 0 ));
        Assertions.assertEquals(Integer.class, EvaluatingVisitor.unaryNumericResultType( (char) 0 ));
    }

    @Test
    public void testUnaryNumericResultTypeLong()
    {
        Assertions.assertEquals(Long.class, EvaluatingVisitor.unaryNumericResultType( 0L ));
    }

    @Test
    public void testUnaryNumericResultTypeVoid()
    {
        Assertions.assertEquals(void.class, EvaluatingVisitor.unaryNumericResultType( new Object() ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.unaryNumericResultType( (double) 0 ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.unaryNumericResultType( (float) 0 ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.unaryNumericResultType( null ));
    }

    @Test
    public void testUnaryResultTypeInteger()
    {
        Assertions.assertEquals(Integer.class, EvaluatingVisitor.unaryResultType( 0 ));
        Assertions.assertEquals(Integer.class, EvaluatingVisitor.unaryResultType( (byte) 0 ));
        Assertions.assertEquals(Integer.class, EvaluatingVisitor.unaryResultType( (short) 0 ));
        Assertions.assertEquals(Integer.class, EvaluatingVisitor.unaryResultType( (char) 0 ));
    }

    @Test
    public void testUnaryResultTypeLong()
    {
        Assertions.assertEquals(Long.class, EvaluatingVisitor.unaryResultType( 0L ));
    }

    @Test
    public void testUnaryResultTypeDouble()
    {
        Assertions.assertEquals(Double.class, EvaluatingVisitor.unaryResultType( (double) 0 ));
    }

    @Test
    public void testUnaryResultTypeFloat()
    {
        Assertions.assertEquals(Float.class, EvaluatingVisitor.unaryResultType( (float) 0 ));
    }

    @Test
    public void testUnaryResultTypeVoid()
    {
        Assertions.assertEquals(void.class, EvaluatingVisitor.unaryResultType( new Object() ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.unaryResultType( null ));
    }
    
    @Test
    public void testNumericResultTypeLong()
    {
        Assertions.assertEquals(Long.class, EvaluatingVisitor.numericResultType( (long) 0, (long) 0 ));
        Assertions.assertEquals(Long.class, EvaluatingVisitor.numericResultType( (int) 0, (long) 0 ));
        Assertions.assertEquals(Long.class, EvaluatingVisitor.numericResultType( (long) 0, (int) 0 ));
    }
    
    @Test
    public void testNumericResultTypeInteger()
    {
        Assertions.assertEquals(Integer.class, EvaluatingVisitor.numericResultType( (int) 0, (int) 0 ));
        Assertions.assertEquals(Integer.class, EvaluatingVisitor.numericResultType( (short) 0, (int) 0 ));
        Assertions.assertEquals(Integer.class, EvaluatingVisitor.numericResultType( (int) 0, (short) 0 ));
    }
    
    @Test
    public void testNumericResultTypeVoid()
    {
        Assertions.assertEquals(void.class, EvaluatingVisitor.numericResultType( (double) 0, (double) 0 ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.numericResultType( (float) 0, (double) 0 ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.numericResultType( (double) 0, (float) 0 ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.numericResultType( (float) 0, (float) 0 ));

        Assertions.assertEquals(void.class, EvaluatingVisitor.numericResultType( (double) 0, new Object() ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.numericResultType( (float) 0, new Object() ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.numericResultType( (long) 0, new Object() ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.numericResultType( (int) 0, new Object() ));

        Assertions.assertEquals(void.class, EvaluatingVisitor.numericResultType( new Object(), (double) 0 ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.numericResultType( new Object(), (float) 0 ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.numericResultType( new Object(), (long) 0 ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.numericResultType( new Object(), (int) 0 ));
        
        Assertions.assertEquals(void.class, EvaluatingVisitor.numericResultType( (double) 0, null ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.numericResultType( (float) 0, null ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.numericResultType( (long) 0, null ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.numericResultType( (int) 0, null ));

        Assertions.assertEquals(void.class, EvaluatingVisitor.numericResultType( null, (double) 0 ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.numericResultType( null, (float) 0 ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.numericResultType( null, (long) 0 ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.numericResultType( null, (int) 0 ));
    }

    @Test
    public void testResultTypeDouble()
    {
        // If either operand is of type double, the other is converted to double.
        Assertions.assertEquals(Double.class, EvaluatingVisitor.resultType( (double) 0, (double) 0 ));
        Assertions.assertEquals(Double.class, EvaluatingVisitor.resultType( (float) 0, (double) 0 ));
        Assertions.assertEquals(Double.class, EvaluatingVisitor.resultType( (int) 0, (double) 0 ));
        Assertions.assertEquals(Double.class, EvaluatingVisitor.resultType( (double) 0, (float) 0 ));
        Assertions.assertEquals(Double.class, EvaluatingVisitor.resultType( (double) 0, (int) 0 ));
    }

    @Test
    public void testResultTypeFloat()
    {
        // Otherwise, if either operand is of type float, the other is converted to float.
        Assertions.assertEquals(Float.class, EvaluatingVisitor.resultType( (float) 0, (float) 0 ));
        Assertions.assertEquals(Float.class, EvaluatingVisitor.resultType( (int) 0, (float) 0 ));
        Assertions.assertEquals(Float.class, EvaluatingVisitor.resultType( (float) 0, (int) 0 ));
    }

    @Test
    public void testResultTypeLong()
    {
        // Otherwise, if either operand is of type long, the other is converted to long.
        Assertions.assertEquals(Long.class, EvaluatingVisitor.resultType( (long) 0, (long) 0 ));
        Assertions.assertEquals(Long.class, EvaluatingVisitor.resultType( (int) 0, (long) 0 ));
        Assertions.assertEquals(Long.class, EvaluatingVisitor.resultType( (long) 0, (int) 0 ));
    }

    @Test
    public void testResultTypeInteger()
    {
        // Otherwise, if either operand is of type long, the other is converted to long.
        Assertions.assertEquals(Integer.class, EvaluatingVisitor.resultType( (int) 0, (int) 0 ));
        Assertions.assertEquals(Integer.class, EvaluatingVisitor.resultType( (short) 0, (int) 0 ));
        Assertions.assertEquals(Integer.class, EvaluatingVisitor.resultType( (int) 0, (short) 0 ));
    }
    
    @Test
    public void testResultTypeVoid()
    {
        // Otherwise, if either operand is of type long, the other is converted to long.
        Assertions.assertEquals(void.class, EvaluatingVisitor.resultType( (double) 0, new Object() ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.resultType( (float) 0, new Object() ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.resultType( (long) 0, new Object() ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.resultType( (int) 0, new Object() ));

        Assertions.assertEquals(void.class, EvaluatingVisitor.resultType( new Object(), (double) 0 ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.resultType( new Object(), (float) 0 ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.resultType( new Object(), (long) 0 ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.resultType( new Object(), (int) 0 ));
        
        Assertions.assertEquals(void.class, EvaluatingVisitor.resultType( (double) 0, null ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.resultType( (float) 0, null ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.resultType( (long) 0, null ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.resultType( (int) 0, null ));

        Assertions.assertEquals(void.class, EvaluatingVisitor.resultType( null, (double) 0 ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.resultType( null, (float) 0 ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.resultType( null, (long) 0 ));
        Assertions.assertEquals(void.class, EvaluatingVisitor.resultType( null, (int) 0 ));
    }
    
    @Test
    public void testVisitAdd()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Doubles
        when( lhs.accept( any( EvaluatingVisitor.class ) ) ).thenReturn( 7.0D );
        when( rhs.accept( any( EvaluatingVisitor.class ) ) ).thenReturn( 2.0D );
        Assertions.assertEquals(7.0D + 2.0D, visitor.visit(  new Add( lhs, rhs ) ));

        // Floats
        when( lhs.accept( any( EvaluatingVisitor.class ) ) ).thenReturn( 7.0F );
        when( rhs.accept( any( EvaluatingVisitor.class ) ) ).thenReturn( 2.0F );
        Assertions.assertEquals(7.0F + 2.0F, visitor.visit(  new Add( lhs, rhs ) ));

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        Assertions.assertEquals(7L + 2L, visitor.visit(  new Add( lhs, rhs ) ));

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        Assertions.assertEquals(7 + 2, visitor.visit(  new Add( lhs, rhs ) ));

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new Add( lhs, rhs ) );
            Assertions.fail("Additive operations (+  and -) can only be performed on numeric types");
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitAnd()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        Assertions.assertEquals(7L & 2L, visitor.visit(  new And( lhs, rhs ) ));

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        Assertions.assertEquals(7 & 2, visitor.visit(  new And( lhs, rhs ) ));

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new And( lhs, rhs ) );
            Assertions.fail("The and(&) operator can only be performed on integral types");
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }

    @Test
    public void testVisitAnnotation()
    {
        try{
            JavaAnnotation annotation = mock(JavaAnnotation.class);
            visitor.visit( annotation );
            Assertions.fail("Visiting an annotation is not supported and should throw an UnsupportedOperationException");
        }
        catch (UnsupportedOperationException e) {
        }
    }
    
    @Test
    public void testVisitAnnotationValueList() {
        {
            List<AnnotationValue> emptyList = Collections.emptyList();
            List<?> visitedResult = visitor.visit( new AnnotationValueList( emptyList ) );
            Assertions.assertEquals(0, visitedResult.size());
        }
        
        {
            AnnotationValue annoVal = mock( AnnotationValue.class );
            Object singleResult = new Object();
            when( annoVal.accept( visitor ) ).thenReturn( singleResult );
            List<?> visitedResult = visitor.visit( new AnnotationValueList( Collections.singletonList( annoVal ) ) );
            Assertions.assertEquals(1, visitedResult.size());
            Assertions.assertSame(singleResult, visitedResult.get( 0 ));
        }
    }

    @Test
    public void testVisitCast() throws Exception
    {
        AnnotationValue value = mock( AnnotationValue.class );
        when( value.accept( visitor ) ).thenReturn( 7 );

        JavaClass primitiveClass = mock( JavaClass.class );
        when( primitiveClass.isPrimitive() ).thenReturn( true );

        when( primitiveClass.getFullyQualifiedName() ).thenReturn( "byte" );
        Assertions.assertEquals((byte) 7, visitor.visit( new Cast( primitiveClass, value ) ));
        when( primitiveClass.getFullyQualifiedName() ).thenReturn( "char" );
        Assertions.assertEquals((char) 7, visitor.visit( new Cast( primitiveClass, value ) ));
        when( primitiveClass.getFullyQualifiedName() ).thenReturn( "short" );
        Assertions.assertEquals((short) 7, visitor.visit( new Cast( primitiveClass, value ) ));
        when( primitiveClass.getFullyQualifiedName() ).thenReturn( "int" );
        Assertions.assertEquals((int) 7, visitor.visit( new Cast( primitiveClass, value ) ));
        when( primitiveClass.getFullyQualifiedName() ).thenReturn( "long" );
        Assertions.assertEquals((long) 7, visitor.visit( new Cast( primitiveClass, value ) ));
        when( primitiveClass.getFullyQualifiedName() ).thenReturn( "float" );
        Assertions.assertEquals((float) 7, visitor.visit( new Cast( primitiveClass, value ) ));
        when( primitiveClass.getFullyQualifiedName() ).thenReturn( "double" );
        Assertions.assertEquals((double) 7, visitor.visit( new Cast( primitiveClass, value ) ));

        try
        {
            when( primitiveClass.getFullyQualifiedName() ).thenReturn( "void" );
            visitor.visit( new Cast( primitiveClass, value ) );

            Assertions.fail("Although 'void' is a primitive, you can't cast to it");
        }
        catch ( IllegalArgumentException iae )
        {
        }

        JavaClass stringClass = mock( JavaClass.class );
        when( stringClass.getFullyQualifiedName() ).thenReturn( "java.lang.String" );
        when( value.accept( visitor ) ).thenReturn( "hello world" );
        Assertions.assertEquals((String) "hello world", visitor.visit( new Cast( stringClass, value ) ));

        JavaClass listClass = mock( JavaClass.class );
        when( listClass.getFullyQualifiedName() ).thenReturn( "java.util.List" );
        Object list = Collections.EMPTY_LIST;
        when( value.accept( visitor ) ).thenReturn( list );
        Assertions.assertEquals((List<?>) list, visitor.visit( new Cast( listClass, value ) ));
    }
    
    @Test
    public void testVisitDivide()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Doubles
        when( lhs.accept( visitor ) ).thenReturn( 7.0D );
        when( rhs.accept( visitor ) ).thenReturn( 2.0D );
        Assertions.assertEquals(7.0D / 2.0D, visitor.visit(  new Divide( lhs, rhs ) ));

        // Floats
        when( lhs.accept( visitor ) ).thenReturn( 7.0F );
        when( rhs.accept( visitor ) ).thenReturn( 2.0F );
        Assertions.assertEquals(7.0F / 2.0F, visitor.visit(  new Divide( lhs, rhs ) ));

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        Assertions.assertEquals(7L / 2L, visitor.visit(  new Divide( lhs, rhs ) ));

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        Assertions.assertEquals(7 / 2, visitor.visit(  new Divide( lhs, rhs ) ));

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new Divide( lhs, rhs ) );
            Assertions.fail("The divide(/) operator can only be performed on numeric types");
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitEquals() 
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Doubles
        when( lhs.accept( visitor ) ).thenReturn( 7.0D );
        when( rhs.accept( visitor ) ).thenReturn( 2.0D );
        Assertions.assertEquals(7.0D == 2.0D, visitor.visit(  new Equals( lhs, rhs ) ));

        when( rhs.accept( visitor ) ).thenReturn( 7.0D );
        Assertions.assertEquals(7.0D == 7.0D, visitor.visit(  new Equals( lhs, rhs ) ));

        // Floats
        when( lhs.accept( visitor ) ).thenReturn( 7.0F );
        when( rhs.accept( visitor ) ).thenReturn( 2.0F );
        Assertions.assertEquals(7.0F == 2.0F, visitor.visit(  new Equals( lhs, rhs ) ));

        when( rhs.accept( visitor ) ).thenReturn( 7.0F );
        Assertions.assertEquals(7.0F == 7.0F, visitor.visit(  new Equals( lhs, rhs ) ));

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        Assertions.assertEquals(7L == 2L, visitor.visit(  new Equals( lhs, rhs ) ));

        when( rhs.accept( visitor ) ).thenReturn( 7L );
        Assertions.assertEquals(7L == 7L, visitor.visit(  new Equals( lhs, rhs ) ));

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        Assertions.assertEquals(7 == 2, visitor.visit(  new Equals( lhs, rhs ) ));

        when( rhs.accept( visitor ) ).thenReturn( 7 );
        Assertions.assertEquals(7 == 7, visitor.visit(  new Equals( lhs, rhs ) ));

        // Objects
        Object object1 = new Object();
        Object object2 = new Object();
        when( lhs.accept( visitor ) ).thenReturn( object1 );
        when( rhs.accept( visitor ) ).thenReturn( object2 );
        Assertions.assertEquals(object1 == object2, visitor.visit(  new Equals( lhs, rhs ) ));

        when( rhs.accept( visitor ) ).thenReturn( object1 );
        Assertions.assertEquals(object1 == object1, visitor.visit(  new Equals( lhs, rhs ) ));
    }

    @Test
    public void testVisitExlusiveOr() 
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        Assertions.assertEquals(7L ^ 2L, visitor.visit(  new ExclusiveOr( lhs, rhs ) ));

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        Assertions.assertEquals(7 ^ 2, visitor.visit(  new ExclusiveOr( lhs, rhs ) ));

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new ExclusiveOr( lhs, rhs ) );
            Assertions.fail("The exclusive-or(^) operator can only be performed on integral types");
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitFieldRef() 
    {
        FieldRef ref = new FieldRef( "fieldname" );
        try {
            visitor.visit( ref );
            Assertions.fail("fieldname should be a unresolvable field");
        }
        catch( IllegalArgumentException e )
        {
        }
        
        JavaField nonStaticNonFinalfield = mock( JavaField.class );
        JavaClass declaringClass = mock( JavaClass.class );
        when( declaringClass.getFieldByName( "fieldname" ) ).thenReturn( nonStaticNonFinalfield );
        when( nonStaticNonFinalfield.getDeclaringClass() ).thenReturn( declaringClass );

        ref.setDeclaringClass( declaringClass );
        try 
        {
            visitor.visit(  ref );
            Assertions.fail("fieldname should fail, because it's not-static and non-final");
        }
        catch( IllegalArgumentException e)
        {
        }
        

        JavaField staticFinalfield = mock( JavaField.class );
        when( staticFinalfield.isStatic() ).thenReturn( true );
        when( staticFinalfield.isFinal() ).thenReturn( true );
        when( declaringClass.getFieldByName( "fieldname" ) ).thenReturn( staticFinalfield );

        ref = new FieldRef( "fieldname" );
        ref.setDeclaringClass( declaringClass );
        Assertions.assertSame(EvaluatingVisitorStub.fieldReferenceValue, visitor.visit( ref ));

        ref = new FieldRef( "fieldname" );
        ref.setDeclaringClass( declaringClass );
        Assertions.assertSame(EvaluatingVisitorStub.fieldReferenceValue, visitor.visit( ref ));

        ref = new FieldRef( "a.B.fieldname" );
        ref.setDeclaringClass( null );
        JavaClass b = mock( JavaClass.class );
        when( b.getFieldByName( "fieldname" ) ).thenReturn( staticFinalfield );
        ClassLibrary classLibrary = mock( ClassLibrary.class );
        when( classLibrary.hasClassReference( "a.B" ) ).thenReturn( true );
        when( classLibrary.getJavaClass( "a.B" ) ).thenReturn( b );
        ref.setClassLibrary( classLibrary );
        Assertions.assertSame(EvaluatingVisitorStub.fieldReferenceValue, visitor.visit( ref ));
    }
    
    
    @Test
    public void testVisitGreaterEquals()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Doubles
        when( lhs.accept( visitor ) ).thenReturn( 7.0D );
        when( rhs.accept( visitor ) ).thenReturn( 2.0D );
        Assertions.assertEquals(7.0D >= 2.0D, visitor.visit(  new GreaterEquals( lhs, rhs ) ));

        when( rhs.accept( visitor ) ).thenReturn( 7.0D );
        Assertions.assertEquals(7.0D >= 7.0D, visitor.visit(  new GreaterEquals( lhs, rhs ) ));
        
        when( lhs.accept( visitor ) ).thenReturn( 2.0D );
        Assertions.assertEquals(2.0D >= 7.0D, visitor.visit(  new GreaterEquals( lhs, rhs ) ));

        // Floats
        when( lhs.accept( visitor ) ).thenReturn( 7.0F );
        when( rhs.accept( visitor ) ).thenReturn( 2.0F );
        Assertions.assertEquals(7.0F >= 2.0F, visitor.visit(  new GreaterEquals( lhs, rhs ) ));

        when( rhs.accept( visitor ) ).thenReturn( 7.0F );
        Assertions.assertEquals(7.0F >= 7.0F, visitor.visit(  new GreaterEquals( lhs, rhs ) ));

        when( lhs.accept( visitor ) ).thenReturn( 2.0F );
        Assertions.assertEquals(2.0F >= 7.0F, visitor.visit(  new GreaterEquals( lhs, rhs ) ));

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        Assertions.assertEquals(7L >= 2L, visitor.visit(  new GreaterEquals( lhs, rhs ) ));

        when( rhs.accept( visitor ) ).thenReturn( 7L );
        Assertions.assertEquals(7L >= 7L, visitor.visit(  new GreaterEquals( lhs, rhs ) ));

        when( lhs.accept( visitor ) ).thenReturn( 2L );
        Assertions.assertEquals(2L >= 7L, visitor.visit(  new GreaterEquals( lhs, rhs ) ));

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        Assertions.assertEquals(7 >= 2, visitor.visit(  new GreaterEquals( lhs, rhs ) ));

        when( rhs.accept( visitor ) ).thenReturn( 7 );
        Assertions.assertEquals(7 >=7, visitor.visit(  new GreaterEquals( lhs, rhs ) ));

        when( lhs.accept( visitor ) ).thenReturn( 2 );
        Assertions.assertEquals(2 >= 7, visitor.visit(  new GreaterEquals( lhs, rhs ) ));

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new GreaterEquals( lhs, rhs ) );
            Assertions.fail("The greater-equals(>=) operator can only be performed on numeric types");
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitGreaterThan()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Doubles
        when( lhs.accept( visitor ) ).thenReturn( 7.0D );
        when( rhs.accept( visitor ) ).thenReturn( 2.0D );
        Assertions.assertEquals(7.0D > 2.0D, visitor.visit(  new GreaterThan( lhs, rhs ) ));

        when( rhs.accept( visitor ) ).thenReturn( 7.0D );
        Assertions.assertEquals(7.0D > 7.0D, visitor.visit(  new GreaterThan( lhs, rhs ) ));
        
        when( lhs.accept( visitor ) ).thenReturn( 2.0D );
        Assertions.assertEquals(2.0D > 7.0D, visitor.visit(  new GreaterThan( lhs, rhs ) ));

        // Floats
        when( lhs.accept( visitor ) ).thenReturn( 7.0F );
        when( rhs.accept( visitor ) ).thenReturn( 2.0F );
        Assertions.assertEquals(7.0F > 2.0F, visitor.visit(  new GreaterThan( lhs, rhs ) ));

        when( rhs.accept( visitor ) ).thenReturn( 7.0F );
        Assertions.assertEquals(7.0F > 7.0F, visitor.visit(  new GreaterThan( lhs, rhs ) ));

        when( lhs.accept( visitor ) ).thenReturn( 2.0F );
        Assertions.assertEquals(2.0F > 7.0F, visitor.visit(  new GreaterThan( lhs, rhs ) ));

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        Assertions.assertEquals(7L > 2L, visitor.visit(  new GreaterThan( lhs, rhs ) ));

        when( rhs.accept( visitor ) ).thenReturn( 7L );
        Assertions.assertEquals(7L > 7L, visitor.visit(  new GreaterThan( lhs, rhs ) ));

        when( lhs.accept( visitor ) ).thenReturn( 2L );
        Assertions.assertEquals(2L > 7L, visitor.visit(  new GreaterThan( lhs, rhs ) ));

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        Assertions.assertEquals(7 > 2, visitor.visit(  new GreaterThan( lhs, rhs ) ));

        when( rhs.accept( visitor ) ).thenReturn( 7 );
        Assertions.assertEquals(7 > 7, visitor.visit(  new GreaterThan( lhs, rhs ) ));

        when( lhs.accept( visitor ) ).thenReturn( 2 );
        Assertions.assertEquals(2 > 7, visitor.visit(  new GreaterThan( lhs, rhs ) ));

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new GreaterThan( lhs, rhs ) );
            Assertions.fail("The greater-than(>) operator can only be performed on numeric types");
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitLessEquals()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Doubles
        when( lhs.accept( visitor ) ).thenReturn( 7.0D );
        when( rhs.accept( visitor ) ).thenReturn( 2.0D );
        Assertions.assertEquals(7.0D <= 2.0D, visitor.visit(  new LessEquals( lhs, rhs ) ));

        when( rhs.accept( visitor ) ).thenReturn( 7.0D );
        Assertions.assertEquals(7.0D <= 7.0D, visitor.visit(  new LessEquals( lhs, rhs ) ));
        
        when( lhs.accept( visitor ) ).thenReturn( 2.0D );
        Assertions.assertEquals(2.0D <= 7.0D, visitor.visit(  new LessEquals( lhs, rhs ) ));

        // Floats
        when( lhs.accept( visitor ) ).thenReturn( 7.0F );
        when( rhs.accept( visitor ) ).thenReturn( 2.0F );
        Assertions.assertEquals(7.0F <= 2.0F, visitor.visit(  new LessEquals( lhs, rhs ) ));

        when( rhs.accept( visitor ) ).thenReturn( 7.0F );
        Assertions.assertEquals(7.0F <= 7.0F, visitor.visit(  new LessEquals( lhs, rhs ) ));

        when( lhs.accept( visitor ) ).thenReturn( 2.0F );
        Assertions.assertEquals(2.0F <= 7.0F, visitor.visit(  new LessEquals( lhs, rhs ) ));

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        Assertions.assertEquals(7L <= 2L, visitor.visit(  new LessEquals( lhs, rhs ) ));

        when( rhs.accept( visitor ) ).thenReturn( 7L );
        Assertions.assertEquals(7L <= 7L, visitor.visit(  new LessEquals( lhs, rhs ) ));

        when( lhs.accept( visitor ) ).thenReturn( 2L );
        Assertions.assertEquals(2L <= 7L, visitor.visit(  new LessEquals( lhs, rhs ) ));

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        Assertions.assertEquals(7 <= 2, visitor.visit(  new LessEquals( lhs, rhs ) ));

        when( rhs.accept( visitor ) ).thenReturn( 7 );
        Assertions.assertEquals(7 <= 7, visitor.visit(  new LessEquals( lhs, rhs ) ));

        when( lhs.accept( visitor ) ).thenReturn( 2 );
        Assertions.assertEquals(2 <= 7, visitor.visit(  new LessEquals( lhs, rhs ) ));

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new LessEquals( lhs, rhs ) );
            Assertions.fail("The less-equals(<=) operator can only be performed on numeric types");
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitLessThan()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Doubles
        when( lhs.accept( visitor ) ).thenReturn( 7.0D );
        when( rhs.accept( visitor ) ).thenReturn( 2.0D );
        Assertions.assertEquals(7.0D < 2.0D, visitor.visit(  new LessThan( lhs, rhs ) ));

        when( rhs.accept( visitor ) ).thenReturn( 7.0D );
        Assertions.assertEquals(7.0D < 7.0D, visitor.visit(  new LessThan( lhs, rhs ) ));
        
        when( lhs.accept( visitor ) ).thenReturn( 2.0D );
        Assertions.assertEquals(2.0D < 7.0D, visitor.visit(  new LessThan( lhs, rhs ) ));

        // Floats
        when( lhs.accept( visitor ) ).thenReturn( 7.0F );
        when( rhs.accept( visitor ) ).thenReturn( 2.0F );
        Assertions.assertEquals(7.0F < 2.0F, visitor.visit(  new LessThan( lhs, rhs ) ));

        when( rhs.accept( visitor ) ).thenReturn( 7.0F );
        Assertions.assertEquals(7.0F < 7.0F, visitor.visit(  new LessThan( lhs, rhs ) ));

        when( lhs.accept( visitor ) ).thenReturn( 2.0F );
        Assertions.assertEquals(2.0F < 7.0F, visitor.visit(  new LessThan( lhs, rhs ) ));

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        Assertions.assertEquals(7L < 2L, visitor.visit(  new LessThan( lhs, rhs ) ));

        when( rhs.accept( visitor ) ).thenReturn( 7L );
        Assertions.assertEquals(7L < 7L, visitor.visit(  new LessThan( lhs, rhs ) ));

        when( lhs.accept( visitor ) ).thenReturn( 2L );
        Assertions.assertEquals(2L < 7L, visitor.visit(  new LessThan( lhs, rhs ) ));

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        Assertions.assertEquals(7 < 2, visitor.visit(  new LessThan( lhs, rhs ) ));

        when( rhs.accept( visitor ) ).thenReturn( 7 );
        Assertions.assertEquals(7 < 7, visitor.visit(  new LessThan( lhs, rhs ) ));

        when( lhs.accept( visitor ) ).thenReturn( 2 );
        Assertions.assertEquals(2 < 7, visitor.visit(  new LessThan( lhs, rhs ) ));

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new LessThan( lhs, rhs ) );
            Assertions.fail("The less-than(<) operator can only be performed on numeric types");
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }

    @Test
    public void visitLogicalAnd()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        when( lhs.accept( visitor ) ).thenReturn( true );
        when( rhs.accept( visitor ) ).thenReturn( true );
        Assertions.assertEquals(true && true, visitor.visit( new LogicalAnd( lhs, rhs ) ));


        when( lhs.accept( visitor ) ).thenReturn( false );
        when( rhs.accept( visitor ) ).thenReturn( false );
        Assertions.assertEquals(false && false, visitor.visit( new LogicalAnd( lhs, rhs ) ));

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new LogicalAnd( lhs, rhs ) );
            Assertions.fail("The logical and(&&) operator can only be performed on booleans");
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void visitLogicalNot()
    {
        AnnotationValue value = mock( AnnotationValue.class );

        when( value.accept( visitor ) ).thenReturn( true );
        Assertions.assertEquals(!true, visitor.visit( new LogicalNot( value ) ));

        when( value.accept( visitor ) ).thenReturn( false );
        Assertions.assertEquals(!false, visitor.visit( new LogicalNot( value ) ));

        // Objects
        when( value.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit( new LogicalNot( value ) );
            Assertions.fail("The logical not(!) operator can only be performed on booleans");
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }

    @Test
    public void visitLogicalOr()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        when( lhs.accept( visitor ) ).thenReturn( true );
        when( rhs.accept( visitor ) ).thenReturn( true );
        Assertions.assertEquals(true || true, visitor.visit( new LogicalOr( lhs, rhs ) ));


        when( lhs.accept( visitor ) ).thenReturn( false );
        when( rhs.accept( visitor ) ).thenReturn( false );
        Assertions.assertEquals(false || false, visitor.visit( new LogicalOr( lhs, rhs ) ));

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new LogicalOr( lhs, rhs ) );
            Assertions.fail("The logical or(||) operator can only be performed on booleans");
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitMinusSign()
    {
        AnnotationValue value = mock( AnnotationValue.class );
        
        // Double
        when( value.accept( visitor ) ).thenReturn( 7.0D );
        Assertions.assertEquals(-7.0D, visitor.visit( new MinusSign( value ) ));
        
        // Float
        when( value.accept( visitor ) ).thenReturn( 7.0F );
        Assertions.assertEquals(-7.0F, visitor.visit( new MinusSign( value ) ));
        
        // Long
        when( value.accept( visitor ) ).thenReturn( 7L );
        Assertions.assertEquals(-7L, visitor.visit( new MinusSign( value ) ));

        // Integer
        when( value.accept( visitor ) ).thenReturn( 7 );
        Assertions.assertEquals(-7, visitor.visit( new MinusSign( value ) ));
        
        when( value.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit( new MinusSign( value ) );
            Assertions.fail("The minus(-) sign operator can only be performed on numeric types");
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitMultiply()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Doubles
        when( lhs.accept( visitor ) ).thenReturn( 7.0D );
        when( rhs.accept( visitor ) ).thenReturn( 2.0D );
        Assertions.assertEquals(7.0D * 2.0D, visitor.visit(  new Multiply( lhs, rhs ) ));

        // Floats
        when( lhs.accept( visitor ) ).thenReturn( 7.0F );
        when( rhs.accept( visitor ) ).thenReturn( 2.0F );
        Assertions.assertEquals(7.0F * 2.0F, visitor.visit(  new Multiply( lhs, rhs ) ));

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        Assertions.assertEquals(7L * 2L, visitor.visit(  new Multiply( lhs, rhs ) ));

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        Assertions.assertEquals(7 * 2, visitor.visit(  new Multiply( lhs, rhs ) ));

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new Multiply( lhs, rhs ) );
            Assertions.fail("The multiply(*) operator can only be performed on numeric types");
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitNot() 
    {
        AnnotationValue value = mock( AnnotationValue.class );
        
        // Longs
        when( value.accept( visitor ) ).thenReturn( 7L );
        Assertions.assertEquals(~7L, visitor.visit(  new Not( value ) ));

        // Integers
        when( value.accept( visitor ) ).thenReturn( 7 );
        Assertions.assertEquals(~7, visitor.visit(  new Not( value) ));

        // Objects
        when( value.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new Not( value ) );
            Assertions.fail("The not(~) operator can only be performed on integral types");
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitNotEquals()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Doubles
        when( lhs.accept( visitor ) ).thenReturn( 7.0D );
        when( rhs.accept( visitor ) ).thenReturn( 2.0D );
        Assertions.assertEquals(7.0D != 2.0D, visitor.visit(  new NotEquals( lhs, rhs ) ));

        when( rhs.accept( visitor ) ).thenReturn( 7.0D );
        Assertions.assertEquals(7.0D != 7.0D, visitor.visit(  new NotEquals( lhs, rhs ) ));

        // Floats
        when( lhs.accept( visitor ) ).thenReturn( 7.0F );
        when( rhs.accept( visitor ) ).thenReturn( 2.0F );
        Assertions.assertEquals(7.0F != 2.0F, visitor.visit(  new NotEquals( lhs, rhs ) ));

        when( rhs.accept( visitor ) ).thenReturn( 7.0F );
        Assertions.assertEquals(7.0F != 7.0F, visitor.visit(  new NotEquals( lhs, rhs ) ));

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        Assertions.assertEquals(7L != 2L, visitor.visit(  new NotEquals( lhs, rhs ) ));

        when( rhs.accept( visitor ) ).thenReturn( 7L );
        Assertions.assertEquals(7L != 7L, visitor.visit(  new NotEquals( lhs, rhs ) ));

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        Assertions.assertEquals(7 != 2, visitor.visit(  new NotEquals( lhs, rhs ) ));

        when( rhs.accept( visitor ) ).thenReturn( 7 );
        Assertions.assertEquals(7 != 7, visitor.visit(  new NotEquals( lhs, rhs ) ));

        // Objects
        Object object1 = new Object();
        Object object2 = new Object();
        when( lhs.accept( visitor ) ).thenReturn( object1 );
        when( rhs.accept( visitor ) ).thenReturn( object2 );
        Assertions.assertEquals(object1 != object2, visitor.visit(  new NotEquals( lhs, rhs ) ));

        when( rhs.accept( visitor ) ).thenReturn( object1 );
        Assertions.assertEquals(object1 != object1, visitor.visit(  new NotEquals( lhs, rhs ) ));
    }
    
    @Test
    public void testVisitOr()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        Assertions.assertEquals(7L | 2L, visitor.visit(  new Or( lhs, rhs ) ));

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        Assertions.assertEquals(7 | 2, visitor.visit(  new Or( lhs, rhs ) ));

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new Or( lhs, rhs ) );
            Assertions.fail("The or(|) operator can only be performed on integral types");
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitParenExpression()
    {
        AnnotationValue value = mock( AnnotationValue.class );
        
        Object acceptResult = new Object();
        when( value.accept( visitor ) ).thenReturn( acceptResult );
        Assertions.assertSame(acceptResult, visitor.visit( new ParenExpression( value ) ));
    }
    
    @Test
    public void testVisitPlusSign() 
    {
        AnnotationValue value = mock( AnnotationValue.class );
        
        // Double
        when( value.accept( visitor ) ).thenReturn( 7.0D );
        Assertions.assertEquals(7.0D, visitor.visit( new PlusSign( value ) ));
        
        // Float
        when( value.accept( visitor ) ).thenReturn( 7.0F );
        Assertions.assertEquals(7.0F, visitor.visit( new PlusSign( value ) ));
        
        // Long
        when( value.accept( visitor ) ).thenReturn( 7L );
        Assertions.assertEquals(7L, visitor.visit( new PlusSign( value ) ));

        // Integer
        when( value.accept( visitor ) ).thenReturn( 7 );
        Assertions.assertEquals(7, visitor.visit( new PlusSign( value ) ));
        
        when( value.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit( new PlusSign( value ) );
            Assertions.fail("The plus sign operator can only be performed on numeric types");
        }
        catch ( IllegalArgumentException iae )
        {

        }
    }
    
    @Test
    public void testVisitQuery()
    {
        AnnotationValue condition = mock( AnnotationValue.class );
        AnnotationValue trueExpr = mock( AnnotationValue.class );
        AnnotationValue falseExpr = mock( AnnotationValue.class );

        when( trueExpr.accept( visitor ) ).thenReturn( "consequent" );
        when( falseExpr.accept( visitor ) ).thenReturn( "alternative" );

        // true condition
        when( condition.accept( visitor ) ).thenReturn( Boolean.TRUE );
        Assertions.assertEquals("consequent", visitor.visit( new Query( condition, trueExpr, falseExpr ) ));

        when( condition.accept( visitor ) ).thenReturn( true );
        Assertions.assertEquals("consequent", visitor.visit( new Query( condition, trueExpr, falseExpr ) ));

        // false condition
        when( condition.accept( visitor ) ).thenReturn( Boolean.FALSE );
        Assertions.assertEquals("alternative", visitor.visit( new Query( condition, trueExpr, falseExpr ) ));

        when( condition.accept( visitor ) ).thenReturn( false );
        Assertions.assertEquals("alternative", visitor.visit( new Query( condition, trueExpr, falseExpr ) ));

        when( condition.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit( new Query( condition, trueExpr, falseExpr ) );
            Assertions.fail("The condition of the query( ? : ) must be a boolean");
        }
        catch ( IllegalArgumentException iae )
        {

        }
    }
    
    @Test
    public void testVisitRemainder()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Doubles
        when( lhs.accept( visitor ) ).thenReturn( 7.0D );
        when( rhs.accept( visitor ) ).thenReturn( 2.0D );
        Assertions.assertEquals(7.0D % 2.0D, visitor.visit(  new Remainder( lhs, rhs ) ));

        // Floats
        when( lhs.accept( visitor ) ).thenReturn( 7.0F );
        when( rhs.accept( visitor ) ).thenReturn( 2.0F );
        Assertions.assertEquals(7.0F % 2.0F, visitor.visit(  new Remainder( lhs, rhs ) ));

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        Assertions.assertEquals(7L % 2L, visitor.visit(  new Remainder( lhs, rhs ) ));

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        Assertions.assertEquals(7 % 2, visitor.visit(  new Remainder( lhs, rhs ) ));

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new Remainder( lhs, rhs ) );
            Assertions.fail("The remainder(%) operator can only be performed on numeric types");
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitShiftLeft()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        Assertions.assertEquals(7L << 2L, visitor.visit( new ShiftLeft( lhs, rhs ) ));

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        Assertions.assertEquals(7 << 2, visitor.visit( new ShiftLeft( lhs, rhs ) ));

        // Objects
        when( lhs.accept( any( EvaluatingVisitor.class ) ) ).thenReturn( new Object() );
        when( rhs.accept( any( EvaluatingVisitor.class ) ) ).thenReturn( new Object() );
        try
        {
            visitor.visit( new ShiftLeft( lhs, rhs ) );
            Assertions.fail("Bitwise and bit shift operations can only be performed on integral types");
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitShiftRight()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );
        
        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        Assertions.assertEquals(7L >> 2L, visitor.visit( new ShiftRight( lhs, rhs ) ));

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        Assertions.assertEquals(7 >> 2, visitor.visit( new ShiftRight( lhs, rhs ) ));

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit( new ShiftRight( lhs, rhs ) );
            Assertions.fail("Bitwise and bit shift operations can only be performed on integral types");
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitSubtract()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Doubles
        when( lhs.accept( any( EvaluatingVisitor.class ) ) ).thenReturn( 7.0D );
        when( rhs.accept( any( EvaluatingVisitor.class ) ) ).thenReturn( 2.0D );
        Assertions.assertEquals(7.0D - 2.0D, visitor.visit(  new Subtract( lhs, rhs ) ));

        // Floats
        when( lhs.accept( any( EvaluatingVisitor.class ) ) ).thenReturn( 7.0F );
        when( rhs.accept( any( EvaluatingVisitor.class ) ) ).thenReturn( 2.0F );
        Assertions.assertEquals(7.0F - 2.0F, visitor.visit(  new Subtract( lhs, rhs ) ));

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        Assertions.assertEquals(7L - 2L, visitor.visit(  new Subtract( lhs, rhs ) ));

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        Assertions.assertEquals(7 - 2, visitor.visit(  new Subtract( lhs, rhs ) ));

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new Subtract( lhs, rhs ) );
            Assertions.fail("Additive operations (+  and -) can only be performed on numeric types");
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    @Test
    public void testVisitTypeRef()
    {
        JavaType type = mock( JavaType.class );
        Assertions.assertSame(type, visitor.visit( new TypeRef( type ) ));
    }

    @Test
    public void testVisitUnsignedShiftRight()
    {
        AnnotationValue lhs = mock( AnnotationValue.class );
        AnnotationValue rhs = mock( AnnotationValue.class );

        // Longs
        when( lhs.accept( visitor ) ).thenReturn( 7L );
        when( rhs.accept( visitor ) ).thenReturn( 2L );
        Assertions.assertEquals(7L >>> 2L, visitor.visit(  new UnsignedShiftRight( lhs, rhs ) ));

        // Integers
        when( lhs.accept( visitor ) ).thenReturn( 7 );
        when( rhs.accept( visitor ) ).thenReturn( 2 );
        Assertions.assertEquals(7 >>> 2, visitor.visit(  new UnsignedShiftRight( lhs, rhs ) ));

        // Objects
        when( lhs.accept( visitor ) ).thenReturn( new Object() );
        when( rhs.accept( visitor ) ).thenReturn( new Object() );
        try
        {
            visitor.visit(  new UnsignedShiftRight( lhs, rhs ) );
            Assertions.fail("Bitwise and bit shift operations can only be performed on integral types");
        }
        catch ( IllegalArgumentException iae )
        {
        }
    }
    
    private static class EvaluatingVisitorStub extends EvaluatingVisitor {
        
        static final Object fieldReferenceValue = new Object();
        
        @Override
        protected Object getFieldReferenceValue( JavaField javaField )
        {
            return fieldReferenceValue;
        }
    }
}
