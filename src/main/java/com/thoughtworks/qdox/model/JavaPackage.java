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

import java.util.Collection;

import com.thoughtworks.qdox.library.ClassLibrary;

public interface JavaPackage extends JavaModel, JavaAnnotatedElement
{

    /**
     * Returns all the classes found for the package.
     *
     * @return all the classes found for the package, never <code>null</code>
     */
    Collection<JavaClass> getClasses();
    
    /**
     * Try to get any class of this package by name.
     * The name can be both the fully qualified name or just the name of the class.
     * 
     * @param name the (fully qualified) name of the class 
     * @return the matching class, otherwise <code>null</code>
     * @since 2.0
     */
    JavaClass getClassByName( String name );

    /**
     * The parent of this package
     * 
     * For instance: the package of <code>java.lang.reflect</code> is <code>java.lang</code>
     * 
     * @return the parent package, otherwise <code>null</code>
     */
    JavaPackage getParentPackage();

    /**
     * For instance: one of the children of <code>java.lang</code> would be <code>java.lang.reflect</code>
     * 
     * @return all the children of this package , never <code>null</code>
     */
    Collection<JavaPackage> getSubPackages();

    /**
     * Equivalent of {@link Package#getName()}
     * 
     * @return the name, should never be <code>null</code>
     */
    String getName();
    
    /**
     * The {@link ClassLibrary} of this package. 
     * 
     * @return the classLibrary, should never be <code>null</code>
     */
    ClassLibrary getJavaClassLibrary();
    
    /**
     * Equivalent of {@link Package#toString()}
     * 
     * @return the string representation of the package.
     */
    String toString();
}