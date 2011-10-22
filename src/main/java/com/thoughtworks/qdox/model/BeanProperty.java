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

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface BeanProperty
{

    /**
     * The name of the bean, which is based on the name of the mutator or accessor, not on the field.
     * 
     * @return the name of the bean
     */
    String getName();

    /**
     * The type of the bean, either used as argument type of the mutator or return type of the accessor.
     * 
     * @return the type of the bean
     */
    JavaType getType();

    /**
     * This can return both the <code>isProperty</code> if the property is of type {@link Boolean} or
     * <code>getProperty</code> for any other type of {@link Object}.
     * 
     * @return the getter, otherwise <code>null</code>
     */
    JavaMethod getAccessor();

    /**
     * The setter-method of the bean.
     * 
     * @return the setter, otherwise <code>null</code>
     */
    JavaMethod getMutator();
}