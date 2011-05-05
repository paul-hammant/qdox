package com.thoughtworks.qdox.parser.expression;

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

import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.expression.Add;
import com.thoughtworks.qdox.model.expression.And;
import com.thoughtworks.qdox.model.expression.Cast;
import com.thoughtworks.qdox.model.expression.Constant;
import com.thoughtworks.qdox.model.expression.Divide;
import com.thoughtworks.qdox.model.expression.Equals;
import com.thoughtworks.qdox.model.expression.ExclusiveOr;
import com.thoughtworks.qdox.model.expression.FieldRef;
import com.thoughtworks.qdox.model.expression.GreaterEquals;
import com.thoughtworks.qdox.model.expression.GreaterThan;
import com.thoughtworks.qdox.model.expression.LessEquals;
import com.thoughtworks.qdox.model.expression.LessThan;
import com.thoughtworks.qdox.model.expression.LogicalAnd;
import com.thoughtworks.qdox.model.expression.LogicalOr;
import com.thoughtworks.qdox.model.expression.Multiply;
import com.thoughtworks.qdox.model.expression.NotEquals;
import com.thoughtworks.qdox.model.expression.Or;
import com.thoughtworks.qdox.model.expression.ParenExpression;
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
 * Users of this class must override
 * {@link EvaluatingVisitor#getFieldReferenceValue(JavaField)} to return values
 * for referenced fields.
 * 
 * @author Jochen Kuhnle
 */
public abstract class EvaluatingVisitor implements AnnotationVisitor {

    public Object getValue( JavaAnnotation annotation, String property ) {
        Object result = null;
        AnnotationValue value = annotation.getProperty( property );

        if( value != null ) {
            result = value.accept( this );
        }

        return result;
    }

    public List<?> getListValue( JavaAnnotation annotation, String property ) {
        Object value = getValue( annotation, property );
        List<?> list = null;

        if( value != null ) {
            if( value instanceof List ) {
                list = (List<?>) value;
            }
            else {
                list = Collections.singletonList( value );
            }
        }
        return list;
    }

    /**
     * Return the result type of a binary operator
     * <p>
     * Performs binary numeric promotion as specified in the Java Language
     * Specification, 
     * @see <a href="http://java.sun.com/docs/books/jls/second_edition/html/conversions.doc.html#170983">section 5.6.1<a>
     */
    protected static Class<?> resultType( Object left, Object right ) {
        Class<?> type = void.class;

        if( left instanceof String || right instanceof String ) {
            type = String.class;
        }
        else if( left instanceof Number && right instanceof Number ) {
            if( left instanceof Double || right instanceof Double ) {
                type = Double.class;
            }
            else if( left instanceof Float || right instanceof Float ) {
                type = Float.class;
            }
            else if( left instanceof Long || right instanceof Long ) {
                type = Long.class;
            }
            else {
                type = Integer.class;
            }
        }

        return type;
    }

    /**
     * Return the numeric result type of a binary operator
     * <p>
     * Performs binary numeric promotion as specified in the Java Language
     * Specification, 
     * @see <a href="http://java.sun.com/docs/books/jls/second_edition/html/conversions.doc.html#170983">section 5.6.1<a>
     */
    protected static Class<?> numericResultType( Object left, Object right ) {
        Class<?> type = void.class;

        if( left instanceof Number && right instanceof Number ) {
            if( left instanceof Long || right instanceof Long ) {
                type = Long.class;
            }
            else if( left instanceof Integer || right instanceof Integer ) {
                type = Integer.class;
            }
        }

        return type;
    }

    /**
     * Return the result type of an unary operator
     * <p>
     * Performs unary numeric promotion as specified in the Java Language
     * Specification, 
     * @see <a href="http://java.sun.com/docs/books/jls/second_edition/html/conversions.doc.html#170952">section 5.6.2<a>
     */
    protected static Class<?> unaryNumericResultType( Object value ) {
        Class<?> type = void.class;

        if( value instanceof Byte || value instanceof Short || value instanceof Character || value instanceof Integer ) {
            type = Integer.class;
        }
        else if( value instanceof Long ) {
            value = Long.class;
        }

        return type;
    }

    protected static Class<?> unaryResultType( Object value ) {
        Class<?> type = unaryNumericResultType( value );

        if( type == void.class ) {
            if( value instanceof Float ) {
                value = Float.class;
            }
            else if( value instanceof Double ) {
                value = Double.class;
            }
        }

        return type;
    }

    public Object visitAnnotation( Annotation annotation ) {
        throw new UnsupportedOperationException( "Illegal annotation value '" + annotation + "'." );
    }

    public Object visitAnnotationAdd( Add op ) {
        Object left = op.getLeft().accept( this );
        Object right = op.getRight().accept( this );
        Class<?> type = resultType( left, right );
        Object result;

        if( type == String.class ) {
            result = left.toString() + right.toString();
        }
        else if( type == Double.class ) {
            result = new Double( ((Number) left).doubleValue() + ((Number) right).doubleValue() );
        }
        else if( type == Float.class ) {
            result = new Float( ((Number) left).floatValue() + ((Number) right).floatValue() );
        }
        else if( type == Long.class ) {
            result = new Long( ((Number) left).longValue() + ((Number) right).longValue() );
        }
        else if( type == Integer.class ) {
            result = new Integer( ((Number) left).intValue() + ((Number) right).intValue() );
        }
        else {
            throw new IllegalArgumentException( "Cannot evaluate '" + op + "'." );
        }

        return result;
    }

    public Object visitAnnotationConstant( Constant constant ) {
        return constant.getValue();
    }

    public Object visitAnnotationDivide( Divide op ) {
        Object left = op.getLeft().accept( this );
        Object right = op.getRight().accept( this );
        Class<?> type = resultType( left, right );
        Object result;

        if( type == Double.class ) {
            result = new Double( ((Number) left).doubleValue() / ((Number) right).doubleValue() );
        }
        else if( type == Float.class ) {
            result = new Float( ((Number) left).floatValue() / ((Number) right).floatValue() );
        }
        else if( type == Long.class ) {
            result = new Long( ((Number) left).longValue() / ((Number) right).longValue() );
        }
        else if( type == Integer.class ) {
            result = new Integer( ((Number) left).intValue() / ((Number) right).intValue() );
        }
        else {
            throw new IllegalArgumentException( "Cannot evaluate '" + op + "'." );
        }

        return result;
    }

    public Object visitAnnotationFieldRef( FieldRef fieldRef ) {
        JavaField javaField = fieldRef.getField();

        if( javaField == null ) {
            throw new IllegalArgumentException( "Cannot resolve field reference '" + fieldRef + "'." );
        }

        if( !javaField.isFinal() || !javaField.isStatic() ) {
            throw new IllegalArgumentException( "Field reference '" + fieldRef + "' must be static and final." );
        }

        Object result = getFieldReferenceValue( javaField );
        return result;
    }

    protected abstract Object getFieldReferenceValue( JavaField javaField );

    public Object visitAnnotationGreaterThan( GreaterThan op ) {
        Object left = op.getLeft().accept( this );
        Object right = op.getRight().accept( this );
        Class<?> type = resultType( left, right );
        boolean result;

        if( type == Double.class ) {
            result = ((Number) left).doubleValue() > ((Number) right).doubleValue();
        }
        else if( type == Float.class ) {
            result = ((Number) left).floatValue() > ((Number) right).floatValue();
        }
        else if( type == Long.class ) {
            result = ((Number) left).longValue() > ((Number) right).longValue();
        }
        else if( type == Integer.class ) {
            result = ((Number) left).intValue() > ((Number) right).intValue();
        }
        else {
            throw new IllegalArgumentException( "Cannot evaluate '" + op + "'." );
        }

        return result ? Boolean.TRUE : Boolean.FALSE;
    }

    public Object visitAnnotationLessThan( LessThan op ) {
        Object left = op.getLeft().accept( this );
        Object right = op.getRight().accept( this );
        Class<?> type = resultType( left, right );
        boolean result;

        if( type == Double.class ) {
            result = ((Number) left).doubleValue() < ((Number) right).doubleValue();
        }
        else if( type == Float.class ) {
            result = ((Number) left).floatValue() < ((Number) right).floatValue();
        }
        else if( type == Long.class ) {
            result = ((Number) left).longValue() < ((Number) right).longValue();
        }
        else if( type == Integer.class ) {
            result = ((Number) left).intValue() < ((Number) right).intValue();
        }
        else {
            throw new IllegalArgumentException( "Cannot evaluate '" + op + "'." );
        }

        return result ? Boolean.TRUE : Boolean.FALSE;
    }

    public Object visitAnnotationMultiply( Multiply op ) {
        Object left = op.getLeft().accept( this );
        Object right = op.getRight().accept( this );
        Class<?> type = resultType( left, right );
        Object result;

        if( type == Double.class ) {
            result = new Double( ((Number) left).doubleValue() * ((Number) right).doubleValue() );
        }
        else if( type == Float.class ) {
            result = new Float( ((Number) left).floatValue() * ((Number) right).floatValue() );
        }
        else if( type == Long.class ) {
            result = new Long( ((Number) left).longValue() * ((Number) right).longValue() );
        }
        else if( type == Integer.class ) {
            result = new Integer( ((Number) left).intValue() * ((Number) right).intValue() );
        }
        else {
            throw new IllegalArgumentException( "Cannot evaluate '" + op + "'." );
        }

        return result;
    }

    public Object visitAnnotationParenExpression( ParenExpression parenExpression ) {
        return parenExpression.getValue().accept( this );
    }

    public Object visitAnnotationSubtract( Subtract op ) {
        Object left = op.getLeft().accept( this );
        Object right = op.getRight().accept( this );
        Class<?> type = resultType( left, right );
        Object result;

        if( type == Double.class ) {
            result = new Double( ((Number) left).doubleValue() - ((Number) right).doubleValue() );
        }
        else if( type == Float.class ) {
            result = new Float( ((Number) left).floatValue() - ((Number) right).floatValue() );
        }
        else if( type == Long.class ) {
            result = new Long( ((Number) left).longValue() - ((Number) right).longValue() );
        }
        else if( type == Integer.class ) {
            result = new Integer( ((Number) left).intValue() - ((Number) right).intValue() );
        }
        else {
            throw new IllegalArgumentException( "Cannot evaluate '" + op + "'." );
        }

        return result;
    }

    public Object visitAnnotationTypeRef( TypeRef typeRef ) {
        JavaClass javaClass = typeRef.getType().getJavaClass();
        return javaClass;
    }

    public Object visitAnnotationValueList( AnnotationValueList valueList ) {
        List<Object> list = new LinkedList<Object>();

        for( AnnotationValue value : valueList.getValueList()) {
            Object v = value.accept( this );
            list.add( v );
        }

        return list;
    }

    public Object visitAnnotationAnd( And and ) {
        Object left = and.getLeft().accept( this );
        Object right = and.getRight().accept( this );
        Class<?> type = numericResultType( left, right );
        Object result;

        if( type == Long.class ) {
            result = new Long( ((Number) left).longValue() & ((Number) right).longValue() );
        }
        else if( type == Integer.class ) {
            result = new Integer( ((Number) left).intValue() & ((Number) right).intValue() );
        }
        else {
            throw new IllegalArgumentException( "Cannot evaluate '" + and + "'." );
        }

        return result;
    }

    public Object visitAnnotationGreaterEquals( GreaterEquals greaterEquals ) {
        Object left = greaterEquals.getLeft().accept( this );
        Object right = greaterEquals.getRight().accept( this );
        Class<?> type = resultType( left, right );
        boolean result;

        if( type == Double.class ) {
            result = ((Number) left).doubleValue() >= ((Number) right).doubleValue();
        }
        else if( type == Float.class ) {
            result = ((Number) left).floatValue() >= ((Number) right).floatValue();
        }
        else if( type == Long.class ) {
            result = ((Number) left).longValue() >= ((Number) right).longValue();
        }
        else if( type == Integer.class ) {
            result = ((Number) left).intValue() >= ((Number) right).intValue();
        }
        else {
            throw new IllegalArgumentException( "Cannot evaluate '" + greaterEquals + "'." );
        }

        return result ? Boolean.TRUE : Boolean.FALSE;
    }

    public Object visitAnnotationLessEquals( LessEquals lessEquals ) {
        Object left = lessEquals.getLeft().accept( this );
        Object right = lessEquals.getRight().accept( this );
        Class<?> type = resultType( left, right );
        boolean result;

        if( type == Double.class ) {
            result = ((Number) left).doubleValue() <= ((Number) right).doubleValue();
        }
        else if( type == Float.class ) {
            result = ((Number) left).floatValue() <= ((Number) right).floatValue();
        }
        else if( type == Long.class ) {
            result = ((Number) left).longValue() <= ((Number) right).longValue();
        }
        else if( type == Integer.class ) {
            result = ((Number) left).intValue() <= ((Number) right).intValue();
        }
        else {
            throw new IllegalArgumentException( "Cannot evaluate '" + lessEquals + "'." );
        }

        return result ? Boolean.TRUE : Boolean.FALSE;
    }

    public Object visitAnnotationLogicalAnd( LogicalAnd and ) {
        Object left = and.getLeft().accept( this );
        Object right = and.getRight().accept( this );
        boolean result;

        if( left instanceof Boolean && right instanceof Boolean ) {
            result = ((Boolean) left).booleanValue() && ((Boolean) right).booleanValue();
        }
        else {
            throw new IllegalArgumentException( "Cannot evaluate '" + and + "'." );
        }

        return result ? Boolean.TRUE : Boolean.FALSE;
    }

    public Object visitAnnotationLogicalNot( AnnotationLogicalNot not ) {
        Object value = not.getValue().accept( this );
        boolean result;

        if( value instanceof Boolean ) {
            result = !((Boolean) value).booleanValue();
        }
        else {
            throw new IllegalArgumentException( "Cannot evaluate '" + not + "'." );
        }

        return result ? Boolean.TRUE : Boolean.FALSE;
    }

    public Object visitAnnotationLogicalOr( LogicalOr or ) {
        Object left = or.getLeft().accept( this );
        Object right = or.getRight().accept( this );
        boolean result;

        if( left instanceof Boolean && right instanceof Boolean ) {
            result = ((Boolean) left).booleanValue() || ((Boolean) right).booleanValue();
        }
        else {
            throw new IllegalArgumentException( "Cannot evaluate '" + or + "'." );
        }

        return result ? Boolean.TRUE : Boolean.FALSE;
    }

    public Object visitAnnotationMinusSign( AnnotationMinusSign sign ) {
        Object value = sign.getValue().accept( this );
        Class<?> type = unaryResultType( value );
        Object result;

        if( type == Integer.class ) {
            result = new Integer( -((Integer) value).intValue() );
        }
        else if( type == Long.class ) {
            result = new Long( -((Long) value).longValue() );
        }
        else if( type == Float.class ) {
            result = new Float( -((Float) value).floatValue() );
        }
        else if( type == Double.class ) {
            result = new Double( -((Double) value).intValue() );
        }
        else {
            throw new IllegalArgumentException( "Cannot evaluate '" + sign + "'." );
        }

        return result;
    }

    public Object visitAnnotationNot( AnnotationNot not ) {
        Object value = not.getValue().accept( this );
        Object type = unaryNumericResultType( value );
        Object result;

        if( type == Long.class ) {
            result = new Long( ~((Long) value).longValue() );
        }
        else if( type == Integer.class ) {
            result = new Integer( ~((Integer) value).intValue() );
        }
        else {
            throw new IllegalArgumentException( "Cannot evaluate '" + not + "'." );
        }

        return result;
    }

    public Object visitAnnotationOr( Or or ) {
        Object left = or.getLeft().accept( this );
        Object right = or.getRight().accept( this );
        Class<?> type = numericResultType( left, right );
        Object result;

        if( type == Long.class ) {
            result = new Long( ((Number) left).longValue() | ((Number) right).longValue() );
        }
        else if( type == Integer.class ) {
            result = new Integer( ((Number) left).intValue() | ((Number) right).intValue() );
        }
        else {
            throw new IllegalArgumentException( "Cannot evaluate '" + or + "'." );
        }

        return result;
    }

    public Object visitAnnotationPlusSign( AnnotationPlusSign sign ) {
        Object value = sign.getValue().accept( this );
        Object result;

        if( value instanceof Number ) {
            result = value;
        }
        else {
            throw new IllegalArgumentException( "Cannot evaluate '" + sign + "'." );
        }

        return result;
    }

    public Object visitAnnotationRemainder( Remainder remainder ) {
        Object left = remainder.getLeft().accept( this );
        Object right = remainder.getRight().accept( this );
        Class<?> type = resultType( left, right );
        Object result;

        if( type == Double.class ) {
            result = new Double( ((Number) left).doubleValue() % ((Number) right).doubleValue() );
        }
        else if( type == Float.class ) {
            result = new Float( ((Number) left).floatValue() % ((Number) right).floatValue() );
        }
        else if( type == Long.class ) {
            result = new Long( ((Number) left).longValue() % ((Number) right).longValue() );
        }
        else if( type == Integer.class ) {
            result = new Integer( ((Number) left).intValue() % ((Number) right).intValue() );
        }
        else {
            throw new IllegalArgumentException( "Cannot evaluate '" + remainder + "'." );
        }

        return result;
    }

    public Object visitAnnotationShiftLeft( ShiftLeft shiftLeft ) {
        Object left = shiftLeft.getLeft().accept( this );
        Object right = shiftLeft.getRight().accept( this );
        Class<?> type = numericResultType( left, right );
        Object result;

        if( type == Long.class ) {
            result = new Long( ((Number) left).longValue() << ((Number) right).longValue() );
        }
        else if( type == Integer.class ) {
            result = new Integer( ((Number) left).intValue() << ((Number) right).intValue() );
        }
        else {
            throw new IllegalArgumentException( "Cannot evaluate '" + shiftLeft + "'." );
        }

        return result;
    }

    public Object visitAnnotationShiftRight( ShiftRight shiftRight ) {
        Object left = shiftRight.getLeft().accept( this );
        Object right = shiftRight.getRight().accept( this );
        Class<?> type = numericResultType( left, right );
        Object result;

        if( type == Long.class ) {
            result = new Long( ((Number) left).longValue() >> ((Number) right).longValue() );
        }
        else if( type == Integer.class ) {
            result = new Integer( ((Number) left).intValue() >> ((Number) right).intValue() );
        }
        else {
            throw new IllegalArgumentException( "Cannot evaluate '" + shiftRight + "'." );
        }

        return result;
    }

    public Object visitAnnotationUnsignedShiftRight( UnsignedShiftRight shiftRight ) {
        Object left = shiftRight.getLeft().accept( this );
        Object right = shiftRight.getRight().accept( this );
        Class<?> type = numericResultType( left, right );
        Object result;

        if( type == Long.class ) {
            result = new Long( ((Number) left).longValue() >>> ((Number) right).longValue() );
        }
        else if( type == Integer.class ) {
            result = new Integer( ((Number) left).intValue() >>> ((Number) right).intValue() );
        }
        else {
            throw new IllegalArgumentException( "Cannot evaluate '" + shiftRight + "'." );
        }

        return result;
    }

    public Object visitAnnotationEquals( Equals annotationEquals ) {
        Object left = annotationEquals.getLeft().accept( this );
        Object right = annotationEquals.getRight().accept( this );
        Class<?> type = resultType( left, right );
        boolean result;

        if( type == Double.class ) {
            result = ((Number) left).doubleValue() == ((Number) right).doubleValue();
        }
        else if( type == Float.class ) {
            result = ((Number) left).floatValue() == ((Number) right).floatValue();
        }
        else if( type == Long.class ) {
            result = ((Number) left).longValue() == ((Number) right).longValue();
        }
        else if( type == Integer.class ) {
            result = ((Number) left).intValue() == ((Number) right).intValue();
        }
        else {
            result = (left == right);
        }

        return result ? Boolean.TRUE : Boolean.FALSE;
    }

    public Object visitAnnotationExclusiveOr( ExclusiveOr annotationExclusiveOr ) {
        Object left = annotationExclusiveOr.getLeft().accept( this );
        Object right = annotationExclusiveOr.getRight().accept( this );
        Class<?> type = numericResultType( left, right );
        Object result;

        if( type == Long.class ) {
            result = new Long( ((Number) left).longValue() ^ ((Number) right).longValue() );
        }
        else if( type == Integer.class ) {
            result = new Integer( ((Number) left).intValue() ^ ((Number) right).intValue() );
        }
        else {
            throw new IllegalArgumentException( "Cannot evaluate '" + annotationExclusiveOr + "'." );
        }

        return result;
    }

    public Object visitAnnotationNotEquals( NotEquals annotationNotEquals ) {
        Object left = annotationNotEquals.getLeft().accept( this );
        Object right = annotationNotEquals.getRight().accept( this );
        Class<?> type = resultType( left, right );
        boolean result;

        if( type == Double.class ) {
            result = ((Number) left).doubleValue() != ((Number) right).doubleValue();
        }
        else if( type == Float.class ) {
            result = ((Number) left).floatValue() != ((Number) right).floatValue();
        }
        else if( type == Long.class ) {
            result = ((Number) left).longValue() != ((Number) right).longValue();
        }
        else if( type == Integer.class ) {
            result = ((Number) left).intValue() != ((Number) right).intValue();
        }
        else {
            result = (left == right);
        }

        return result ? Boolean.TRUE : Boolean.FALSE;
    }

    public Object visitAnnotationQuery( Query annotationQuery ) {
        Object value = annotationQuery.getCondition().accept( this );

        if( value == null || !(value instanceof Boolean) ) {
            throw new IllegalArgumentException( "Cannot evaluate '" + annotationQuery + "'." );
        }

        AnnotationValue expression = ((Boolean) value).booleanValue() ? annotationQuery.getTrueExpression()
            : annotationQuery.getFalseExpression();

        return expression.accept( this );
    }

    public Object visitAnnotationCast( Cast annotationCast ) {
        Object value = annotationCast.getValue().accept( this );
        String type = annotationCast.getType().getJavaClass().getFullyQualifiedName();
        Object result;

        if( value instanceof Number ) {
            Number n = (Number) value;

            if( type.equals( "byte" ) ) {
                result = new Byte( n.byteValue() );
            }
            else if( type.equals( "char" ) ) {
                result = new Character( (char) n.intValue() );
            }
            else if( type.equals( "short" ) ) {
                result = new Short( n.shortValue() );
            }
            else if( type.equals( "int" ) ) {
                result = new Integer( n.intValue() );
            }
            else if( type.equals( "long" ) ) {
                result = new Long( n.longValue() );
            }
            else if( type.equals( "float" ) ) {
                result = new Float( n.floatValue() );
            }
            else if( type.equals( "double" ) ) {
                result = new Double( n.doubleValue() );
            }
            else {
                throw new IllegalArgumentException( "Cannot evaluate '" + annotationCast + "'." );
            }
        }
        else if( value instanceof String ) {
            if( type.equals( "java.lang.String" ) ) {
                result = value;
            }
            else {
                throw new IllegalArgumentException( "Cannot evaluate '" + annotationCast + "'." );
            }
        }
        else {
            throw new IllegalArgumentException( "Cannot evaluate '" + annotationCast + "'." );
        }

        return result;
    }

}
