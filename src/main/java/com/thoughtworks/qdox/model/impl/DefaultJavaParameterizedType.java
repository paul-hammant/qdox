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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.qdox.model.JavaParameterizedType;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.type.TypeResolver;

public class DefaultJavaParameterizedType extends DefaultJavaType implements JavaParameterizedType
{
    private List<JavaType> actualArgumentTypes = Collections.emptyList();

    public DefaultJavaParameterizedType( String fullName, String name, int dimensions, TypeResolver typeResolver )
    {
        super( fullName, name, dimensions, typeResolver );
    }


    /** {@inheritDoc} */
    public List<JavaType> getActualTypeArguments()
    {
        return actualArgumentTypes;
    }
    
    public void setActualArgumentTypes( List<JavaType> actualArgumentTypes )
    {
        this.actualArgumentTypes = actualArgumentTypes;
    }
    
    /** {@inheritDoc} */
	@Override
	public String getGenericValue()
    {
        StringBuilder result = new StringBuilder( name );
        if ( !actualArgumentTypes.isEmpty() )
        {
            result.append( "<" );
            for ( Iterator<JavaType> iter = actualArgumentTypes.iterator(); iter.hasNext(); )
            {
                result.append( iter.next().getGenericValue() );
                if ( iter.hasNext() )
                {
                    result.append( "," );
                }
            }
            result.append( ">" );
        }
        for ( int i = 0; i < getDimensions(); i++ )
        {
            result.append( "[]" );
        }
        return result.toString();
    }
    
    /** {@inheritDoc} */
    @Override
	public String getGenericCanonicalName()
    {
        String base = isArray() ? getComponentType().getCanonicalName() : getCanonicalName();
        StringBuilder result = new StringBuilder( base );
        if ( !actualArgumentTypes.isEmpty() )
        {
            result.append( "<" );
            for ( Iterator<JavaType> iter = actualArgumentTypes.iterator(); iter.hasNext(); )
            {
                result.append( iter.next().getGenericCanonicalName() );
                if ( iter.hasNext() )
                {
                    result.append( "," );
                }
            }
            result.append( ">" );
        }
        for ( int i = 0; i < getDimensions(); i++ )
        {
            result.append( "[]" );
        }
        return result.toString();
    }

    /** {@inheritDoc} */
    @Override
	public String getGenericFullyQualifiedName()
    {
        StringBuilder result = new StringBuilder( isResolved() ? fullName : name );
        if ( !actualArgumentTypes.isEmpty() )
        {
            result.append( "<" );
            for ( Iterator<JavaType> iter = actualArgumentTypes.iterator(); iter.hasNext(); )
            {
                result.append( iter.next().getGenericFullyQualifiedName() );
                if ( iter.hasNext() )
                {
                    result.append( "," );
                }
            }
            result.append( ">" );
        }
        for ( int i = 0; i < getDimensions(); i++ )
        {
            result.append( "[]" );
        }
        return result.toString();   
    }
}