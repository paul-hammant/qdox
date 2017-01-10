package com.thoughtworks.qdox.builder.impl;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.expression.Add;
import com.thoughtworks.qdox.model.expression.And;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import com.thoughtworks.qdox.model.expression.AnnotationValueList;
import com.thoughtworks.qdox.model.expression.Assignment;
import com.thoughtworks.qdox.model.expression.Cast;
import com.thoughtworks.qdox.model.expression.Constant;
import com.thoughtworks.qdox.model.expression.Divide;
import com.thoughtworks.qdox.model.expression.Equals;
import com.thoughtworks.qdox.model.expression.ExclusiveOr;
import com.thoughtworks.qdox.model.expression.ExpressionVisitor;
import com.thoughtworks.qdox.model.expression.FieldRef;
import com.thoughtworks.qdox.model.expression.GreaterEquals;
import com.thoughtworks.qdox.model.expression.GreaterThan;
import com.thoughtworks.qdox.model.expression.LessEquals;
import com.thoughtworks.qdox.model.expression.LessThan;
import com.thoughtworks.qdox.model.expression.LogicalAnd;
import com.thoughtworks.qdox.model.expression.LogicalNot;
import com.thoughtworks.qdox.model.expression.LogicalOr;
import com.thoughtworks.qdox.model.expression.MethodInvocation;
import com.thoughtworks.qdox.model.expression.MinusSign;
import com.thoughtworks.qdox.model.expression.Multiply;
import com.thoughtworks.qdox.model.expression.Not;
import com.thoughtworks.qdox.model.expression.NotEquals;
import com.thoughtworks.qdox.model.expression.Or;
import com.thoughtworks.qdox.model.expression.ParenExpression;
import com.thoughtworks.qdox.model.expression.PlusSign;
import com.thoughtworks.qdox.model.expression.PostDecrement;
import com.thoughtworks.qdox.model.expression.PostIncrement;
import com.thoughtworks.qdox.model.expression.PreDecrement;
import com.thoughtworks.qdox.model.expression.PreIncrement;
import com.thoughtworks.qdox.model.expression.Query;
import com.thoughtworks.qdox.model.expression.Remainder;
import com.thoughtworks.qdox.model.expression.ShiftLeft;
import com.thoughtworks.qdox.model.expression.ShiftRight;
import com.thoughtworks.qdox.model.expression.Subtract;
import com.thoughtworks.qdox.model.expression.TypeRef;
import com.thoughtworks.qdox.model.expression.UnsignedShiftRight;

/**
 * Visitor that evaluates annotation expressions.
 * <p>
 * Users of this class must override {@link EvaluatingVisitor#getFieldReferenceValue(JavaField)} to return values for
 * referenced fields.
 * 
 * @author Jochen Kuhnle
 */
