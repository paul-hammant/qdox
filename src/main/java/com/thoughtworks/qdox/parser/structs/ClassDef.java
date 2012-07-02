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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ClassDef extends LocatedDef {
    
    public static final String CLASS = "class";
    public static final String INTERFACE = "interface";
    public static final String ENUM = "enum";
    public static final String ANNOTATION_TYPE = "@interface";
    
    private String name = "";
    private Set<String> modifiers = new LinkedHashSet<String>();
    private List<TypeVariableDef> typeParams = new LinkedList<TypeVariableDef>();
    private Set<TypeDef> extendz = new LinkedHashSet<TypeDef>();
    private Set<TypeDef> implementz = new LinkedHashSet<TypeDef>();
    private String type = CLASS;
    
    public ClassDef()
    {
    }

    public ClassDef( String name )
    {
        this.name = name;
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

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append( getModifiers() );
        result.append( ' ' );
        result.append( getType() );
        result.append( ' ' );
        result.append( getName() );
        // typeParams
        result.append( " extends " );
        result.append( getExtends() );
        result.append( " implements " );
        result.append( getImplements() );
        return result.toString();
    }

    public void setModifiers( Set<String> modifiers )
    {
        this.modifiers = modifiers;
    }

    public Set<String> getModifiers()
    {
        return modifiers;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    public void setImplements( Set<TypeDef> implementz )
    {
        this.implementz = implementz;
    }

    public Set<TypeDef> getImplements()
    {
        return implementz;
    }

    public void setExtends( Set<TypeDef> extendz )
    {
        this.extendz = extendz;
    }

    public Set<TypeDef> getExtends()
    {
        return extendz;
    }

    public void setTypeParameters( List<TypeVariableDef> typeParams )
    {
        this.typeParams = typeParams;
    }

    public List<TypeVariableDef> getTypeParameters()
    {
        return typeParams;
    }

}
