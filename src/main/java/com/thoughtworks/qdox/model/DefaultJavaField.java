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

public class DefaultJavaField extends AbstractJavaEntity implements JavaField {

    private Type type;
    private String initializationExpression;
    	
    public DefaultJavaField() {
    }

    public DefaultJavaField(String name) {
        setName(name);
    }

    public DefaultJavaField(Type type, String name) {
        this( name );
        this.type = type;
    }
    
    /**
     * Retrieve the class containing this field
     * 
     * @return the declaring class
     */
    public JavaClass getDeclaringClass() {
    	return getParentClass();
    }
    
    /**
     * Retrieve the Type of this field
     * 
     * @return the Type of this field
     */
    public Type getType() {
        return type;
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaField#getCodeBlock()
     */
    public String getCodeBlock()
    {
        return getModelWriter().writeField( this ).toString();
    }

    public void setType(Type type) {
        this.type = type;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaField#getDeclarationSignature(boolean)
     */
    public String getDeclarationSignature(boolean withModifiers) {
        StringBuffer result = new StringBuffer();
        if (withModifiers) {
            for (String modifier  : getModifiers()) {
                result.append(modifier);
                result.append(' ');
            }
        }
        result.append(type.toString());
        result.append(' ');
        result.append(getName());
        return result.toString();
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaField#getCallSignature()
     */
    public String getCallSignature() {
        return getName();
    }

    /**
     * This value represents an assignment to the name.
     * It can hold a primitive, Object of field reference of any class.
     * 
     * @return the expression, otherwise <code>null</code>
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
    	result.append( type.getValue() + " ");
    	result.append( getDeclaringClass().getFullyQualifiedName() + "." +getName() );
    	return result.toString();
    }
    
    /**
     * Compares this Field against the specified object. 
     * Returns <code>true</code> if the objects are the same. 
     * Two Field objects are the same if they were declared by the same class and have the same name and type.  
     */
    @Override
    public boolean equals( Object obj )
    {
        if(this == obj) 
        {
            return true;
        }
        if (obj == null || !(obj instanceof JavaField) )
        {
            return false;
        }
        JavaField fld = (JavaField) obj;
        if ( fld.getDeclaringClass().equals( this.getDeclaringClass() ) ) 
        {
            return false;
        }
        //Don't see any reason to compare the Type. Otherwise it's already invalid
        return fld.getName().equals( this.getName() );
    }
    
    @Override
    public int hashCode()
    {
        return getDeclaringClass().hashCode() * getName().hashCode();
    }
}
