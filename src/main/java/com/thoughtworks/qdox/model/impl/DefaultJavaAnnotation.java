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

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import com.thoughtworks.qdox.model.expression.ExpressionVisitor;

/**
 * @author Eric Redmond
 */
public class DefaultJavaAnnotation extends AbstractJavaModel
    implements AnnotationValue, Serializable, JavaAnnotation
{
    private final JavaClass type;

    /**
     * Annotation properties as AnnotationValues
     * <p>
     * This map contains the parsed AnnotationValue for each property and allows access to the full parse tree,
     * including typerefs and expressions.
     */
    private final Map<String, AnnotationValue> properties = new LinkedHashMap<String, AnnotationValue>();

    /**
     * Annotation properties as Parameters
     */
    private final Map<String, Object> namedParameters = new LinkedHashMap<String, Object>();

    public DefaultJavaAnnotation( JavaClass type, Map<String, Object> namedParameters )
    {
        this.type = type;
        if ( properties != null )
        {
            for ( Entry<String, AnnotationValue> entry : properties.entrySet() )
            {
                String name = entry.getKey();
                AnnotationValue value = entry.getValue();

                setProperty( name, value );
            }
        }
    }

    public DefaultJavaAnnotation( JavaClass type )
    {
        this( type, null );
    }

    public final void setProperty( String name, AnnotationValue value )
    {
        properties.put( name, value );
        namedParameters.put( name, value.getParameterValue() );
    }

    /** {@inheritDoc} */
    public JavaClass getType()
    {
        return type;
    }

    /** {@inheritDoc} */
    public Object getNamedParameter( String key )
    {
        return namedParameters.get( key );
    }

    /** {@inheritDoc} */
    public Map<String, Object> getNamedParameterMap()
    {
        return namedParameters;
    }

    /** {@inheritDoc} */
    public String getCodeBlock()
    {
        return getModelWriter().writeAnnotation( this ).toString();
    }
    
    /** {@inheritDoc} */
    public Object accept( ExpressionVisitor visitor )
    {
        return visitor.visit( this );
    }

    /** {@inheritDoc} */
    public DefaultJavaAnnotation getParameterValue()
    {
        return this;
    }

    /** {@inheritDoc} */
    public Map<String, AnnotationValue> getPropertyMap()
    {
        return properties;
    }

    /** {@inheritDoc} */
    public AnnotationValue getProperty( String name )
    {
        return properties.get( name );
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append( '@' );
        result.append( type.getFullyQualifiedName() );
        result.append( '(' );
        if ( !namedParameters.isEmpty() )
        {
            for ( Iterator<Entry<String, Object>> i = namedParameters.entrySet().iterator(); i.hasNext(); )
            {
                result.append( i.next() );
                if ( i.hasNext() )
                {
                    result.append( ',' );
                }
            }
        }
        result.append( ')' );
        return result.toString();
    }
}