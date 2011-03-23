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
import java.util.Set;

public class FieldDef extends LocatedDef {
    public String name = "";
    public TypeDef type;
    public Set<String> modifiers = new HashSet<String>();
    public int dimensions;
    public boolean isVarArgs;
    public String body = "";

    public boolean equals(Object obj) {
        FieldDef paramDef = (FieldDef) obj;
        boolean result = paramDef.name.equals(name)
                && paramDef.modifiers.equals(modifiers)
                && paramDef.isVarArgs == isVarArgs;
        if(paramDef.type == null) {
        	result &= (type == null)
        		&& paramDef.dimensions == dimensions;
        }
        else {
        	result &= (type != null)
        		&&(paramDef.type.name.equals(type.name))
        		&&(paramDef.type.actualArgumentTypes == null ? type.actualArgumentTypes == null: paramDef.type.actualArgumentTypes.equals(type.actualArgumentTypes))
        		&&(paramDef.type.dimensions + paramDef.dimensions == dimensions + type.dimensions);
        }
        return result;
    }

    public int hashCode() {
        return name.hashCode() + (type != null ? type.hashCode() : 0) +
                dimensions + modifiers.hashCode() + (isVarArgs ? 79769989 : 0);
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(modifiers);
        result.append(' ');
        result.append(type);
        for (int i = 0; i < dimensions; i++) result.append("[]");
        result.append(' ');
        result.append(name);
        if(body.length() > 0){
            result.append(" = ").append(body);
        }
        return result.toString();
    }
}
