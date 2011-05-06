package com.thoughtworks.qdox.parser.expression;

import com.thoughtworks.qdox.parser.structs.AnnoDef;

public interface AnnotationTransformer<U>
{

    U transform( AnnoDef annoDef );

    U transform( AnnotationAdd annotationAdd );

    U transform( AnnotationAnd annotationAnd );

    U transform( AnnotationDivide annotationDivide );

    U transform( AnnotationEquals annotationEquals );

    U transform( AnnotationExclusiveOr annotationExclusiveOr );

    U transform( AnnotationGreaterEquals annotationGreaterEquals );

    U transform( AnnotationGreaterThan annotationGreaterThan );

    U transform( AnnotationLessEquals annotationLessEquals );

    U transform( AnnotationLessThan annotationLessThan );

    U transform( AnnotationLogicalAnd annotationLogicalAnd );

    U transform( AnnotationLogicalOr annotationLogicalOr );

    U transform( AnnotationMultiply annotationMultiply );

    U transform( AnnotationNotEquals annotationNotEquals );

    U transform( AnnotationOr annotationOr );

    U transform( AnnotationRemainder annotationRemainder );

    U transform( AnnotationShiftLeft annotationShiftLeft );

    U transform( AnnotationShiftRight annotationShiftRight );

    U transform( AnnotationSubtract annotationSubtract );

    U transform( AnnotationUnsignedShiftRight annotationUnsignedShiftRight );

    U transform( AnnotationCast annotationCast );

    U transform( AnnotationConstant annotationConstant );

    U transform( AnnotationFieldRef annotationFieldRef );

    U transform( AnnotationLogicalNot annotationLogicalNot );

    U transform( AnnotationMinusSign annotationMinusSign );

    U transform( AnnotationNot annotationNot );

    U transform( AnnotationParenExpression annotationParenExpression );

    U transform( AnnotationPlusSign annotationPlusSign );

    U transform( AnnotationQuery annotationQuery );

    U transform( AnnotationTypeRef annotationTypeRef );

    U transform( ElemValueListDef elemValueListDef );

}