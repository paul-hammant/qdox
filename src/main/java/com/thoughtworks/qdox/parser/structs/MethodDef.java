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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class MethodDef extends LocatedDef {

    public String name = "";
    public List<TypeVariableDef> typeParams;
    public TypeDef returnType;
    public Set<String> modifiers = new HashSet<String>();
    public List<TypeVariableDef> params = new LinkedList<TypeVariableDef>();
    public Set<TypeDef> exceptions = new LinkedHashSet<TypeDef>();
    public boolean constructor = false;
    public int dimensions;
    public String body;

    @Override
    public boolean equals(Object obj) {
        MethodDef methodDef = (MethodDef) obj;
        boolean result;
        result = methodDef.name.equals(name)
                && methodDef.modifiers.equals(modifiers)
                && methodDef.params.equals(params)
                && methodDef.exceptions.equals(exceptions)
                && methodDef.constructor == constructor;
        if(methodDef.returnType == null) {
        	result &= (returnType == null)
        		&& methodDef.dimensions == dimensions;
        	
        }
        else {
        	result &= (returnType != null)        		
        			&&(methodDef.returnType.name.equals(returnType.name))
        			&&(methodDef.returnType.actualArgumentTypes == null ? returnType.actualArgumentTypes == null: methodDef.returnType.actualArgumentTypes.equals(returnType.actualArgumentTypes))
        			&&(methodDef.returnType.dimensions + methodDef.dimensions == dimensions + returnType.dimensions);
        }
        return result;
    }

    @Override
    public int hashCode() {
        return name.hashCode() + 
        		(returnType != null ? returnType.hashCode() : 0) +
                modifiers.hashCode() + params.hashCode() +
                params.hashCode() + exceptions.hashCode() +
                dimensions + (constructor ? 0 : 1);
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(modifiers);
        result.append(' ');
        result.append((returnType != null ? returnType.toString() : ""));
        for (int i = 0; i < dimensions; i++) result.append("[]");
        result.append(' ');
        result.append(name);
        result.append('(');
        result.append(params);
        result.append(')');
        result.append(" throws ");
        result.append(exceptions);
        result.append(body);
        return result.toString();
    }
}
