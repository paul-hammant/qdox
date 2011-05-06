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


import com.thoughtworks.qdox.model.Annotation;
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
import com.thoughtworks.qdox.model.expression.LogicalNot;
import com.thoughtworks.qdox.model.expression.LogicalOr;
import com.thoughtworks.qdox.model.expression.MinusSign;
import com.thoughtworks.qdox.model.expression.Multiply;
import com.thoughtworks.qdox.model.expression.Not;
import com.thoughtworks.qdox.model.expression.NotEquals;
import com.thoughtworks.qdox.model.expression.Or;
import com.thoughtworks.qdox.model.expression.ParenExpression;
import com.thoughtworks.qdox.model.expression.PlusSign;
import com.thoughtworks.qdox.model.expression.Query;
import com.thoughtworks.qdox.model.expression.Remainder;
import com.thoughtworks.qdox.model.expression.ShiftLeft;
import com.thoughtworks.qdox.model.expression.ShiftRight;
import com.thoughtworks.qdox.model.expression.Subtract;
import com.thoughtworks.qdox.model.expression.TypeRef;
import com.thoughtworks.qdox.model.expression.UnsignedShiftRight;

/**
 * Visitor class for the annotation model elements
 * 
 * @author Jochen Kuhnle
 */
public interface AnnotationVisitor {

    public Object visitAnnotationAdd( Add add );

    public Object visitAnnotationSubtract( Subtract subtract );

    public Object visitAnnotationMultiply( Multiply multiply );

    public Object visitAnnotationDivide( Divide divide );

    public Object visitAnnotationGreaterThan( GreaterThan greaterThan );

    public Object visitAnnotationLessThan( LessThan lessThan );

    public Object visitAnnotation( Annotation annotation );

    public Object visitAnnotationConstant( Constant constant );

    public Object visitAnnotationParenExpression( ParenExpression expression );

    public Object visitAnnotationValueList( AnnotationValueList valueList );

    public Object visitAnnotationTypeRef( TypeRef typeRef );

    public Object visitAnnotationFieldRef( FieldRef fieldRef );

    public Object visitAnnotationLessEquals( LessEquals lessEquals );

    public Object visitAnnotationGreaterEquals( GreaterEquals greaterEquals );

    public Object visitAnnotationRemainder( Remainder remainder );

    public Object visitAnnotationOr( Or or );

    public Object visitAnnotationAnd( And and );

    public Object visitAnnotationShiftLeft( ShiftLeft left );

    public Object visitAnnotationShiftRight( ShiftRight right );

    public Object visitAnnotationNot( Not not );

    public Object visitAnnotationLogicalOr( LogicalOr or );

    public Object visitAnnotationLogicalAnd( LogicalAnd and );

    public Object visitAnnotationLogicalNot( LogicalNot not );

    public Object visitAnnotationMinusSign( MinusSign sign );

    public Object visitAnnotationPlusSign( PlusSign sign );

    public Object visitAnnotationUnsignedShiftRight( UnsignedShiftRight right );

    public Object visitAnnotationEquals( Equals annotationEquals );

    public Object visitAnnotationNotEquals( NotEquals annotationNotEquals );

    public Object visitAnnotationExclusiveOr( ExclusiveOr annotationExclusiveOr );

    public Object visitAnnotationQuery( Query annotationQuery );

    public Object visitAnnotationCast( Cast annotationCast );

}
