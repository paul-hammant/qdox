package com.thoughtworks.qdox.model;

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
 * JavaParameter is an extended version of JavaClass and doesn't exist in the java api. 
 * 
 * @author Robert Scholte
 *
 */
public interface JavaParameter extends JavaAnnotatedElement, JavaType
{

    /**
     * 
     * @return the name of the parameter
     */
    String getName();

    /**
     * 
     * @return the type of this parameter
     */
    JavaType getType();
    
    JavaClass getJavaClass();

    /**
     * Returns the declaring method or constructor of this parameter
     * 
     * @return the declaring method or constructor
     */
    JavaExecutable getExecutable();

    /**
     * The declaring class of the declaring method of this parameter.
     * 
     * @return the declaring class of the declaring method
     */
    JavaClass getDeclaringClass();

    /**
     * Is this a Java 5 var args type specified using three dots. e.g. <code>void doStuff(Object... thing)</code>
     * 
     * @return {@code true} if this parameter is a varArg, otherwise {@code false}
     * @since 1.6
     */
    boolean isVarArgs();
    
    /**
     * 
     * @return the value of the type, never <code>null</code>
     */
    String getValue();
    
    /**
     * 
     * @return the FQN of the type, never <code>null</code>
     */
    String getFullyQualifiedName();
    
    /**
     * @return the canonical name of the type, never <code>null</code>
     * @since 2.0
     */
    String getCanonicalName();

    /**
     * @return the resolved value of the type
     * @since 1.10
     */
    String getResolvedValue();

    /**
     * 
     * @return the resolved generic value of the type, never <code>null</code>
     * @since 2.0
     */
    String getResolvedGenericValue();
    
    /**
     * 
     * @return the resolved FQN, never <code>null</code>
     * @since 2.0
     */
    String getResolvedFullyQualifiedName();

    /**
     * 
     * @return the resolved generic FQN, never <code>null</code>
     * @since 2.0
     */
    String getResolvedGenericFullyQualifiedName();
}