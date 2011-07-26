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

    private String name;
    private int dimensions;
    private List<TypeDef> actualArgumentTypes; 

    public TypeDef(String name, int dimensions) {
        this.name = name;
        this.dimensions = dimensions;
    }

	public TypeDef(String name) {
		this(name, 0);
	}
	
	public String getName()
    {
        return name;
    }
	
	public int getDimensions()
    {
        return dimensions;
    }
	
	public void setDimensions( int dimensions )
    {
        this.dimensions = dimensions;
    }
	
	@Override
	public boolean equals(Object obj) {
		TypeDef typeDef = (TypeDef) obj;
        return typeDef.name.equals(name)
                && typeDef.dimensions == dimensions
                && (typeDef.getActualArgumentTypes() != null ? typeDef.getActualArgumentTypes().equals(getActualArgumentTypes()): getActualArgumentTypes() == null);
	}

	@Override
	public int hashCode() {
        return name.hashCode() + 
                dimensions + (getActualArgumentTypes() == null ? 0 : getActualArgumentTypes().hashCode());
    }

    /**
     * @param actualArgumentTypes the actualArgumentTypes to set
     */
    public void setActualArgumentTypes( List<TypeDef> actualArgumentTypes )
    {
        this.actualArgumentTypes = actualArgumentTypes;
    }

    /**
     * @return the actualArgumentTypes
     */
    public List<TypeDef> getActualArgumentTypes()
    {
        return actualArgumentTypes;
    }
}
