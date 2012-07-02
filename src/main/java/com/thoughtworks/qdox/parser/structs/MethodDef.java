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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MethodDef extends LocatedDef {

    private String name = "";
    private List<TypeVariableDef> typeParams;
    private TypeDef returnType;
    private Set<String> modifiers = new LinkedHashSet<String>();
    private Set<TypeDef> exceptions = new LinkedHashSet<TypeDef>();
    private boolean constructor = false;
    private int dimensions;
    private String body;
    
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append( getModifiers() );
        result.append( ' ' );
        result.append( ( getReturnType() != null ? getReturnType().toString() : "" ) );
        for ( int i = 0; i < getDimensions(); i++ )
        {
            result.append( "[]" );
        }
        result.append( ' ' );
        result.append( getName() );
        result.append( '(' );
        result.append( getTypeParams() );
        result.append( ')' );
        result.append( " throws " );
        result.append( getExceptions() );
        result.append( getBody() );
        return result.toString();
    }

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setReturnType(TypeDef returnType) {
		this.returnType = returnType;
	}

	public TypeDef getReturnType() {
		return returnType;
	}

	public void setModifiers(Set<String> modifiers) {
		this.modifiers = modifiers;
	}

	public Set<String> getModifiers() {
		return modifiers;
	}

	public void setConstructor(boolean constructor) {
		this.constructor = constructor;
	}

	public boolean isConstructor() {
		return constructor;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getBody() {
		return body;
	}

	public void setDimensions(int dimensions) {
		this.dimensions = dimensions;
	}

	public int getDimensions() {
		return dimensions;
	}

	public void setExceptions(Set<TypeDef> exceptions) {
		this.exceptions = exceptions;
	}

	public Set<TypeDef> getExceptions() {
		return exceptions;
	}

    public void setTypeParams( List<TypeVariableDef> typeParams )
    {
        this.typeParams = typeParams;
    }

    public List<TypeVariableDef> getTypeParams()
    {
        return typeParams;
    }
}
