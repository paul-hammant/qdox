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

/**
 * Used for both fields and parameters
 * 
 *
 */
public class FieldDef extends LocatedDef {
    
    private String name = "";
    private TypeDef type;
    private Set<String> modifiers = new HashSet<String>();
    private int dimensions;
    private boolean isVarArgs;
    private String body = "";
    
    public FieldDef()
    {
    }
    
    public FieldDef( String name )
    {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        FieldDef paramDef = (FieldDef) obj;
        boolean result = paramDef.getName().equals(getName())
                && paramDef.getModifiers().equals(getModifiers())
                && paramDef.isVarArgs() == isVarArgs();
        if(paramDef.getType() == null) {
        	result &= (getType() == null)
        		&& paramDef.getDimensions() == getDimensions();
        }
        else {
        	result &= (getType() != null)
        		&&(paramDef.getType().name.equals(getType().name))
        		&&(paramDef.getType().actualArgumentTypes == null ? getType().actualArgumentTypes == null: paramDef.getType().actualArgumentTypes.equals(getType().actualArgumentTypes))
        		&&(paramDef.getType().dimensions + paramDef.getDimensions() == getDimensions() + getType().dimensions);
        }
        return result;
    }

    @Override
    public int hashCode() {
        return getName().hashCode() + (getType() != null ? getType().hashCode() : 0) +
                getDimensions() + getModifiers().hashCode() + (isVarArgs() ? 79769989 : 0);
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(getModifiers());
        result.append(' ');
        result.append(getType());
        for (int i = 0; i < getDimensions(); i++) result.append("[]");
        result.append(' ');
        result.append(getName());
        if(getBody().length() > 0){
            result.append(" = ").append(getBody());
        }
        return result.toString();
    }

    /**
     * @param name the name to set
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param type the type to set
     */
    public void setType( TypeDef type )
    {
        this.type = type;
    }

    /**
     * @return the type
     */
    public TypeDef getType()
    {
        return type;
    }

    /**
     * @param dimensions the dimensions to set
     */
    public void setDimensions( int dimensions )
    {
        this.dimensions = dimensions;
    }

    /**
     * @return the dimensions
     */
    public int getDimensions()
    {
        return dimensions;
    }

    /**
     * @param isVarArgs the isVarArgs to set
     */
    public void setVarArgs( boolean isVarArgs )
    {
        this.isVarArgs = isVarArgs;
    }

    /**
     * @return the isVarArgs
     */
    public boolean isVarArgs()
    {
        return isVarArgs;
    }

    /**
     * @param body the body to set
     */
    public void setBody( String body )
    {
        this.body = body;
    }

    /**
     * @return the body
     */
    public String getBody()
    {
        return body;
    }

    /**
     * @param modifiers the modifiers to set
     */
    public void setModifiers( Set<String> modifiers )
    {
        this.modifiers = modifiers;
    }

    /**
     * @return the modifiers
     */
    public Set<String> getModifiers()
    {
        return modifiers;
    }
}
