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


import com.thoughtworks.qdox.model.Annotation;

/**
 * Visitor class for the annotation model elements
 * 
 * @author Jochen Kuhnle
 */
public interface AnnotationVisitor {

    public Object visit( Add add );

    public Object visit( Subtract subtract );

    public Object visit( Multiply multiply );

    public Object visit( Divide divide );

    public Object visit( GreaterThan greaterThan );

    public Object visit( LessThan lessThan );

    public Object visit( Annotation annotation );

    public Object visit( Constant constant );

    public Object visit( ParenExpression expression );

    public Object visit( AnnotationValueList valueList );

    public Object visit( TypeRef typeRef );

    public Object visit( FieldRef fieldRef );

    public Object visit( LessEquals lessEquals );

    public Object visit( GreaterEquals greaterEquals );

    public Object visit( Remainder remainder );

    public Object visit( Or or );

    public Object visit( And and );

    public Object visit( ShiftLeft left );

    public Object visit( ShiftRight right );

    public Object visit( Not not );

    public Object visit( LogicalOr or );

    public Object visit( LogicalAnd and );

    public Object visit( LogicalNot not );

    public Object visit( MinusSign sign );

    public Object visit( PlusSign sign );

    public Object visit( UnsignedShiftRight right );

    public Object visit( Equals annotationEquals );

    public Object visit( NotEquals annotationNotEquals );

    public Object visit( ExclusiveOr annotationExclusiveOr );

    public Object visit( Query annotationQuery );

    public Object visit( Cast annotationCast );

}
