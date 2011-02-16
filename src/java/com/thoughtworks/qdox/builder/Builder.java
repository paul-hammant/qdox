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

import com.thoughtworks.qdox.io.ModelWriterFactory;
import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.Type;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.FieldDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;
import com.thoughtworks.qdox.parser.structs.PackageDef;
import com.thoughtworks.qdox.parser.structs.TagDef;
import com.thoughtworks.qdox.parser.structs.TypeDef;

public interface Builder {

    void setModelWriterFactory(ModelWriterFactory writer);
    
    void addPackage(PackageDef packageDef);

    void addImport(String importName);

    void addJavaDoc(String text);

    void addJavaDocTag(TagDef def);

    void beginClass(ClassDef def);

    void endClass();

    void beginMethod();
    void endMethod(MethodDef def);

    void addParameter(FieldDef def);
    
    void addField(FieldDef def);

    void addAnnotation(Annotation annotation);
    
    /**
     * @deprecated
     */
    Type createType(String name, int dimensions);
}
