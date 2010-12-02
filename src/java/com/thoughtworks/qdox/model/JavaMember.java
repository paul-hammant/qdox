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

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * JavaModel representation of a {@link Member} including related methods of {@ Modifier}
 * 
 * @author Robert Scholte
 * @since 2.0
 */
public interface JavaMember
{
    /**
     * Equivalent of {@link Member#getModifiers()}
     * 
     * <strong>This does not follow the java-api</strong>
     * With the {@link Member}-class, getModifiers returns an <code>int</code>, which should be decoded with the {@link Modifier}
     * If this member was extracted from a source, it will keep its order. 
     * Otherwise if will be in the preferred order of the java-api.
     * 
     * @return all modifiers is this member
     */
    public List<String> getModifiers();
    
    
    /**
     * Equivalent of {@link Member#getName()}
     * 
     * @return the name of this member
     */
    public String getName();
    
    /**
     * Equivalent of {@link Modifier#isAbstract(int)}
     * 
     * @return <tt>true</tt> if this member is <tt>abstract</tt>; otherwise <tt>false</tt>
     */
    public boolean isAbstract();
    
    /**
     * Equivalent of {@link Modifier#isFinal(int)}
     * 
     * @return <tt>true</tt> is this member is <tt>final</tt>; otherwise <tt>false</tt>
     */
    public boolean isFinal();
    
    /**
     * Equivalent of {@link Modifier#isNative(int)}
     * 
     * @return <tt>true</tt> if this member is <tt>native</tt>; otherwise <tt>false</tt>
     */
    public boolean isNative();
    
    /**
     * Equivalent of {@link Modifier#isPrivate(int)}
     * 
     * @return <tt>true</tt> if this member is <tt>private</tt>; otherwise <tt>false</tt>
     */
    public boolean isPrivate();
    
    /**
     * Equivalent of {@link Modifier#isProtected(int)}
     * 
     * @return <tt>true</tt> if this member is <tt>protected</tt>; otherwise <tt>false</tt>
     */
    public boolean isProtected();

    /**
     * Equivalent of {@link Modifier#isPublic(int)}
     * 
     * @return <tt>true</tt> if this member is <tt>public</tt>; otherwise <tt>false</tt>
     */
    public boolean isPublic();
    
    /**
     * Equivalent of {@link Modifier#isStatic(int)}
     * 
     * @return <tt>true</tt> if this member is <tt>static</tt>; otherwise <tt>false</tt>
     */
    public boolean isStatic();
    
    /**
     * Equivalent of {@link Modifier#isStrict(int)}
     * 
     * @return <tt>true</tt> if this member is <tt>strictfp</tt>; otherwise <tt>false</tt>
     */
    public boolean isStrictfp();
    
    /**
     * Equivalent of {@link Modifier#isSynchronized(int)}
     * 
     * @return <tt>true</tt> if this member is <tt>synchronized</tt>; otherwise <tt>false</tt>
     */
    public boolean isSynchronized();
    
    /**
     * Equivalent of {@link Modifier#isTransient(int)}
     * 
     * @return <tt>true</tt> if this member is <tt>transient</tt>; otherwise <tt>false</tt>
     */
    public boolean isTransient();
    
    /**
     * Equivalent of {@link Modifier#isVolatile(int)}
     * 
     * @return <tt>true</tt> if this member is <tt>volatile</tt>; otherwise <tt>false</tt>
     */
    public boolean isVolatile();

}
