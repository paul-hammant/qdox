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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;

public abstract class AbstractBaseJavaEntity extends AbstractJavaModel implements Serializable {

    private JavaSource source;
    
	private List<JavaAnnotation> annotations = Collections.emptyList();
	private String comment;
	private List<DocletTag> tags = Collections.emptyList();

    public AbstractBaseJavaEntity()
    {
        super();
    }

    public JavaSource getSource() {
        return source;
    }
    
    public void setSource(JavaSource source) {
        this.source = source;
    }
    
    public List<JavaAnnotation> getAnnotations()
    {
        return annotations;
    }

    public void setAnnotations( List<JavaAnnotation> annotations )
    {
        this.annotations = annotations;
    }

	/**
	 * Not every entity has a parentClass, but AnnotationFieldRef requires access to it.
	 * When used with JavaClass, don't confuse this with getSuperClass()
	 * 
	 * @return the surrounding class
	 */
    public JavaClass getDeclaringClass()
    {
        return null;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment( String comment )
    {
        this.comment = comment;
    }

    public List<DocletTag> getTags()
    {
        return tags;
    }

    public List<DocletTag> getTagsByName( String name )
    {
        List<DocletTag> specifiedTags = new LinkedList<DocletTag>();
        for ( DocletTag docletTag : tags )
        {
            if ( docletTag.getName().equals( name ) )
            {
                specifiedTags.add( docletTag );
            }
        }
        return specifiedTags;
    }

    public DocletTag getTagByName( String name )
    {
        for ( DocletTag docletTag : tags )
        {
            if ( docletTag.getName().equals( name ) )
            {
                return docletTag;
            }
        }
        return null;
    }

    /**
     * Convenience method for <code>getTagByName(String).getNamedParameter(String)</code> that also checks for null tag.
     * 
     * @param tagName the name of the docletTag
     * @param parameterName the name of the parameter
     * @return the value of the named parameter 
     * @since 1.3
     */
    public String getNamedParameter( String tagName, String parameterName )
    {
        DocletTag tag = getTagByName( tagName );
        return ( tag != null ? tag.getNamedParameter( parameterName ) : null );
    }

    public void setTags( List<DocletTag> tagList )
    {
        this.tags = tagList;
    }
}