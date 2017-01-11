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

import java.io.Serializable;
import java.util.List;

/**
 * Modeled equivalent of {@link java.lang.reflect.Constructor}, providing the most important methods.
 * Where the original Constructor is using an Array, this model is using a {@link List}.
 * 
 * @author Robert Scholte
 * @since 2.0
 */
public interface JavaConstructor
    extends JavaModel, JavaAnnotatedElement, JavaGenericDeclaration, JavaMember, JavaExecutable, Serializable
{
    // Utility methods
    
    /**
     * Returns <code>true</code> if this constructor matches the parameterTypes, assuming it's a non-varArg constructor.
     * 
     * @param parameterTypes the parameter types
     * @return <code>true</code> if signature matches, otherwise <code>false</code>
     */
    boolean signatureMatches( List<JavaType> parameterTypes );

    /**
     * Returns <code>true</code> if this constructor matches the parameterTypes and matches the varArg argument.
     * 
     * @param parameterTypes the parameter types
     * @param varArgs {@code true} if the last argument should be a varArg, otherwise {@code false} 
     * @return <code>true</code> if signature matches, otherwise <code>false</code>
     */
    boolean signatureMatches( List<JavaType> parameterTypes, boolean varArgs );

}
