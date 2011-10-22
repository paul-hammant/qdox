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

import java.util.List;
import java.util.Map;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotatedElement;
import com.thoughtworks.qdox.model.util.TagParser;

public class DefaultDocletTag
    implements DocletTag
{

    private final String name;

    private final String value;

    private final int lineNumber;

    private List<String> parameters;

    private Map<String, String> namedParameters;

    private JavaAnnotatedElement context;

    public DefaultDocletTag( String name, String value, JavaAnnotatedElement context, int lineNumber )
    {
        this.name = name;
        this.value = value;
        this.context = context;
        this.lineNumber = lineNumber;
    }

    public DefaultDocletTag( String name, String value )
    {
        this( name, value, null, 0 );
    }

    /** {@inheritDoc} */
    public String getName()
    {
        return name;
    }

    /** {@inheritDoc} */
    public String getValue()
    {
        return value;
    }

    /** {@inheritDoc} */
    public List<String> getParameters()
    {
        if ( parameters == null )
        {
            parameters = TagParser.parseParameters( value );
        }
        return parameters;
    }

    /** {@inheritDoc} */
    public Map<String, String> getNamedParameterMap()
    {
        if ( namedParameters == null )
        {
            namedParameters = TagParser.parseNamedParameters( value );
        }
        return namedParameters;
    }

    /** {@inheritDoc} */
    public String getNamedParameter( String key )
    {
        return (String) getNamedParameterMap().get( key );
    }

    /** {@inheritDoc} */
    public final JavaAnnotatedElement getContext()
    {
        return context;
    }

    /** {@inheritDoc} */
    public int getLineNumber()
    {
        return lineNumber;
    }
}