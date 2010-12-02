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

import com.thoughtworks.qdox.io.IndentBuffer;

public class DefaultJavaField extends AbstractJavaEntity implements JavaField {

    private Type type;
    private String initializationExpression;
    	
    public DefaultJavaField() {
    }

    public DefaultJavaField(String name) {
        setName(name);
    }

    public DefaultJavaField(Type type, String name) {
        setType(type);
        setName(name);
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaField#getType()
     */
    public Type getType() {
        return type;
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaField#getCodeBlock()
     */
    public String getCodeBlock()
    {
        return getSource().getModelWriter().writeField( this ).toString();
    }

    public void setType(Type type) {
        this.type = type;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaField#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        return getName().compareTo(((JavaField)o).getName());
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaField#getDeclarationSignature(boolean)
     */
    public String getDeclarationSignature(boolean withModifiers) {
        IndentBuffer result = new IndentBuffer();
        if (withModifiers) {
            writeAllModifiers(result);
        }
        result.write(type.toString());
        result.write(' ');
        result.write(name);
        return result.toString();
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaField#getCallSignature()
     */
    public String getCallSignature() {
        return getName();
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaField#getInitializationExpression()
     */
    public String getInitializationExpression(){
    	return initializationExpression;
    }
    
    public void setInitializationExpression(String initializationExpression){
    	this.initializationExpression = initializationExpression;
    }

    /**
     * @see http://java.sun.com/j2se/1.5.0/docs/api/java/lang/Field.html#toString()
     */
    public String toString() {
    	StringBuffer result = new StringBuffer();
    	if(isPrivate()) {
    		result.append("private ");
    	}
    	else if(isProtected()) {
    		result.append("protected ");
    	}
    	else if(isPublic()) {
    		result.append("public ");
    	}
    	if(isStatic()) {
    		result.append("static ");
    	}
    	if(isFinal()) {
    		result.append("final ");
    	}
    	if(isTransient()) {
    		result.append("transient ");
    	}
    	if(isVolatile()) {
    		result.append("volatile ");
    	}
    	result.append(getType().getValue() + " ");
    	result.append(getParentClass().getFullyQualifiedName() + "." +getName());
    	return result.toString();
    }
}
