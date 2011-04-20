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

import java.net.URL;
import java.util.List;

import com.thoughtworks.qdox.library.ClassLibrary;

/**
 * The root of every JavaModel, even for those based on binary classes.
 * 
 * @author Robert Scholte
 */
public interface JavaSource extends JavaClassParent
{

    /**
     * @since 1.4
     */
    URL getURL();

    /**
     * The package of this source or <code>null</code>
     * 
     * @return the package
     */
    JavaPackage getPackage();

    /**
     * Retrieve all the import
     * 
     * @return the imports, never <code>null</code>
     */
    List<String> getImports();

    /**
     * A List with all direct classes of this source, never <code>null</code>
     * 
     * @return
     */
    List<JavaClass> getClasses();

    /**
     * Complete code representation of this source
     * 
     * @return
     */
    String getCodeBlock();

    String resolveType( String typeName );

    /**
     * If there's a package, return the packageName, followed by a dot, otherwise an empty String
     * 
     * @return
     */
    String getClassNamePrefix();

    /**
     * Try to get the JavaClass child based on its name relative to the package.
     * This doesn't try to resolve it by recursion.
     * 
     * @return the resolved JavaClass or <code>null</code>
     */
    JavaClass getNestedClassByName( String name );

    ClassLibrary getJavaClassLibrary();

    /**
     * Returns the name of the package or an empty String  if there's no package
     * 
     * @return the name 
     */
    String getPackageName();

}