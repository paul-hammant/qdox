package com.thoughtworks.qdox.model.annotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;

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

    public Object getValue( Annotation annotation, String property ) {
        Object result = null;
        AnnotationValue value = annotation.getProperty( property );

        if( value != null ) {
            result = value.accept( this );
        }

        return result;
    }

    public List getListValue( Annotation annotation, String property ) {
        Object value = getValue( annotation, property );
        List list = null;

        if( value != null ) {
            if( value instanceof List ) {
                list = (List) value;
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
    protected static Class resultType( Object left, Object right ) {
        Class type = void.class;

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
    protected static Class numericResultType( Object left, Object right ) {
        Class type = void.class;

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
    protected static Class unaryNumericResultType( Object value ) {
        Class type = void.class;

        if( value instanceof Byte || value instanceof Short || value instanceof Character || value instanceof Integer ) {
            type = Integer.class;
        }
        else if( value instanceof Long ) {
            value = Long.class;
        }

        return type;
    }

    protected static Class unaryResultType( Object value ) {
        Class type = unaryNumericResultType( value );

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

    public Object visitAnnotationAdd( AnnotationAdd op ) {
        Object left = op.getLeft().accept( this );
        Object right = op.getRight().accept( this );
        Class type = resultType( left, right );
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

    public Object visitAnnotationConstant( AnnotationConstant constant ) {
        return constant.getValue();
    }

    public Object visitAnnotationDivide( AnnotationDivide op ) {
        Object left = op.getLeft().accept( this );
        Object right = op.getRight().accept( this );
        Class type = resultType( left, right );
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

    public Object visitAnnotationFieldRef( AnnotationFieldRef fieldRef ) {
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

    public Object visitAnnotationGreaterThan( AnnotationGreaterThan op ) {
        Object left = op.getLeft().accept( this );
        Object right = op.getRight().accept( this );
        Class type = resultType( left, right );
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

    public Object visitAnnotationLessThan( AnnotationLessThan op ) {
        Object left = op.getLeft().accept( this );
        Object right = op.getRight().accept( this );
        Class type = resultType( left, right );
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

    public Object visitAnnotationMultiply( AnnotationMultiply op ) {
        Object left = op.getLeft().accept( this );
        Object right = op.getRight().accept( this );
        Class type = resultType( left, right );
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

    public Object visitAnnotationParenExpression( AnnotationParenExpression parenExpression ) {
        return parenExpression.getValue().accept( this );
    }

    public Object visitAnnotationSubtract( AnnotationSubtract op ) {
        Object left = op.getLeft().accept( this );
        Object right = op.getRight().accept( this );
        Class type = resultType( left, right );
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

    public Object visitAnnotationTypeRef( AnnotationTypeRef typeRef ) {
        JavaClass javaClass = typeRef.getType().getJavaClass();
        return javaClass;
    }

    public Object visitAnnotationValueList( AnnotationValueList valueList ) {
        List list = new ArrayList();

        for( ListIterator i = valueList.getValueList().listIterator(); i.hasNext(); ) {
            AnnotationValue value = (AnnotationValue) i.next();
            Object v = value.accept( this );
            list.add( v );
        }

        return list;
    }

    public Object visitAnnotationAnd( AnnotationAnd and ) {
        Object left = and.getLeft().accept( this );
        Object right = and.getRight().accept( this );
        Class type = numericResultType( left, right );
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

    public Object visitAnnotationGreaterEquals( AnnotationGreaterEquals greaterEquals ) {
        Object left = greaterEquals.getLeft().accept( this );
        Object right = greaterEquals.getRight().accept( this );
        Class type = resultType( left, right );
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

    public Object visitAnnotationLessEquals( AnnotationLessEquals lessEquals ) {
        Object left = lessEquals.getLeft().accept( this );
        Object right = lessEquals.getRight().accept( this );
        Class type = resultType( left, right );
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

    public Object visitAnnotationLogicalAnd( AnnotationLogicalAnd and ) {
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

    public Object visitAnnotationLogicalOr( AnnotationLogicalOr or ) {
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
        Class type = unaryResultType( value );
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

    public Object visitAnnotationOr( AnnotationOr or ) {
        Object left = or.getLeft().accept( this );
        Object right = or.getRight().accept( this );
        Class type = numericResultType( left, right );
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

    public Object visitAnnotationRemainder( AnnotationRemainder remainder ) {
        Object left = remainder.getLeft().accept( this );
        Object right = remainder.getRight().accept( this );
        Class type = resultType( left, right );
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

    public Object visitAnnotationShiftLeft( AnnotationShiftLeft shiftLeft ) {
        Object left = shiftLeft.getLeft().accept( this );
        Object right = shiftLeft.getRight().accept( this );
        Class type = numericResultType( left, right );
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

    public Object visitAnnotationShiftRight( AnnotationShiftRight shiftRight ) {
        Object left = shiftRight.getLeft().accept( this );
        Object right = shiftRight.getRight().accept( this );
        Class type = numericResultType( left, right );
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

    public Object visitAnnotationUnsignedShiftRight( AnnotationUnsignedShiftRight shiftRight ) {
        Object left = shiftRight.getLeft().accept( this );
        Object right = shiftRight.getRight().accept( this );
        Class type = numericResultType( left, right );
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

    public Object visitAnnotationEquals( AnnotationEquals annotationEquals ) {
        Object left = annotationEquals.getLeft().accept( this );
        Object right = annotationEquals.getRight().accept( this );
        Class type = resultType( left, right );
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

    public Object visitAnnotationExclusiveOr( AnnotationExclusiveOr annotationExclusiveOr ) {
        Object left = annotationExclusiveOr.getLeft().accept( this );
        Object right = annotationExclusiveOr.getRight().accept( this );
        Class type = numericResultType( left, right );
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

    public Object visitAnnotationNotEquals( AnnotationNotEquals annotationNotEquals ) {
        Object left = annotationNotEquals.getLeft().accept( this );
        Object right = annotationNotEquals.getRight().accept( this );
        Class type = resultType( left, right );
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

    public Object visitAnnotationQuery( AnnotationQuery annotationQuery ) {
        Object value = annotationQuery.getCondition().accept( this );

        if( value == null || !(value instanceof Boolean) ) {
            throw new IllegalArgumentException( "Cannot evaluate '" + annotationQuery + "'." );
        }

        AnnotationValue expression = ((Boolean) value).booleanValue() ? annotationQuery.getTrueExpression()
            : annotationQuery.getFalseExpression();

        return expression.accept( this );
    }

    public Object visitAnnotationCast( AnnotationCast annotationCast ) {
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
