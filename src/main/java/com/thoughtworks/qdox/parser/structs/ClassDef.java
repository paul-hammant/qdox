package com.thoughtworks.qdox.parser.structs;

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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ClassDef extends LocatedDef {
    
    public static final String CLASS = "class";
    public static final String INTERFACE = "interface";
    public static final String ENUM = "enum";
    public static final String ANNOTATION_TYPE = "@interface";
    
    public String name = "";
    public Set<String> modifiers = new HashSet<String>();
    public List<TypeVariableDef> typeParams = new LinkedList<TypeVariableDef>();
    public Set<TypeDef> extendz = new HashSet<TypeDef>();
    public Set<TypeDef> implementz = new HashSet<TypeDef>();
    public String type = CLASS;

    @Override
    public boolean equals(Object obj) {
        ClassDef classDef = (ClassDef) obj;
        return classDef.name.equals(name)
                && classDef.type == type
                && classDef.typeParams.equals( typeParams )
                && classDef.modifiers.equals(modifiers)
                && classDef.extendz.equals(extendz)
                && classDef.implementz.equals(implementz);
    }

    @Override
    public int hashCode() {
        return name.hashCode() + type.hashCode() + typeParams.hashCode()+
                modifiers.hashCode() + extendz.hashCode() +
                implementz.hashCode();
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(modifiers);
        result.append(' ');
        result.append(type);
        result.append(' ');
        result.append(name);
        //typeParams
        result.append(" extends ");
        result.append(extendz);
        result.append(" implements ");
        result.append(implementz);
        return result.toString();
    }
}
