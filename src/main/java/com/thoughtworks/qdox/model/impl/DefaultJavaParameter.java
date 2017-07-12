package com.thoughtworks.qdox.model.impl;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.JavaExecutable;

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


public class DefaultJavaParameter extends AbstractBaseJavaEntity implements JavaParameter 
{

    private String name;
    private JavaClass type;
    private JavaExecutable executable;
    private boolean varArgs;

    public DefaultJavaParameter( JavaClass type, String name )
    {
        this( type, name, false );
    }

    public DefaultJavaParameter( JavaClass type, String name, boolean varArgs )
    {
        this.name = name;
        this.type = type;
        this.varArgs = varArgs;
    }

    /** {@inheritDoc} */
    public String getCodeBlock()
    {
        return getModelWriter().writeParameter( this ).toString();
    }

    public void setName( String name )
    {
        this.name = name;
    }

	/** {@inheritDoc} */
    public String getName()
    {
        return name;
    }

    /** {@inheritDoc} */
    public JavaType getType()
    {
        return type;
    }

    /** {@inheritDoc} */
    public JavaClass getJavaClass()
    {
        return type;
    }

    /** {@inheritDoc} */
    public JavaExecutable getExecutable()
    {
        return executable;
    }

    public void setExecutable( JavaExecutable executable )
    {
        this.executable = executable;
    }
    
    /** {@inheritDoc} */
    @Override
	public JavaClass getDeclaringClass()
    {
        return getExecutable().getDeclaringClass();
    }

    /** {@inheritDoc} */
    public boolean isVarArgs() {
        return varArgs;
    }
    
    /** {@inheritDoc} */
    public String getBinaryName()
    {
        return type.getBinaryName();
    }

    /** {@inheritDoc} */
    public String getFullyQualifiedName()
    {
        return type.getFullyQualifiedName();
    }
    
    /** {@inheritDoc} */
    public String getCanonicalName()
    {
        return type.getCanonicalName();
    }
    
    /** {@inheritDoc} */
    public String getValue()
    {
        return type.getValue();
    }
    
    /** {@inheritDoc} */
    public String getGenericCanonicalName()
    {
        return type.getGenericCanonicalName();
    }
    
    /** {@inheritDoc} */
    public String getGenericFullyQualifiedName()
    {
        return type.getGenericFullyQualifiedName();
    }
    
    /** {@inheritDoc} */
    public String getResolvedValue()
    {
        return DefaultJavaType.getResolvedValue( type, getExecutable().getTypeParameters() );
    }

    /** {@inheritDoc} */
    public String getResolvedFullyQualifiedName() 
    {
        return DefaultJavaType.getResolvedFullyQualifiedName( type, getExecutable().getTypeParameters() );
    }
    
    /** {@inheritDoc} */
	public String getResolvedGenericValue() 
	{
		return DefaultJavaType.getResolvedGenericValue( type, getExecutable().getTypeParameters() );
	}
	
    /** {@inheritDoc} */
	public String getResolvedGenericFullyQualifiedName()
	{
	    return DefaultJavaType.getResolvedGenericFullyQualifiedName( type, getExecutable().getTypeParameters() );
	}

    @Override
    public int hashCode()
    {
        return 13 + ( isVarArgs() ? 1 : 0 ) + getType().hashCode();
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( !( obj instanceof JavaParameter ) )
        {
            return false;
        }
        JavaParameter p = (JavaParameter) obj;
        // name should not be used in equality check.
        return getType().equals( p.getType() ) && isVarArgs() == p.isVarArgs();
    }
	
    @Override
    public String toString()
    {
        return getResolvedValue() + " "+ name;
    }

    /** {@inheritDoc} */
    public String getGenericValue()
    {
        return type.getGenericValue();
    }

    /** {@inheritDoc} */
    public String toGenericString()
    {
        return type.toGenericString();
    }
}