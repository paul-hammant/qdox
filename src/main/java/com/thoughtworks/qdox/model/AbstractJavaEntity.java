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

import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.qdox.io.IndentBuffer;

public abstract class AbstractJavaEntity extends AbstractBaseJavaEntity implements JavaModel {

    private List<String> modifiers = new LinkedList<String>();
    private JavaClass parentClass;
	private String name;
    /**
     * Return list of modifiers as Strings.
     * (public, private, protected, final, abstract, static)
     */
    public List<String> getModifiers() {
        return modifiers;
    }

    public void setModifiers(List<String> modifiers) {
        this.modifiers = modifiers;
    }

    //helper methods for querying the modifiers
    public boolean isAbstract() {
        return isModifierPresent("abstract");
    }

    public boolean isPublic() {
        return isModifierPresent("public");
    }

    public boolean isPrivate() {
        return isModifierPresent("private");
    }

    public boolean isProtected() {
        return isModifierPresent("protected");
    }

    public boolean isStatic() {
        return isModifierPresent("static");
    }

    public boolean isFinal() {
        return isModifierPresent("final");
    }

    public boolean isSynchronized() {
        return isModifierPresent("synchronized");
    }

    public boolean isTransient() {
        return isModifierPresent("transient");
    }

	/**
	 * @since 1.4
	 */
    public boolean isVolatile() {
        return isModifierPresent("volatile");
    }

	/**
	 * @since 1.4
	 */
    public boolean isNative() {
        return isModifierPresent("native");
    }

	/**
	 * @since 1.4
	 */
    public boolean isStrictfp() {
        return isModifierPresent("strictfp");
    }

    private boolean isModifierPresent(String modifier) {
        return modifiers.contains(modifier);
    }

    public void setParentClass( JavaClass parentClass )
    {
        this.parentClass = parentClass;
    }
    
    public JavaClass getParentClass()
    {
        return parentClass;
    }

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}
}
