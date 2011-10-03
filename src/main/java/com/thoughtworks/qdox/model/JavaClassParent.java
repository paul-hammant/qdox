package com.thoughtworks.qdox.model;

import com.thoughtworks.qdox.library.ClassLibrary;

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
 * @deprecated this interface will be hidden for the model interfaces
 */
public interface JavaClassParent {

    /**
     * Resolve a type-name within the context of this source or class.
     * @param typeName name of a type
     * @return the fully-qualified name of the type, or null if it cannot
     *     be resolved
     */
    String resolveType(String typeName);
    
    String resolveCanonicalName(String typeName);
    
    String resolveFullyQualifiedName(String typeName);

    JavaSource getParentSource();

    JavaClass getNestedClassByName(String name);
    
    ClassLibrary getJavaClassLibrary(); 
    
}
