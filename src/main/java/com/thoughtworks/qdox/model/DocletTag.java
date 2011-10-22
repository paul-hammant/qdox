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
import java.util.List;
import java.util.Map;

/**
 * @author Joe Walnes
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface DocletTag
    extends Serializable
{

    /**
     * The name of the doclet tag.
     * 
     * @return the tag name
     */
    String getName();

    /**
     * The full value of the doclet tag. 
     * 
     * @return the full tag-value
     */
    String getValue();

    /**
     * 
     * 
     * @return a list of whitespace-separated tag parameters
     */
    List<String> getParameters();

    /**
     * @param key name of a named-parameter
     * @return the matching value, otherwise <code>null</code>
     */
    String getNamedParameter( String key );

    /**
     * @return a Map containing all the named-parameters, never <code>null</code>
     */
    Map<String, String> getNamedParameterMap();

    /**
     * The line number of the source where this tag occurred.
     * 
     * @return the line number where the tag occurred, otherwise <code>-1</code>
     */
    int getLineNumber();

    /**
     * The element to which this tag applies.
     * 
     * @return the annotated element, should never be <code>null</code>
     * @since 1.4
     */
    JavaAnnotatedElement getContext();

}