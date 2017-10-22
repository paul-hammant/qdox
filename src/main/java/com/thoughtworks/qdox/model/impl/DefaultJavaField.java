package com.thoughtworks.qdox.model.impl;

import java.util.List;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.expression.Expression;

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

public class DefaultJavaField
    extends AbstractJavaEntity
    implements JavaField
{
    private JavaClass type;

    private String initializationExpression;

    private boolean enumConstant;

    private JavaClass enumConstantClass;
    
    private List<Expression> enumConstantArguments;

    public DefaultJavaField( String name )
    {
        setName( name );
    }

    public DefaultJavaField( JavaClass type, String name )
    {
        this( name );
        this.type = type;
    }

    /** {@inheritDoc} */
    public JavaClass getType()
    {
        return type;
    }

    /** {@inheritDoc} */
    public String getCodeBlock()
    {
        return getModelWriter().writeField( this ).toString();
    }

    public void setType( JavaClass type )
    {
        this.type = type;
    }

    /** {@inheritDoc} */
    public String getInitializationExpression()
    {
        return initializationExpression;
    }

    public void setInitializationExpression( String initializationExpression )
    {
        this.initializationExpression = initializationExpression;
    }

    /** {@inheritDoc} */
    public boolean isEnumConstant()
    {
        return enumConstant;
    }

    public void setEnumConstant( boolean enumConstant )
    {
        this.enumConstant = enumConstant;
    }

    /** {@inheritDoc} */
    public List<Expression> getEnumConstantArguments()
    {
        return enumConstantArguments;
    }
    
    public void setEnumConstantArguments( List<Expression> enumConstantArguments )
    {
        this.enumConstantArguments = enumConstantArguments;
    }
    
    /** {@inheritDoc} */
    public JavaClass getEnumConstantClass()
    {
        return enumConstantClass;
    }
    
    public void setEnumConstantClass( JavaClass enumConstantClass )
    {
        this.enumConstantClass = enumConstantClass;
    }

    /**
     * @see java.lang.reflect.Field#toString()
     */
    @Override
	public String toString()
    {
        StringBuilder result = new StringBuilder();
        if ( isPrivate() )
        {
            result.append( "private " );
        }
        else if ( isProtected() )
        {
            result.append( "protected " );
        }
        else if ( isPublic() )
        {
            result.append( "public " );
        }
        
        if ( isStatic() )
        {
            result.append( "static " );
        }
        if ( isFinal() )
        {
            result.append( "final " );
        }
        if ( isTransient() )
        {
            result.append( "transient " );
        }
        if ( isVolatile() )
        {
            result.append( "volatile " );
        }
        result.append( type.getValue() ).append( ' ' );
        result.append( getDeclaringClass().getFullyQualifiedName() ).append( '.' ).append( getName() );
        return result.toString();
    }

    /**
     * Compares this Field against the specified object. Returns <code>true</code> if the objects are the same. Two
     * Field objects are the same if they were declared by the same class and have the same name and type.
     */
    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( !( obj instanceof JavaField ) )
        {
            return false;
        }
        JavaField fld = (JavaField) obj;
        if ( !fld.getDeclaringClass().equals( this.getDeclaringClass() ) )
        {
            return false;
        }
        if ( !fld.isEnumConstant() == this.isEnumConstant() )
        {
            return false;
        }
        // Don't see any reason to compare the Type. Otherwise it's already invalid
        return fld.getName().equals( this.getName() );
    }

    @Override
    public int hashCode()
    {
        int hashCode = 5;
        if ( getDeclaringClass() != null )
        {
            hashCode *= 31 + getDeclaringClass().hashCode();
        }
        if ( getName() != null )
        {
            hashCode *= 37 + getName().hashCode();
        }
        return hashCode;
    }
}