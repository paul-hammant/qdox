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

import java.util.List;

public class TypeDef {

    public String name;
    public int dimensions;
    public List<TypeDef> actualArgumentTypes; 

    public TypeDef(String name, int dimensions) {
        this.name = name;
        this.dimensions = dimensions;
    }

	public TypeDef(String name) {
		this(name, 0);
	}
	
	@Override
	public boolean equals(Object obj) {
		TypeDef typeDef = (TypeDef) obj;
        return typeDef.name.equals(name)
                && typeDef.dimensions == dimensions
                && (typeDef.actualArgumentTypes != null ? typeDef.actualArgumentTypes.equals(actualArgumentTypes): actualArgumentTypes == null);
	}

	@Override
	public int hashCode() {
        return name.hashCode() + 
                dimensions + (actualArgumentTypes == null ? 0 : actualArgumentTypes.hashCode());
    }
}
