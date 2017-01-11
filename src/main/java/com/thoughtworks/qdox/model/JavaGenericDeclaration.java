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

import java.util.List;

/**
 * Equivalent of {@link java.lang.reflect.GenericDeclaration}.
 * Where the original GenericDeclaration uses an Array, the JavaGenericDeclaration is using a {@link List}.
 * 
 * @author Robert Scholte
 * @since 2.0
 */
public interface JavaGenericDeclaration
{
    /**
     * Equivalent of {@link java.lang.reflect.GenericDeclaration#getTypeParameters()}
     * 
     * @param <D> the type
     * @return a list of typeParameters, never <code>null</code> 
     */
    <D extends JavaGenericDeclaration> List<JavaTypeVariable<D>> getTypeParameters();
}
