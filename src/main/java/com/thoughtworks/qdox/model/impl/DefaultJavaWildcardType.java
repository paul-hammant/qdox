package com.thoughtworks.qdox.model.impl;

import java.util.Collections;
import java.util.List;

import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.JavaWildcardType;

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

/**
 * Equivalent of {@link java.lang.reflect.WildcardType}
 * This class supports both the 'super' and 'extends' wildcards. For &lt;?&gt; you must use the normal Type, because ?
 * itself can't be generic
 * 
 * @author Robert Scholte
 */
public class DefaultJavaWildcardType extends DefaultJavaType
    implements JavaWildcardType
{
    public static enum BoundType { EXTENDS, SUPER }
    
    private BoundType boundType;
    
    private List<JavaType> bounds;

    public DefaultJavaWildcardType()
    {
        super( "?" );
        bounds = Collections.emptyList();
    }
    
    public DefaultJavaWildcardType( JavaType type, BoundType boundType )
    {
        this();
        bounds = Collections.singletonList( type );
        this.boundType = boundType;
    }

    @Override
    public String getFullyQualifiedName()
    {
        StringBuilder builder = getPreparedStringBuilder();
        for( JavaType type : bounds )
        {
            builder.append( type.getFullyQualifiedName() );
        }
        return builder.toString();
    }

    @Override
    public String getGenericValue()
    {
        StringBuilder builder = getPreparedStringBuilder();
        for( JavaType type : bounds )
        {
            builder.append( type.getGenericValue() );
        }
        return builder.toString();
    }

    @Override
    public String getGenericFullyQualifiedName()
    {
        StringBuilder builder = getPreparedStringBuilder();
        for( JavaType type : bounds )
        {
            builder.append( type.getGenericFullyQualifiedName() );
        }
        return builder.toString();
    }
    
    @Override
    public String getCanonicalName()
    {
        StringBuilder builder = getPreparedStringBuilder();
        for( JavaType type : bounds )
        {
            builder.append( type.getCanonicalName() );
        }
        return builder.toString();
    }

    @Override
    public String getGenericCanonicalName()
    {
        StringBuilder builder = getPreparedStringBuilder();
        for( JavaType type : bounds )
        {
            builder.append( type.getGenericCanonicalName() );
        }
        return builder.toString();
    }

    @Override
    public String getValue()
    {
        StringBuilder builder = getPreparedStringBuilder();
        for( JavaType type : bounds )
        {
            builder.append( type.getValue() );
        }
        return builder.toString();

    }

    @Override
    public String toGenericString()
    {
        StringBuilder builder = getPreparedStringBuilder();
        for( JavaType type : bounds )
        {
            builder.append( type.toGenericString() );
        }
        return builder.toString();
    }
    
    private StringBuilder getPreparedStringBuilder()
    {
        StringBuilder builder = new StringBuilder( "?" );
        if( BoundType.EXTENDS.equals( boundType ) )
        {
            builder.append( " extends " );
        }
        else if( BoundType.SUPER.equals( boundType ) )
        {
            builder.append( " super " );
        }
        return builder;
    }
}