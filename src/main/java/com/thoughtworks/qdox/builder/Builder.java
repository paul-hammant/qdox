package com.thoughtworks.qdox.builder;

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

import com.thoughtworks.qdox.model.JavaModule;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.parser.expression.ExpressionDef;
import com.thoughtworks.qdox.parser.structs.AnnoDef;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.FieldDef;
import com.thoughtworks.qdox.parser.structs.InitDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;
import com.thoughtworks.qdox.parser.structs.ModuleDef;
import com.thoughtworks.qdox.parser.structs.PackageDef;
import com.thoughtworks.qdox.parser.structs.TagDef;
import com.thoughtworks.qdox.writer.ModelWriterFactory;

public interface Builder
{
    void setModelWriterFactory( ModelWriterFactory writer );
    
    void setUrl( URL url );
    
    void setModule( ModuleDef moduleDef );
    
    void addExports( ModuleDef.ExportsDef exports );
    void addRequires( ModuleDef.RequiresDef requires );
    void addOpens( ModuleDef.OpensDef opens );
    void addProvides( ModuleDef.ProvidesDef provides );
    void addUses( ModuleDef.UsesDef uses );

    void addPackage( PackageDef packageDef );

    void addImport( String importName );

    void addJavaDoc( String text );
    void addJavaDocTag( TagDef def );

    void beginClass( ClassDef def );
    void endClass();
    
    void addInitializer( InitDef def );

    void beginConstructor();
    void endConstructor( MethodDef def );

    void beginMethod();
    void endMethod( MethodDef def );

    void beginField( FieldDef def );
    void endField();

    void addParameter( FieldDef def );

    void addAnnotation( AnnoDef annotation );

    void addArgument(  ExpressionDef argument );

    JavaSource getSource();

    JavaModule getModuleInfo();
}