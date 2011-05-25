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

import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractJavaEntity extends AbstractBaseJavaEntity implements JavaModel {

    private List<String> modifiers = new LinkedList<String>();
    private JavaClass parentCls;
	private String name;

	/**
     * Return list of modifiers as Strings.
     * (public, private, protected, final, abstract, static)
	 * @return a list of modifiers, never <code>null</code>
	 */
    public List<String> getModifiers() {
        return modifiers;
    }

    public void setModifiers(List<String> modifiers) {
        this.modifiers = modifiers;
    }

    /**
     * Equivalent of {@link Modifier#isAbstract(int)}
     * 
     * @return <code>true</code> if entity is abstract, otherwise <code>false</code>
     */
    public boolean isAbstract() {
        return isModifierPresent("abstract");
    }

    /**
     * Equivalent of {@link Modifier#isPublic(int)}
     * 
     * @return <code>true</code> if entity is public, otherwise <code>false</code>
     */
    public boolean isPublic() {
        return isModifierPresent("public");
    }

    /**
     * Equivalent of {@link Modifier#isPrivate(int)}
     * 
     * @return <code>true</code> if entity is private, otherwise <code>false</code>
     */
    public boolean isPrivate() {
        return isModifierPresent("private");
    }

    /**
     * Equivalent of {@link Modifier#isProtected(int)}
     * 
     * @return <code>true</code> if entity is protected, otherwise <code>false</code>
     */
    public boolean isProtected() {
        return isModifierPresent("protected");
    }

    /**
     * Equivalent of {@link Modifier#isStatic(int)}
     * 
     * @return <code>true</code> if entity is static, otherwise <code>false</code>
     */
    public boolean isStatic() {
        return isModifierPresent("static");
    }

    /**
     * Equivalent of {@link Modifier#isFinal(int)}
     * 
     * @return <code>true</code> if entity is final, otherwise <code>false</code>
     */
    public boolean isFinal() {
        return isModifierPresent("final");
    }

    /**
     * Equivalent of {@link Modifier#isSynchronized(int)}
     * 
     * @return <code>true</code> if entity is sunchronized, otherwise <code>false</code>
     */
    public boolean isSynchronized() {
        return isModifierPresent("synchronized");
    }

    /**
     * Equivalent of {@link Modifier#isTransient(int)}
     * 
     * @return <code>true</code> if entity is transient, otherwise <code>false</code>
     */
    public boolean isTransient() {
        return isModifierPresent("transient");
    }

    /**
     * Equivalent of {@link Modifier#isVolatile(int)}
     * 
     * @return <code>true</code> if entity is volatile, otherwise <code>false</code>
     * @since 1.4
     */
    public boolean isVolatile() {
        return isModifierPresent("volatile");
    }

    /**
     * Equivalent of {@link Modifier#isNative(int)}
     * 
     * @return <code>true</code> if entity is native, otherwise <code>false</code>
     * @since 1.4
     */
    public boolean isNative() {
        return isModifierPresent("native");
    }

    /**
     * Equivalent of {@link Modifier#isStrict(int)}
     * 
     * @return <code>true</code> if entity is strictfp, otherwise <code>false</code>
     * @since 1.4
     */
    public boolean isStrictfp() {
        return isModifierPresent("strictfp");
    }

    /**
     * Returns <code>true</code> if one of the modifiers matches the modifier
     * 
     * @param modifier the modifier
     * @return <code>true</code> if the modifier is present, otherwise <code>false</code>
     */
    private boolean isModifierPresent(String modifier) {
        return modifiers.contains(modifier);
    }

    public void setParentClass( JavaClass parentClass )
    {
        this.parentCls = parentClass;
    }
    
    public JavaClass getParentClass()
    {
        return parentCls;
    }

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}
}
