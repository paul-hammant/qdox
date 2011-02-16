package com.thoughtworks.qdox.model;

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

import com.thoughtworks.qdox.parser.expression.AnnotationValue;
import com.thoughtworks.qdox.parser.expression.AnnotationVisitor;

/**
 * 
 * @author Eric Redmond
 */
public class Annotation implements AnnotationValue, Serializable, JavaAnnotation
{
    private final Type type;
    private final int lineNumber;

    /**
     * Annotation properties as AnnotationValues
     * <p>
     * This map contains the parsed AnnotationValue for each property and allows
     * access to the full parse tree, including typerefs and expressions.
     */
    private final Map<String, AnnotationValue> properties = new LinkedHashMap<String, AnnotationValue>();

    /**
     * Annotation properties as Parameters
     */
    private final Map<String, Object> namedParameters = new LinkedHashMap<String, Object>();

    private AbstractJavaModel context;

    public Annotation(Type type,
            AbstractJavaModel context,
            Map<String, Object> namedParameters,
            int lineNumber)
	{
        this.type = type;
        this.context = context;
        this.lineNumber = lineNumber;
        
        if(properties != null) {
            for(Iterator<Entry<String, AnnotationValue>> i = this.properties.entrySet().iterator(); i.hasNext(); ) {
                Entry<String, AnnotationValue> entry = i.next();
                String name = entry.getKey();
                AnnotationValue value = entry.getValue();
                
                setProperty(name, value);
            }
        }
	}

    public Annotation( Type type, int line ) {
        this(type, null, null, line);
    }

    public void setProperty(String name, AnnotationValue value) {
        properties.put( name, value );
        namedParameters.put( name, value.getParameterValue() );
    }

    /* (non-Javadoc)
	 * @see com.thoughtworks.qdox.model.JavaAnnotation#getType()
	 */
    public Type getType() {
    	return type;
    }

    /**
     * @param key name of a named-parameter
     * @return the corresponding value,
     *   or null if no such named-parameter was present
     */
    public Object getNamedParameter(String key) {
    	return namedParameters.get( key );
    }

    /**
     * @return a Map containing all the named-parameters
     */
    public Map<String, Object> getNamedParameterMap() {
    	return namedParameters;
    }

    public final AbstractJavaModel getContext() {
        return context;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotation( this );
    }

    public Object getParameterValue() {
        return this;
    }
    
    /* (non-Javadoc)
	 * @see com.thoughtworks.qdox.model.JavaAnnotation#getPropertyMap()
	 */
    public Map<String, AnnotationValue> getPropertyMap() {
        return properties;
    }
    
    /* (non-Javadoc)
	 * @see com.thoughtworks.qdox.model.JavaAnnotation#getProperty(java.lang.String)
	 */
    public AnnotationValue getProperty(String name) {
        return properties.get( name );
    }

    public void setContext( AbstractJavaModel context ) {
        this.context = context;
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append('@');
        result.append(type.getValue());
        result.append('(');
        if( !namedParameters.isEmpty() ) {
            for(Iterator<Entry<String, Object>> i = namedParameters.entrySet().iterator(); i.hasNext();) result.append( i.next() + ",");
            result.deleteCharAt( result.length()-1 );
        }
        result.append(')');
        return result.toString();
    }
}
