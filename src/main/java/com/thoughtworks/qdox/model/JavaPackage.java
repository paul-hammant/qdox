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

public interface JavaPackage extends JavaModel, JavaAnnotatedElement
{

    /**
     * Returns all the classes found for the package.
     *
     * @return all the classes found for the package
     */
    List<JavaClass> getClasses();

    /**
     * The parent of this package
     * 
     * For instance: the package of <code>java.lang.reflect</code> is <code>java.lang</code>
     * 
     * @return the parent package
     */
    JavaPackage getParentPackage();

    /**
     * For instance: one of the children of <code>java.lang</code> would be <code>java.lang.reflect</code>
     * 
     * @return all the children of this package 
     */
    List<JavaPackage> getSubPackages();

    /**
     * The name of this package
     * 
     * @return the name
     */
    String getName();

}