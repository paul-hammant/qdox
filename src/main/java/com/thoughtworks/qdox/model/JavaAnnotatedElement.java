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

import java.util.List;

/**
 * <p>
 * Equivalent of {@link java.lang.reflect.AnnotatedElement}, providing the most important methods.
 * Where the original AnnotatedElement uses an Array, the JavaAnnotatedElement is using a {@link List}.
 * </p>
 * <p>
 * Where you can use Annotations, you can also use JavaDoc. For that reason all JavaDoc methods have been added to this interface.
 * </p>
 * 
 * @author Robert Scholte
 * @since 2.0
 *
 */
public interface JavaAnnotatedElement extends JavaModel
{
    // Methods from AnnotatedElement
    
	/**
	 * 
	 * Equivalent of {@link java.lang.reflect.AnnotatedElement#getAnnotations()}
	 * 
	 * @return a list of Annotations, never <code>null</code>
	 */
    List<JavaAnnotation> getAnnotations();
    
    // JavaDoc specific methods
    
    /**
     * Retrieve the javadoc comment of this annotated element.
     * This is the part between &#47;&#42;&#42; and the &#42;&#47;, but without the doclet tags
     * 
     * @return the comment, otherwise <code>null</code>
     */
    String getComment();
    
    /**
     * Retrieve all defined doclet tags.
     * 
     * @return a list of DocletTags, never <code>null</code>
     */
	List<DocletTag> getTags();

	/**
	 * Retrieve all doclettags with a specific name.
	 * 
	 * @param name the name of the doclet tag
	 * @return a list of doclettags, never <code>null</code>
	 */
    List<DocletTag> getTagsByName( String name );

    /**
     * Retrieve the doclettag by the specified name.
     * If there are more than one tags, only return the first one.
     * 
     * @param name the name of the doclettag trying to retrieve
     * @return the first doclettag matching the name, otherwise <code>null</code>
     */
    DocletTag getTagByName( String name );

    /**
     * Convenience method for <code>getTagByName(String).getNamedParameter(String)</code>
     * that also checks for null tag.
     * 
     * @param tagName the tag name
     * @param parameterName the parameter name
     * @return the value of the matching parameter, otherwise <code>null</code>
     * @since 1.3
     */
    String getNamedParameter(String tagName, String parameterName);

}
