package com.thoughtworks.qdox.model.expression;

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

import com.thoughtworks.qdox.model.JavaAnnotation;

/**
 * Visitor class for the annotation model elements
 * 
 * @author Jochen Kuhnle
 */
public interface ExpressionVisitor
{

    Object visit( Add add );

    Object visit( Subtract subtract );

    Object visit( Multiply multiply );

    Object visit( Divide divide );

    Object visit( GreaterThan greaterThan );

    Object visit( LessThan lessThan );

    Object visit( JavaAnnotation annotation );

    Object visit( Constant constant );

    Object visit( ParenExpression expression );

    Object visit( AnnotationValueList valueList );

    Object visit( TypeRef typeRef );

    Object visit( FieldRef fieldRef );

    Object visit( LessEquals lessEquals );

    Object visit( GreaterEquals greaterEquals );

    Object visit( Remainder remainder );

    Object visit( Or or );

    Object visit( And and );

    Object visit( ShiftLeft left );

    Object visit( ShiftRight right );

    Object visit( Not not );

    Object visit( LogicalOr or );

    Object visit( LogicalAnd and );

    Object visit( LogicalNot not );

    Object visit( MinusSign sign );

    Object visit( PlusSign sign );

    Object visit( UnsignedShiftRight right );

    Object visit( Equals annotationEquals );

    Object visit( NotEquals annotationNotEquals );

    Object visit( ExclusiveOr annotationExclusiveOr );

    Object visit( Query annotationQuery );

    Object visit( Cast annotationCast );

    Object visit( PreDecrement preDecrement );

    Object visit( PreIncrement preIncrement );

    Object visit( PostDecrement postDecrement );

    Object visit( PostIncrement postIncrement );

    Object visit( Assignment assignment );

    Object visit( MethodInvocation methodInvocation );

}