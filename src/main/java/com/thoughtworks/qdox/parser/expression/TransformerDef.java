package com.thoughtworks.qdox.parser.expression;

import com.thoughtworks.qdox.parser.structs.AnnoDef;

public interface TransformerDef<U>
{

    U transform( AnnoDef annoDef );

    U transform( AddDef annotationAdd );

    U transform( AndDef annotationAnd );

    U transform( DivideDef annotationDivide );

    U transform( EqualsDef annotationEquals );

    U transform( ExclusiveOrDef annotationExclusiveOr );

    U transform( GreaterEqualsDef annotationGreaterEquals );

    U transform( GreaterThanDef annotationGreaterThan );

    U transform( LessEqualsDef annotationLessEquals );

    U transform( LessThanDef annotationLessThan );

    U transform( LogicalAndDef annotationLogicalAnd );

    U transform( LogicalOrDef annotationLogicalOr );

    U transform( MultiplyDef annotationMultiply );

    U transform( NotEqualsDef annotationNotEquals );

    U transform( OrDef annotationOr );

    U transform( RemainderDef annotationRemainder );

    U transform( ShiftLeftDef annotationShiftLeft );

    U transform( ShiftRightDef annotationShiftRight );

    U transform( SubtractDef annotationSubtract );

    U transform( UnsignedShiftRightDef annotationUnsignedShiftRight );

    U transform( CastDef annotationCast );

    U transform( ConstantDef annotationConstant );

    U transform( FieldRefDef annotationFieldRef );

    U transform( LogicalNotDef annotationLogicalNot );

    U transform( MinusSignDef annotationMinusSign );

    U transform( NotDef annotationNot );

    U transform( ParenExpressionDef annotationParenExpression );

    U transform( PlusSignDef annotationPlusSign );

    U transform( QueryDef annotationQuery );

    U transform( TypeRefDef annotationTypeRef );

    U transform( ElemValueListDef elemValueListDef );

    U transform( AssignmentDef assignmentDef );
}