package com.thoughtworks.qdox.model;

import java.util.List;

import com.thoughtworks.qdox.model.expression.Expression;

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

/**
 * Modeled equivalent of {@link java.lang.reflect.Field}, providing the most important methods.
 * Where the original Field is using an Array, this model is using a {@link java.util.List}.
 * 
 */
public interface JavaField extends JavaAnnotatedElement, JavaMember, JavaModel
{
    // Methods of Field

    /**
     * Equivalent of {@link java.lang.reflect.Field#getType()}
     * 
     * @return the type of this field, should never be <code>null</code>.
     */
    JavaClass getType();
    
    // Source methods

    /**
     * The in-code representation of this field.
     * 
     * @return the complete representation of this field
     */
    String getCodeBlock();

    /**
     * Get the original expression used to initialize the field.
     *
     * @return initialization as string.
     */
    String getInitializationExpression();
    
    /**
     * Equivalent of {@link java.lang.reflect.Field#isEnumConstant()}
     * 
     * @return <code>true</code> if this field is an enum constant, otherwise <code>false</code>
     * @since 2.0
     */
    boolean isEnumConstant();
    
    /**
     *  
     * @return the classBody of the enum constant
     * @since 2.0 
     */
    JavaClass getEnumConstantClass();

    /**
     * 
     * 
     * @return the arguments of the enum constant
     * @since 2.0
     */
    List<Expression> getEnumConstantArguments();
}