package com.thoughtworks.qdox.model.impl;

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

import java.util.Iterator;
import java.util.List;

import com.thoughtworks.qdox.model.JavaGenericDeclaration;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.JavaTypeVariable;
import com.thoughtworks.qdox.type.TypeResolver;

/**
 * Equivalent of {@link java.lang.reflect.TypeVariable}
 * 
 * @author Robert Scholte
 * @since 1.10
 */
public class DefaultJavaTypeVariable<D extends JavaGenericDeclaration>
    extends DefaultJavaType implements JavaTypeVariable<D>
{
    private List<JavaType> bounds;
    
    private D genericDeclaration;

    public DefaultJavaTypeVariable( String name, TypeResolver typeResolver )
    {
        super( name, typeResolver );
    }
    
    /** {@inheritDoc} */
    public List<JavaType> getBounds()
    {
        return bounds;
    }

    /**
     * @param bounds the bounds to set
     */
    public void setBounds( List<JavaType> bounds )
    {
        this.bounds = bounds;
    }
    
    /** {@inheritDoc} */
    public D getGenericDeclaration()
    {
        return genericDeclaration;
    }

    @Override
    public String getFullyQualifiedName()
    {
        return getValue();
    }

    @Override
    public String getGenericFullyQualifiedName()
    {
        StringBuilder result = new StringBuilder();
        result.append( '<' );
        result.append( super.getFullyQualifiedName() );
        if ( bounds != null && !bounds.isEmpty() )
        {
            result.append( " extends " );
            for ( Iterator<JavaType> iter = bounds.iterator(); iter.hasNext(); )
            {
                result.append( iter.next().getGenericFullyQualifiedName() );
                if ( iter.hasNext() )
                {
                    result.append( " & " );
                }
            }
        }
        result.append( '>' );
        return result.toString();
    }
    
    @Override
    public String getCanonicalName()
    {
        return super.getValue();
    }
    
    @Override
    public String getGenericCanonicalName()
    {
        StringBuilder result = new StringBuilder();
        result.append( '<' );
        result.append( super.getGenericCanonicalName() );
        if ( bounds != null && !bounds.isEmpty() )
        {
            result.append( " extends " );
            for ( Iterator<JavaType> iter = bounds.iterator(); iter.hasNext(); )
            {
                result.append( iter.next().getGenericCanonicalName() );
                if ( iter.hasNext() )
                {
                    result.append( " & " );
                }
            }
        }
        result.append( '>' );
        return result.toString();
    }

    @Override
    public String getGenericValue()
    {
        StringBuilder result = new StringBuilder();
        result.append( '<' );
        result.append( getValue() );
        if ( bounds != null && !bounds.isEmpty() )
        {
            result.append( " extends " );
            for ( Iterator<JavaType> iter = bounds.iterator(); iter.hasNext(); )
            {
                result.append( iter.next().getGenericValue() );
                if ( iter.hasNext() )
                {
                    result.append( "," );
                }
            }
        }
        result.append( '>' );
        return result.toString();
    }

    @Override
    public String getName()
    {
        return getValue();
    }
}