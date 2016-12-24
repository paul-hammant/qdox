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

import com.thoughtworks.qdox.parser.structs.AnnoDef;

public interface ElemValueTransformer<U>
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

    U transform( PostIncrementDef postIncrement );

    U transform( PostDecrementDef postDecrementDef );

    U transform( PreDecrementDef preDecrementDef );

    U transform( PreIncrementDef preIncrementDef );

    U transform( MethodInvocationDef methodInvocationDef );

    U transform( MethodReferenceDef methodReferenceDef );

    U transform( CreatorDef newCreator );

}