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
 * JavaModel representation of a {@link java.lang.reflect.Member} including related methods of {@link java.lang.reflect.Modifier}
 * 
 * @author Robert Scholte
 * @since 2.0
 */
public interface JavaMember
{
    /**
     * Equivalent of {@link java.lang.reflect.Member#getModifiers()}
     * 
     * <strong>This does not follow the java-api</strong>
     * With the Member-class, getModifiers returns an <code>int</code>, which should be decoded with the Modifier.
     * If this member was extracted from a source, it will keep its order. 
     * Otherwise if will be in the preferred order of the java-api.
     * 
     * @return all modifiers is this member
     */
    List<String> getModifiers();
    
    /**
     * Equivalent of {@link java.lang.reflect.Member#getDeclaringClass()}
     * 
     * @return the declaring class
     */
    JavaClass getDeclaringClass();    
    
    /**
     * Equivalent of {@link java.lang.reflect.Member#getName()}
     * 
     * @return the name of this member
     */
    String getName();
    
    /**
     * Equivalent of {@link java.lang.reflect.Modifier#isAbstract(int)}
     * 
     * @return <code>true</code> if this member is <code>abstract</code>, otherwise <code>false</code>
     */
    boolean isAbstract();
    
    /**
     * Equivalent of {@link java.lang.reflect.Modifier#isFinal(int)}
     * 
     * @return <code>true</code> is this member is <code>final</code>, otherwise <code>false</code>
     */
    boolean isFinal();
    
    /**
     * Equivalent of {@link java.lang.reflect.Modifier#isNative(int)}
     * 
     * @return <code>true</code> if this member is <code>native</code>, otherwise <code>false</code>
     */
    boolean isNative();
    
    /**
     * Equivalent of {@link java.lang.reflect.Modifier#isPrivate(int)}
     * 
     * @return <code>true</code> if this member is <code>private</code>, otherwise <code>false</code>
     */
    boolean isPrivate();
    
    /**
     * Equivalent of {@link java.lang.reflect.Modifier#isProtected(int)}
     * 
     * @return <code>true</code> if this member is <code>protected</code>; otherwise <code>false</code>
     */
    boolean isProtected();

    /**
     * Equivalent of {@link java.lang.reflect.Modifier#isPublic(int)}
     * 
     * @return <code>true</code> if this member is <code>public</code>, otherwise <code>false</code>
     */
    boolean isPublic();
    
    /**
     * Equivalent of {@link java.lang.reflect.Modifier#isStatic(int)}
     * 
     * @return <code>true</code> if this member is <code>static</code>, otherwise <code>false</code>
     */
    boolean isStatic();
    
    /**
     * Equivalent of {@link java.lang.reflect.Modifier#isStrict(int)}
     * 
     * @return <code>true</code> if this member is <code>strictfp</code>, otherwise <code>false</code>
     */
    boolean isStrictfp();
    
    /**
     * Equivalent of {@link java.lang.reflect.Modifier#isSynchronized(int)}
     * 
     * @return <code>true</code> if this member is <code>synchronized</code>, otherwise <code>false</code>
     */
    boolean isSynchronized();
    
    /**
     * Equivalent of {@link java.lang.reflect.Modifier#isTransient(int)}
     * 
     * @return <code>true</code> if this member is <code>transient</code>, otherwise <code>false</code>
     */
    boolean isTransient();
    
    /**
     * Equivalent of {@link java.lang.reflect.Modifier#isVolatile(int)}
     * 
     * @return <code>true</code> if this member is <code>volatile</code>, otherwise <code>false</code>
     */
    boolean isVolatile();

}