public class EvaluatingVisitor
    implements ExpressionVisitor
{

    public Object getValue( JavaAnnotation annotation, String property )
    {
        Object result = null;
        AnnotationValue value = annotation.getProperty( property );

        if ( value != null )
        {
            result = value.accept( this );
        }

        return result;
    }

    public List<?> getListValue( JavaAnnotation annotation, String property )
    {
        Object value = getValue( annotation, property );
        List<?> list = null;

        if ( value != null )
        {
            if ( value instanceof List )
            {
                list = (List<?>) value;
            }
            else
            {
                list = Collections.singletonList( value );
            }
        }
        return list;
    }

    /**
     * Return the result type of a binary operator
     * <p>
     * Performs binary numeric promotion as specified in the Java Language Specification,
     * 
     * @param left the left hand side instance
     * @param right the right hand side instance
     * @return the expected result Class 
     * @see <a href="http://java.sun.com/docs/books/jls/second_edition/html/conversions.doc.html#170983">section 5.6.1</a>
     */
    protected static Class<?> resultType( final Object left, final Object right )
    {
        Class<?> type = void.class;

        if ( left instanceof String || right instanceof String )
        {
            type = String.class;
        }
        else if ( left instanceof Number && right instanceof Number )
        {
            if ( left instanceof Double || right instanceof Double )
            {
                type = Double.class;
            }
            else if ( left instanceof Float || right instanceof Float )
            {
                type = Float.class;
            }
            else if ( left instanceof Long || right instanceof Long )
            {
                type = Long.class;
            }
            else
            {
                type = Integer.class;
            }
        }

        return type;
    }

    /**
     * Return the numeric result type of a binary operator
     * <p>
     * Performs binary numeric promotion as specified in the Java Language Specification,
     * 
     * @param left the left hand side instance
     * @param right the right hand side instance
     * @return the expected result Class 
     * @see <a href="http://java.sun.com/docs/books/jls/second_edition/html/conversions.doc.html#170983">section 5.6.1</a>
     */
    protected static Class<?> numericResultType( final Object left, final Object right )
    {
        Class<?> type = void.class;

        if ( left instanceof Number && right instanceof Number )
        {
            if ( left instanceof Long || right instanceof Long )
            {
                type = Long.class;
            }
            else if ( left instanceof Integer || right instanceof Integer )
            {
                type = Integer.class;
            }
        }

        return type;
    }

    /**
     * Return the result type of an unary operator
     * <p>
     * Performs unary numeric promotion as specified in the Java Language Specification,
     * 
     * @param value the instance
     * @return the expected result Class 
     * @see <a href="http://java.sun.com/docs/books/jls/second_edition/html/conversions.doc.html#170952">section 5.6.2</a>
     */
    protected static Class<?> unaryNumericResultType( final Object value )
    {
        Class<?> type = void.class;

        if ( value instanceof Byte || value instanceof Short || value instanceof Character || value instanceof Integer )
        {
            type = Integer.class;
        }
        else if ( value instanceof Long )
        {
            type = Long.class;
        }

        return type;
    }

    protected static Class<?> unaryResultType( final Object value )
    {
        Class<?> type = unaryNumericResultType( value );

        if ( type == void.class )
        {
            if ( value instanceof Float )
            {
                type = Float.class;
            }
            else if ( value instanceof Double )
            {
                type = Double.class;
            }
        }

        return type;
    }

    /** {@inheritDoc} */
    public Object visit( JavaAnnotation annotation ) throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException( "Illegal annotation value '" + annotation + "'." );
    }

    /** {@inheritDoc} */
    public Object visit( Add op )
    {
        Object left = op.getLeft().accept( this );
        Object right = op.getRight().accept( this );
        Class<?> type = resultType( left, right );
        Object result;

        if ( type == String.class )
        {
            result = left.toString() + right.toString();
        }
        else if ( type == Double.class )
        {
            result = Double.valueOf( ( (Number) left ).doubleValue() + ( (Number) right ).doubleValue() );
        }
        else if ( type == Float.class )
        {
            result = Float.valueOf( ( (Number) left ).floatValue() + ( (Number) right ).floatValue() );
        }
        else if ( type == Long.class )
        {
            result = Long.valueOf( ( (Number) left ).longValue() + ( (Number) right ).longValue() );
        }
        else if ( type == Integer.class )
        {

            result = Integer.valueOf( ( (Number) left ).intValue() + ( (Number) right ).intValue() );
        }
        else
        {
            throw new IllegalArgumentException( "Cannot evaluate '" + op + "'." );
        }

        return result;
    }

    /** {@inheritDoc} */
    public Object visit( Constant constant )
    {
        return constant.getValue();
    }

    /** {@inheritDoc} */
    public Object visit( Divide op )
    {
        Object left = op.getLeft().accept( this );
        Object right = op.getRight().accept( this );
        Class<?> type = resultType( left, right );
        Object result;

        if ( type == Double.class )
        {
            result = Double.valueOf( ( (Number) left ).doubleValue() / ( (Number) right ).doubleValue() );
        }
        else if ( type == Float.class )
        {
            result = Float.valueOf( ( (Number) left ).floatValue() / ( (Number) right ).floatValue() );
        }
        else if ( type == Long.class )
        {
            result = Long.valueOf( ( (Number) left ).longValue() / ( (Number) right ).longValue() );
        }
        else if ( type == Integer.class )
        {
            result = Integer.valueOf( ( (Number) left ).intValue() / ( (Number) right ).intValue() );
        }
        else
        {
            throw new IllegalArgumentException( "Cannot evaluate '" + op + "'." );
        }

        return result;
    }

    /** {@inheritDoc} */
    public Object visit( FieldRef fieldRef )
    {
        JavaField javaField = fieldRef.getField();

        if ( javaField == null )
        {
            throw new IllegalArgumentException( "Cannot resolve field reference '" + fieldRef + "'." );
        }

        if ( !(javaField.isFinal() && javaField.isStatic() ) )
        {
            throw new IllegalArgumentException( "Field reference '" + fieldRef + "' must be static and final." );
        }

        return getFieldReferenceValue( javaField );
    }

    protected Object getFieldReferenceValue( JavaField javaField ) {
        throw new UnsupportedOperationException("getFieldReferenceValue(JavaField) has not been implemented.");
    }

    /** {@inheritDoc} */
    public Object visit( GreaterThan op )
    {
        Object left = op.getLeft().accept( this );
        Object right = op.getRight().accept( this );
        Class<?> type = resultType( left, right );
        boolean result;

        if ( type == Double.class )
        {
            result = ( (Number) left ).doubleValue() > ( (Number) right ).doubleValue();
        }
        else if ( type == Float.class )
        {
            result = ( (Number) left ).floatValue() > ( (Number) right ).floatValue();
        }
        else if ( type == Long.class )
        {
            result = ( (Number) left ).longValue() > ( (Number) right ).longValue();
        }
        else if ( type == Integer.class )
        {
            result = ( (Number) left ).intValue() > ( (Number) right ).intValue();
        }
        else
        {
            throw new IllegalArgumentException( "Cannot evaluate '" + op + "'." );
        }

        return result ? Boolean.TRUE : Boolean.FALSE;
    }

    /** {@inheritDoc} */
    public Object visit( LessThan op )
    {
        Object left = op.getLeft().accept( this );
        Object right = op.getRight().accept( this );
        Class<?> type = resultType( left, right );
        boolean result;

        if ( type == Double.class )
        {
            result = ( (Number) left ).doubleValue() < ( (Number) right ).doubleValue();
        }
        else if ( type == Float.class )
        {
            result = ( (Number) left ).floatValue() < ( (Number) right ).floatValue();
        }
        else if ( type == Long.class )
        {
            result = ( (Number) left ).longValue() < ( (Number) right ).longValue();
        }
        else if ( type == Integer.class )
        {
            result = ( (Number) left ).intValue() < ( (Number) right ).intValue();
        }
        else
        {
            throw new IllegalArgumentException( "Cannot evaluate '" + op + "'." );
        }

        return result ? Boolean.TRUE : Boolean.FALSE;
    }

    /** {@inheritDoc} */
    public Object visit( Multiply op )
    {
        Object left = op.getLeft().accept( this );
        Object right = op.getRight().accept( this );
        Class<?> type = resultType( left, right );
        Object result;

        if ( type == Double.class )
        {
            result = Double.valueOf( ( (Number) left ).doubleValue() * ( (Number) right ).doubleValue() );
        }
        else if ( type == Float.class )
        {
            result = Float.valueOf( ( (Number) left ).floatValue() * ( (Number) right ).floatValue() );
        }
        else if ( type == Long.class )
        {
            result = Long.valueOf( ( (Number) left ).longValue() * ( (Number) right ).longValue() );
        }
        else if ( type == Integer.class )
        {
            result = Integer.valueOf( ( (Number) left ).intValue() * ( (Number) right ).intValue() );
        }
        else
        {
            throw new IllegalArgumentException( "Cannot evaluate '" + op + "'." );
        }

        return result;
    }

    /** {@inheritDoc} */
    public Object visit( ParenExpression parenExpression )
    {
        return parenExpression.getValue().accept( this );
    }

    /** {@inheritDoc} */
    public Object visit( Subtract op )
    {
        Object left = op.getLeft().accept( this );
        Object right = op.getRight().accept( this );
        Class<?> type = resultType( left, right );
        Object result;

        if ( type == Double.class )
        {
            result = Double.valueOf( ( (Number) left ).doubleValue() - ( (Number) right ).doubleValue() );
        }
        else if ( type == Float.class )
        {
            result = Float.valueOf( ( (Number) left ).floatValue() - ( (Number) right ).floatValue() );
        }
        else if ( type == Long.class )
        {
            result = Long.valueOf( ( (Number) left ).longValue() - ( (Number) right ).longValue() );
        }
        else if ( type == Integer.class )
        {
            result = Integer.valueOf( ( (Number) left ).intValue() - ( (Number) right ).intValue() );
        }
        else
        {
            throw new IllegalArgumentException( "Cannot evaluate '" + op + "'." );
        }

        return result;
    }

    /** {@inheritDoc} */
    public JavaType visit( TypeRef typeRef )
    {
        return typeRef.getType();
    }

    /** {@inheritDoc} */
    public List<?> visit( AnnotationValueList valueList )
    {
        List<Object> list = new LinkedList<Object>();

        for ( AnnotationValue value : valueList.getValueList() )
        {
            Object v = value.accept( this );
            list.add( v );
        }

        return list;
    }

    /** {@inheritDoc} */
    public Object visit( And and )
    {
        Object left = and.getLeft().accept( this );
        Object right = and.getRight().accept( this );
        Class<?> type = numericResultType( left, right );
        Object result;

        if ( type == Long.class )
        {
            result = Long.valueOf( ( (Number) left ).longValue() & ( (Number) right ).longValue() );
        }
        else if ( type == Integer.class )
        {
            result = Integer.valueOf( ( (Number) left ).intValue() & ( (Number) right ).intValue() );
        }
        else
        {
            throw new IllegalArgumentException( "Cannot evaluate '" + and + "'." );
        }

        return result;
    }

    /** {@inheritDoc} */
    public Object visit( GreaterEquals greaterEquals )
    {
        Object left = greaterEquals.getLeft().accept( this );
        Object right = greaterEquals.getRight().accept( this );
        Class<?> type = resultType( left, right );
        boolean result;

        if ( type == Double.class )
        {
            result = ( (Number) left ).doubleValue() >= ( (Number) right ).doubleValue();
        }
        else if ( type == Float.class )
        {
            result = ( (Number) left ).floatValue() >= ( (Number) right ).floatValue();
        }
        else if ( type == Long.class )
        {
            result = ( (Number) left ).longValue() >= ( (Number) right ).longValue();
        }
        else if ( type == Integer.class )
        {
            result = ( (Number) left ).intValue() >= ( (Number) right ).intValue();
        }
        else
        {
            throw new IllegalArgumentException( "Cannot evaluate '" + greaterEquals + "'." );
        }

        return result ? Boolean.TRUE : Boolean.FALSE;
    }

    /** {@inheritDoc} */
    public Object visit( LessEquals lessEquals )
    {
        Object left = lessEquals.getLeft().accept( this );
        Object right = lessEquals.getRight().accept( this );
        Class<?> type = resultType( left, right );
        boolean result;

        if ( type == Double.class )
        {
            result = ( (Number) left ).doubleValue() <= ( (Number) right ).doubleValue();
        }
        else if ( type == Float.class )
        {
            result = ( (Number) left ).floatValue() <= ( (Number) right ).floatValue();
        }
        else if ( type == Long.class )
        {
            result = ( (Number) left ).longValue() <= ( (Number) right ).longValue();
        }
        else if ( type == Integer.class )
        {
            result = ( (Number) left ).intValue() <= ( (Number) right ).intValue();
        }
        else
        {
            throw new IllegalArgumentException( "Cannot evaluate '" + lessEquals + "'." );
        }

        return result ? Boolean.TRUE : Boolean.FALSE;
    }

    /** {@inheritDoc} */
    public Object visit( LogicalAnd and )
    {
        Object left = and.getLeft().accept( this );
        Object right = and.getRight().accept( this );
        boolean result;

        if ( left instanceof Boolean && right instanceof Boolean )
        {
            result = ( (Boolean) left ).booleanValue() && ( (Boolean) right ).booleanValue();
        }
        else
        {
            throw new IllegalArgumentException( "Cannot evaluate '" + and + "'." );
        }

        return result ? Boolean.TRUE : Boolean.FALSE;
    }

    /** {@inheritDoc} */
    public Object visit( LogicalNot not )
    {
        Object value = not.getValue().accept( this );
        boolean result;

        if ( value instanceof Boolean )
        {
            result = !( (Boolean) value ).booleanValue();
        }
        else
        {
            throw new IllegalArgumentException( "Cannot evaluate '" + not + "'." );
        }

        return result ? Boolean.TRUE : Boolean.FALSE;
    }

    /** {@inheritDoc} */
    public Object visit( LogicalOr or )
    {
        Object left = or.getLeft().accept( this );
        Object right = or.getRight().accept( this );
        boolean result;

        if ( left instanceof Boolean && right instanceof Boolean )
        {
            result = ( (Boolean) left ).booleanValue() || ( (Boolean) right ).booleanValue();
        }
        else
        {
            throw new IllegalArgumentException( "Cannot evaluate '" + or + "'." );
        }

        return result ? Boolean.TRUE : Boolean.FALSE;
    }

    /** {@inheritDoc} */
    public Object visit( MinusSign sign )
    {
        Object value = sign.getValue().accept( this );
        Class<?> type = unaryResultType( value );
        Object result;

        if ( type == Integer.class )
        {
            result = Integer.valueOf( -( (Integer) value ).intValue() );
        }
        else if ( type == Long.class )
        {
            result = Long.valueOf( -( (Long) value ).longValue() );
        }
        else if ( type == Float.class )
        {
            result = Float.valueOf( -( (Float) value ).floatValue() );
        }
        else if ( type == Double.class )
        {
            result = Double.valueOf( -( (Double) value ).intValue() );
        }
        else
        {
            throw new IllegalArgumentException( "Cannot evaluate '" + sign + "'." );
        }

        return result;
    }

    /** {@inheritDoc} */
    public Object visit( Not not )
    {
        Object value = not.getValue().accept( this );
        Object type = unaryNumericResultType( value );
        Object result;

        if ( type == Long.class )
        {
            result = Long.valueOf( ~( (Long) value ).longValue() );
        }
        else if ( type == Integer.class )
        {
            result = Integer.valueOf( ~( (Integer) value ).intValue() );
        }
        else
        {
            throw new IllegalArgumentException( "Cannot evaluate '" + not + "'." );
        }

        return result;
    }

    /** {@inheritDoc} */
    public Object visit( Or or )
    {
        Object left = or.getLeft().accept( this );
        Object right = or.getRight().accept( this );
        Class<?> type = numericResultType( left, right );
        Object result;

        if ( type == Long.class )
        {
            result = Long.valueOf( ( (Number) left ).longValue() | ( (Number) right ).longValue() );
        }
        else if ( type == Integer.class )
        {
            result = Integer.valueOf( ( (Number) left ).intValue() | ( (Number) right ).intValue() );
        }
        else
        {
            throw new IllegalArgumentException( "Cannot evaluate '" + or + "'." );
        }

        return result;
    }

    /** {@inheritDoc} */
    public Object visit( PlusSign sign )
    {
        Object value = sign.getValue().accept( this );
        Object result;

        if ( value instanceof Number )
        {
            result = value;
        }
        else
        {
            throw new IllegalArgumentException( "Cannot evaluate '" + sign + "'." );
        }

        return result;
    }

    /** {@inheritDoc} */
    public Object visit( Remainder remainder )
    {
        Object left = remainder.getLeft().accept( this );
        Object right = remainder.getRight().accept( this );
        Class<?> type = resultType( left, right );
        Object result;

        if ( type == Double.class )
        {
            result = Double.valueOf( ( (Number) left ).doubleValue() % ( (Number) right ).doubleValue() );
        }
        else if ( type == Float.class )
        {
            result = Float.valueOf( ( (Number) left ).floatValue() % ( (Number) right ).floatValue() );
        }
        else if ( type == Long.class )
        {
            result = Long.valueOf( ( (Number) left ).longValue() % ( (Number) right ).longValue() );
        }
        else if ( type == Integer.class )
        {
            result = Integer.valueOf( ( (Number) left ).intValue() % ( (Number) right ).intValue() );
        }
        else
        {
            throw new IllegalArgumentException( "Cannot evaluate '" + remainder + "'." );
        }

        return result;
    }

    /** {@inheritDoc} */
    public Object visit( ShiftLeft shiftLeft )
    {
        Object left = shiftLeft.getLeft().accept( this );
        Object right = shiftLeft.getRight().accept( this );
        Class<?> type = numericResultType( left, right );
        Object result;

        if ( type == Long.class )
        {
            result = Long.valueOf( ( (Number) left ).longValue() << ( (Number) right ).longValue() );
        }
        else if ( type == Integer.class )
        {
            result = Integer.valueOf( ( (Number) left ).intValue() << ( (Number) right ).intValue() );
        }
        else
        {
            throw new IllegalArgumentException( "Cannot evaluate '" + shiftLeft + "'." );
        }

        return result;
    }

    /** {@inheritDoc} */
    public Object visit( ShiftRight shiftRight )
    {
        Object left = shiftRight.getLeft().accept( this );
        Object right = shiftRight.getRight().accept( this );
        Class<?> type = numericResultType( left, right );
        Object result;

        if ( type == Long.class )
        {
            result = Long.valueOf( ( (Number) left ).longValue() >> ( (Number) right ).longValue() );
        }
        else if ( type == Integer.class )
        {
            result = Integer.valueOf( ( (Number) left ).intValue() >> ( (Number) right ).intValue() );
        }
        else
        {
            throw new IllegalArgumentException( "Cannot evaluate '" + shiftRight + "'." );
        }

        return result;
    }

    /** {@inheritDoc} */
    public Object visit( UnsignedShiftRight shiftRight )
    {
        Object left = shiftRight.getLeft().accept( this );
        Object right = shiftRight.getRight().accept( this );
        Class<?> type = numericResultType( left, right );
        Object result;

        if ( type == Long.class )
        {
            result = Long.valueOf( ( (Number) left ).longValue() >>> ( (Number) right ).longValue() );
        }
        else if ( type == Integer.class )
        {
            result = Integer.valueOf( ( (Number) left ).intValue() >>> ( (Number) right ).intValue() );
        }
        else
        {
            throw new IllegalArgumentException( "Cannot evaluate '" + shiftRight + "'." );
        }

        return result;
    }

    /** {@inheritDoc} */
    public Object visit( Equals annotationEquals )
    {
        Object left = annotationEquals.getLeft().accept( this );
        Object right = annotationEquals.getRight().accept( this );
        Class<?> type = resultType( left, right );
        boolean result;

        if ( type == Double.class )
        {
            result = ( (Number) left ).doubleValue() == ( (Number) right ).doubleValue();
        }
        else if ( type == Float.class )
        {
            result = ( (Number) left ).floatValue() == ( (Number) right ).floatValue();
        }
        else if ( type == Long.class )
        {
            result = ( (Number) left ).longValue() == ( (Number) right ).longValue();
        }
        else if ( type == Integer.class )
        {
            result = ( (Number) left ).intValue() == ( (Number) right ).intValue();
        }
        else
        {
            result = ( left == right );
        }

        return result ? Boolean.TRUE : Boolean.FALSE;
    }

    /** {@inheritDoc} */
    public Object visit( ExclusiveOr annotationExclusiveOr )
    {
        Object left = annotationExclusiveOr.getLeft().accept( this );
        Object right = annotationExclusiveOr.getRight().accept( this );
        Class<?> type = numericResultType( left, right );
        Object result;

        if ( type == Long.class )
        {
            result = Long.valueOf( ( (Number) left ).longValue() ^ ( (Number) right ).longValue() );
        }
        else if ( type == Integer.class )
        {
            result = Integer.valueOf( ( (Number) left ).intValue() ^ ( (Number) right ).intValue() );
        }
        else
        {
            throw new IllegalArgumentException( "Cannot evaluate '" + annotationExclusiveOr + "'." );
        }

        return result;
    }

    /** {@inheritDoc} */
    public Object visit( NotEquals annotationNotEquals )
    {
        Object left = annotationNotEquals.getLeft().accept( this );
        Object right = annotationNotEquals.getRight().accept( this );
        Class<?> type = resultType( left, right );
        boolean result;

        if ( type == Double.class )
        {
            result = ( (Number) left ).doubleValue() != ( (Number) right ).doubleValue();
        }
        else if ( type == Float.class )
        {
            result = ( (Number) left ).floatValue() != ( (Number) right ).floatValue();
        }
        else if ( type == Long.class )
        {
            result = ( (Number) left ).longValue() != ( (Number) right ).longValue();
        }
        else if ( type == Integer.class )
        {
            result = ( (Number) left ).intValue() != ( (Number) right ).intValue();
        }
        else
        {
            result = ( left != right );
        }

        return result ? Boolean.TRUE : Boolean.FALSE;
    }

    /** {@inheritDoc} */
    public Object visit( Query annotationQuery )
    {
        Object value = annotationQuery.getCondition().accept( this );

        if ( !( value instanceof Boolean ) )
        {
            throw new IllegalArgumentException( "Cannot evaluate '" + annotationQuery + "'." );
        }

        AnnotationValue expression =
            ( (Boolean) value ).booleanValue() ? annotationQuery.getTrueExpression()
                            : annotationQuery.getFalseExpression();

        return expression.accept( this );
    }

    /** {@inheritDoc} */
    public Object visit( Cast annotationCast )
    {
        Object value = annotationCast.getValue().accept( this );
        JavaType type = annotationCast.getType();
        Object result;

        if ( type instanceof JavaClass && ( (JavaClass) type ).isPrimitive() && value instanceof Number )
        {
            Number n = (Number) value;
            String typeName = type.getFullyQualifiedName();

            if ( typeName.equals( "byte" ) )
            {
                result = Byte.valueOf( n.byteValue() );
            }
            else if ( typeName.equals( "char" ) )
            {
                result = Character.valueOf( (char) n.intValue() );
            }
            else if ( typeName.equals( "short" ) )
            {
                result = Short.valueOf( n.shortValue() );
            }
            else if ( typeName.equals( "int" ) )
            {
                result = Integer.valueOf( n.intValue() );
            }
            else if ( typeName.equals( "long" ) )
            {
                result = Long.valueOf( n.longValue() );
            }
            else if ( typeName.equals( "float" ) )
            {
                result = Float.valueOf( n.floatValue() );
            }
            else if ( typeName.equals( "double" ) )
            {
                result = Double.valueOf( n.doubleValue() );
            }
            else
            {
                throw new IllegalArgumentException( "Cannot evaluate '" + annotationCast + "'." );
            }
        }
        else 
        {
            try
            {
                result = Class.forName( type.getFullyQualifiedName() ).cast( value );
            }
            catch ( ClassNotFoundException e )
            {
                throw new IllegalArgumentException( "Cannot evaluate '" + annotationCast + "'." );
            }
        }

        return result;
    }

    /** {@inheritDoc} */
    public Object visit( PreDecrement preDecrement )
    {
        throw new IllegalArgumentException( "Cannot evaluate '" + preDecrement + "'." );
    }

    /** {@inheritDoc} */
    public Object visit( PreIncrement preIncrement )
    {
        throw new IllegalArgumentException( "Cannot evaluate '" + preIncrement + "'." );
    }

    /** {@inheritDoc} */
    public Object visit( PostDecrement postDecrement )
    {
        throw new IllegalArgumentException( "Cannot evaluate '" + postDecrement + "'." );
    }

    /** {@inheritDoc} */
    public Object visit( PostIncrement postIncrement )
    {
        throw new IllegalArgumentException( "Cannot evaluate '" + postIncrement + "'." );
    }

    /** {@inheritDoc} */
    public Object visit( Assignment assignment )
    {
        throw new IllegalArgumentException( "Cannot evaluate '" + assignment + "'." );
    }

    /** {@inheritDoc} */
    public Object visit( MethodInvocation methodInvocation )
    {
        throw new IllegalArgumentException( "Cannot evaluate '" + methodInvocation + "'." );
    }
}