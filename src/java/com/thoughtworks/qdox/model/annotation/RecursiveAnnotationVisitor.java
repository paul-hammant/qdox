package com.thoughtworks.qdox.model.annotation;

import java.util.ListIterator;

import com.thoughtworks.qdox.model.Annotation;

public class RecursiveAnnotationVisitor implements AnnotationVisitor {

    public Object visitAnnotation( Annotation annotation ) {
        for( AnnotationValue value : annotation.getPropertyMap().values()) {
            value.accept( this );
        }
        return null;
    }

    public Object visitAnnotationAdd( AnnotationAdd op ) {
        op.getLeft().accept( this );
        op.getRight().accept( this );

        return null;
    }

    public Object visitAnnotationConstant( AnnotationConstant constant ) {
        return null;
    }

    public Object visitAnnotationDivide( AnnotationDivide op ) {
        op.getLeft().accept( this );
        op.getRight().accept( this );

        return null;
    }

    public Object visitAnnotationFieldRef( AnnotationFieldRef fieldRef ) {
        return null;
    }

    public Object visitAnnotationGreaterThan( AnnotationGreaterThan op ) {
        op.getLeft().accept( this );
        op.getRight().accept( this );

        return null;
    }

    public Object visitAnnotationLessThan( AnnotationLessThan op ) {
        op.getLeft().accept( this );
        op.getRight().accept( this );

        return null;
    }

    public Object visitAnnotationMultiply( AnnotationMultiply op ) {
        op.getLeft().accept( this );
        op.getRight().accept( this );

        return null;
    }

    public Object visitAnnotationParenExpression( AnnotationParenExpression parenExpression ) {
        parenExpression.getValue().accept( this );

        return null;
    }

    public Object visitAnnotationSubtract( AnnotationSubtract op ) {
        op.getLeft().accept( this );
        op.getRight().accept( this );

        return null;
    }

    public Object visitAnnotationTypeRef( AnnotationTypeRef typeRef ) {
        return null;
    }

    public Object visitAnnotationValueList( AnnotationValueList valueList ) {
        for( AnnotationValue value : valueList.getValueList() ) {
            value.accept( this );
        }

        return null;
    }

    public Object visitAnnotationAnd( AnnotationAnd and ) {
        and.getLeft().accept( this );
        and.getRight().accept( this );

        return null;
    }

    public Object visitAnnotationGreaterEquals( AnnotationGreaterEquals greaterEquals ) {
        greaterEquals.getLeft().accept( this );
        greaterEquals.getRight().accept( this );

        return null;
    }

    public Object visitAnnotationLessEquals( AnnotationLessEquals lessEquals ) {
        lessEquals.getLeft().accept( this );
        lessEquals.getRight().accept( this );

        return null;
    }

    public Object visitAnnotationLogicalAnd( AnnotationLogicalAnd and ) {
        and.getLeft().accept( this );
        and.getRight().accept( this );

        return null;
    }

    public Object visitAnnotationLogicalNot( AnnotationLogicalNot not ) {
        not.getValue().accept( this );

        return null;
    }

    public Object visitAnnotationLogicalOr( AnnotationLogicalOr or ) {
        or.getLeft().accept( this );
        or.getRight().accept( this );

        return null;
    }

    public Object visitAnnotationMinusSign( AnnotationMinusSign sign ) {
        sign.getValue().accept( this );

        return null;
    }

    public Object visitAnnotationNot( AnnotationNot not ) {
        not.getValue().accept( this );

        return null;
    }

    public Object visitAnnotationOr( AnnotationOr or ) {
        or.getLeft().accept( this );
        or.getRight().accept( this );

        return null;
    }

    public Object visitAnnotationPlusSign( AnnotationPlusSign sign ) {
        sign.getValue().accept( this );

        return null;
    }

    public Object visitAnnotationRemainder( AnnotationRemainder remainder ) {
        remainder.getLeft().accept( this );
        remainder.getRight().accept( this );

        return null;
    }

    public Object visitAnnotationShiftLeft( AnnotationShiftLeft left ) {
        left.getLeft().accept( this );
        left.getRight().accept( this );

        return null;
    }

    public Object visitAnnotationShiftRight( AnnotationShiftRight right ) {
        right.getLeft().accept( this );
        right.getRight().accept( this );

        return null;
    }

    public Object visitAnnotationUnsignedShiftRight( AnnotationUnsignedShiftRight right ) {
        right.getLeft().accept( this );
        right.getRight().accept( this );

        return null;
    }

    public Object visitAnnotationEquals( AnnotationEquals annotationEquals ) {
        annotationEquals.getLeft().accept( this );
        annotationEquals.getRight().accept( this );

        return null;
    }

    public Object visitAnnotationExclusiveOr( AnnotationExclusiveOr annotationExclusiveOr ) {
        annotationExclusiveOr.getLeft().accept( this );
        annotationExclusiveOr.getRight().accept( this );

        return null;
    }

    public Object visitAnnotationNotEquals( AnnotationNotEquals annotationNotEquals ) {
        annotationNotEquals.getLeft().accept( this );
        annotationNotEquals.getRight().accept( this );

        return null;
    }

    public Object visitAnnotationQuery( AnnotationQuery annotationQuery ) {
        annotationQuery.getCondition().accept( this );
        annotationQuery.getTrueExpression().accept( this );
        annotationQuery.getFalseExpression().accept( this );

        return null;
    }

    public Object visitAnnotationCast( AnnotationCast annotationCast ) {
        annotationCast.getValue().accept( this );

        return null;
    }

}
