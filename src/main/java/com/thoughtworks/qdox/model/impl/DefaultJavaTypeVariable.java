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

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaConstructor;
import com.thoughtworks.qdox.model.JavaGenericDeclaration;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.JavaTypeVariable;
import com.thoughtworks.qdox.model.Type;

/**
 * @author Robert Scholte
 * @since 1.10
 */
public class DefaultJavaTypeVariable<D extends JavaGenericDeclaration>
    extends Type implements JavaTypeVariable<D>
{

    private List<JavaType> bounds;
    
    private D genericDeclaration;

    public DefaultJavaTypeVariable( String fullName, String name, D genericDeclaration )
    {
        super( fullName, name, 0, getContext( genericDeclaration ) );
        this.genericDeclaration = genericDeclaration;
    }

    private static JavaClass getContext( JavaGenericDeclaration genericDeclaration )
    {
        JavaClass result;
        if ( genericDeclaration instanceof JavaClass )
        {
            result = (JavaClass) genericDeclaration;
        }
        else if ( genericDeclaration instanceof JavaMethod )
        {
            result = ( (JavaMethod) genericDeclaration ).getDeclaringClass();
        }
        else if ( genericDeclaration instanceof JavaConstructor )
        {
            result = ( (JavaConstructor) genericDeclaration ).getDeclaringClass();
        }
        else
        {
            throw new IllegalArgumentException( "Unknown GenericDeclatation implementation" );
        }
        return result;
    }

    /**
     * @return the bounds
     */
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
    
    public D getGenericDeclaration()
    {
        return genericDeclaration;
    }

    @Override
    public String getFullyQualifiedName()
    {
        return super.getValue();
    }

    @Override
    public String getGenericFullyQualifiedName()
    {
        StringBuffer result = new StringBuffer( super.getFullyQualifiedName() );
        if ( bounds != null && !bounds.isEmpty() )
        {
            result.append( " extends " );
            for ( Iterator<JavaType> iter = bounds.iterator(); iter.hasNext(); )
            {
                result.append( iter.next().getGenericFullyQualifiedName() );
                if ( iter.hasNext() )
                {
                    result.append( "," );
                }
            }
        }
        return result.toString();
    }

    public String getValue()
    {
        return super.getValue();
    }

    public String getGenericValue()
    {
        StringBuffer result = new StringBuffer( super.getValue() );
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
        return result.toString();
    }

    public String getName()
    {
        return super.getValue();
    }

    public String getResolvedValue()
    {
        return bounds.get( 0 ).getValue();
    }

    public String getResolvedFullyQualifiedName()
    {
        return bounds.get( 0 ).getFullyQualifiedName();
    }
}
