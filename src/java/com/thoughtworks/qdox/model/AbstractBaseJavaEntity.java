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
import java.util.Collections;
import java.util.List;

public class AbstractBaseJavaEntity implements Serializable {

	private String name;
	private List<Annotation> annotations = Collections.emptyList();
	private int lineNumber = -1;

	public AbstractBaseJavaEntity() {
		super();
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getName() {
	    return name;
	}

	public List<Annotation> getAnnotations() {
	    return annotations;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public void setAnnotations(List<Annotation> annotations) {
	    this.annotations = annotations;
	}

	public void setLineNumber(int lineNumber) {
	    this.lineNumber = lineNumber;
	}

	/**
	 * Not every entity has a parentClass, but AnnotationFieldRef requires access to it.
	 * When used with JavaClass, don't confuse this with getSuperClass()
	 * 
	 * @return the surrounding class
	 */
	public JavaClass getParentClass() { return null; }

}