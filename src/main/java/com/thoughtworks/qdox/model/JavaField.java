package com.thoughtworks.qdox.model;

import java.lang.reflect.Field;
import java.util.List;

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
 * Modeled equivalent of {@link Field}, providing the most important methods.
 * Where the original {@link Field} is using an Array, this model is using a {@link List}.
 * 
 */
public interface JavaField extends JavaAnnotatedElement, JavaMember, JavaModel
{
    // Methods of Field

    /**
     * Equivalent of {@link Field#getType()}
     * 
     * @return the type of this field, should never be <code>null</code>.
     */
    Type getType();
    
    // Source methods

    /**
     * The in-code representation of this field.
     * 
     * @return the complete representation of this field
     */
    String getCodeBlock();

    String getDeclarationSignature( boolean withModifiers );

    String getCallSignature();

    /**
     * Get the original expression used to initialize the field.
     *
     * @return initialization as string.
     */
    String getInitializationExpression();

}