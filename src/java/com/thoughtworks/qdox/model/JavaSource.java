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

import java.io.File;
import java.net.URL;
import java.util.List;

import com.thoughtworks.qdox.io.ModelWriter;

public interface JavaSource extends JavaClassParent
{

    /**
     * 
     * @return
     * @since 2.0
     */
    public ModelWriter getModelWriter();

    /**
     * @since 1.4
     */
    public URL getURL();

    /**
     * @deprecated use getURL
     */
    public File getFile();

    public JavaPackage getPackage();

    /**
     * Retrieve all the import
     * 
     * @return the imports, never null
     */
    public List<String> getImports();

    public List<JavaClass> getClasses();

    public String getCodeBlock();

    public String resolveType( String typeName );

    public String getClassNamePrefix();

    public JavaSource getParentSource();

    public JavaClass getNestedClassByName( String name );

    public com.thoughtworks.qdox.library.ClassLibrary getJavaClassLibrary();

    public String getPackageName();

}