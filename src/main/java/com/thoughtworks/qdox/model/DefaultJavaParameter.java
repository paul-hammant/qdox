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


public class DefaultJavaParameter extends AbstractBaseJavaEntity implements JavaParameter 
{

    private String name;
    private Type type;
    private JavaMethod parentMethod;
    private boolean varArgs;

    public DefaultJavaParameter(Type type, String name) {
        this(type, name, false);
    }

    public DefaultJavaParameter(Type type, String name, boolean varArgs) {
        this.name = name;
        this.type = type;
        this.varArgs = varArgs;
    }

    public String getCodeBlock() {
    	return getModelWriter().writeParameter(this).toString();
    }
    
	public void setName(String name) {
	    this.name = name;
	}

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaParameter#getName()
     */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaParameter#getType()
     */
    public Type getType() {
        return type;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaParameter#getParentMethod()
     */
    public JavaMethod getParentMethod() {
        return parentMethod;
    }

    public void setParentMethod(JavaMethod parentMethod) {
        this.parentMethod = parentMethod;
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaParameter#getParentClass()
     */
    public JavaClass getParentClass()
    {
        return getParentMethod().getParentClass();
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaParameter#isVarArgs()
     */
    public boolean isVarArgs() {
        return varArgs;
    }

    public String getFullyQualifiedName()
    {
        return type.getFullyQualifiedName();
    }
    
    public String getValue()
    {
        return type.getValue();
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaParameter#getResolvedValue()
     */
    public String getResolvedValue() {
        return Type.getResolvedValue( type, getParentMethod().getTypeParameters() );
    }

    public String getResolvedFullyQualifiedName() 
    {
        return Type.getResolvedFullyQualifiedName( type, getParentMethod().getTypeParameters() );
    }
    
	/* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaParameter#getResolvedGenericValue()
     */
	public String getResolvedGenericValue() 
	{
		return type.getResolvedGenericValue( getParentMethod().getTypeParameters() );
	}
	
	public String getResolvedGenericFullyQualifiedName()
	{
	    return type.getResolvedGenericFullyQualifiedName( getParentMethod().getTypeParameters() );
	}

    @Override
    public int hashCode() {
        return 13 + ( isVarArgs() ? 1 : 0 ) + getType().hashCode();
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( !( obj instanceof JavaParameter ) )
        {
            return false;
        }
        JavaParameter p = (JavaParameter) obj;
        // name should not be used in equality check.
        return getType().equals( p.getType() ) && isVarArgs() == p.isVarArgs();
    }
	
    @Override
    public String toString() {
        return getResolvedValue() + " "+ name;
    }
	
}
