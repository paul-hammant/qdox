package com.thoughtworks.qdox.model.annotation;

import com.thoughtworks.qdox.model.Annotation;

/**
 * Visitor class for the annotation model elements
 * 
 * @author Jochen Kuhnle
 */
public interface AnnotationVisitor {

    public Object visitAnnotationAdd( AnnotationAdd add );

    public Object visitAnnotationSubtract( AnnotationSubtract subtract );

    public Object visitAnnotationMultiply( AnnotationMultiply multiply );

    public Object visitAnnotationDivide( AnnotationDivide divide );

    public Object visitAnnotationGreaterThan( AnnotationGreaterThan greaterThan );

    public Object visitAnnotationLessThan( AnnotationLessThan lessThan );

    public Object visitAnnotation( Annotation annotation );

    public Object visitAnnotationConstant( AnnotationConstant constant );

    public Object visitAnnotationParenExpression( AnnotationParenExpression expression );

    public Object visitAnnotationValueList( AnnotationValueList valueList );

    public Object visitAnnotationTypeRef( AnnotationTypeRef typeRef );

    public Object visitAnnotationFieldRef( AnnotationFieldRef fieldRef );

    public Object visitAnnotationLessEquals( AnnotationLessEquals lessEquals );

    public Object visitAnnotationGreaterEquals( AnnotationGreaterEquals greaterEquals );

    public Object visitAnnotationRemainder( AnnotationRemainder remainder );

    public Object visitAnnotationOr( AnnotationOr or );

    public Object visitAnnotationAnd( AnnotationAnd and );

    public Object visitAnnotationShiftLeft( AnnotationShiftLeft left );

    public Object visitAnnotationShiftRight( AnnotationShiftRight right );

    public Object visitAnnotationNot( AnnotationNot not );

    public Object visitAnnotationLogicalOr( AnnotationLogicalOr or );

    public Object visitAnnotationLogicalAnd( AnnotationLogicalAnd and );

    public Object visitAnnotationLogicalNot( AnnotationLogicalNot not );

    public Object visitAnnotationMinusSign( AnnotationMinusSign sign );

    public Object visitAnnotationPlusSign( AnnotationPlusSign sign );

    public Object visitAnnotationUnsignedShiftRight( AnnotationUnsignedShiftRight right );

    public Object visitAnnotationEquals( AnnotationEquals annotationEquals );

    public Object visitAnnotationNotEquals( AnnotationNotEquals annotationNotEquals );

    public Object visitAnnotationExclusiveOr( AnnotationExclusiveOr annotationExclusiveOr );

    public Object visitAnnotationQuery( AnnotationQuery annotationQuery );

    public Object visitAnnotationCast( AnnotationCast annotationCast );

}
