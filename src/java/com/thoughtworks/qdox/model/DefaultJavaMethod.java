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

import java.beans.Introspector;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.thoughtworks.qdox.io.IndentBuffer;

public class DefaultJavaMethod extends AbstractBaseMethod implements JavaMethod {

	private Type returns = Type.VOID;
    private boolean constructor;
    /**
     * The default constructor
     */
    public DefaultJavaMethod() {
    }

    /**
     * Create new method without parameters and return type
     * 
     * @param name the name of the method
     */
    public DefaultJavaMethod(String name) {
        setName(name);
    }

    /**
     * Create a new method without parameters
     * 
     * @param returns the return type
     * @param name the name of this method
     */
    public DefaultJavaMethod(Type returns, String name) {
        setReturns(returns);
        setName(name);
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaMethod#getReturns()
     */
    public Type getReturns() {
        return returns;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaMethod#isConstructor()
     */
    public boolean isConstructor() {
        return constructor;
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaMethod#getCodeBlock()
     */
    public String getCodeBlock()
    {
        return getModelWriter().writeMethod( this ).toString();
    }

    /**
     * @since 1.3
     */
    private String getSignature( boolean withModifiers, boolean isDeclaration )
    {
        IndentBuffer result = new IndentBuffer();
        writeBody(result, withModifiers, isDeclaration, false);
        return result.toString();
    }


    public String getDeclarationSignature( boolean withModifiers )
    {
        return getSignature(withModifiers, true);
    }

    public String getCallSignature()
    {
        return getSignature(false, false);
    }

    /**
     * @since 1.3
     */
    protected void writeBody(IndentBuffer result, boolean withModifiers, boolean isDeclaration, boolean isPrettyPrint) {
        if (withModifiers) {
            for (String modifier : getModifiers()) {
            	// check for public, protected and private
                if (modifier.startsWith("p")) {
                    result.write(modifier);
                    result.write(' ');
                }
            }
            for (String modifier : getModifiers()) {
            	// check for public, protected and private
                if (!modifier.startsWith("p")) {
                    result.write(modifier);
                    result.write(' ');
                }
            }
        }

        if (!constructor) {
            if(isDeclaration) {
                result.write(returns.toString());
                result.write(' ');
            }
        }

        result.write(getName());
        result.write('(');
        for (ListIterator<JavaParameter> iter = getParameters().listIterator(); iter.hasNext();) {
            JavaParameter parameter = iter.next();
            if (isDeclaration) {
                result.write(parameter.getType().toString());
                if (parameter.isVarArgs()) {
                    result.write("...");
                }
                result.write(' ');
            }
            result.write(parameter.getName());
            if (iter.hasNext()) 
            {
                result.write(", ");
            }
        }
        result.write(')');
        if (isDeclaration) {
            if (exceptions.size() > 0) {
                result.write(" throws ");
                for(Iterator<Type> excIter = exceptions.iterator();excIter.hasNext();) {
                    result.write(excIter.next().getValue());
                    if(excIter.hasNext()) {
                        result.write(", ");
                    }
                }
            }
        }
        if (isPrettyPrint) {
            if (sourceCode != null && sourceCode.length() > 0) {
                result.write(" {");
                result.newline();
                result.write(sourceCode);
                result.write("}");
                result.newline();
            } else {
                result.write(';');
                result.newline();
            }
        }
    }

    /**
     * Define the return type of this method
     * 
     * @param returns the return type
     */
    public void setReturns(Type returns) {
        this.returns = returns;
    }

    public void setConstructor(boolean constructor) {
        this.constructor = constructor;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaMethod#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj == null) return false;
        JavaMethod m = (JavaMethod) obj;

        if (m.isConstructor() != isConstructor()) return false;

        if (m.getName() == null) return (getName() == null);
        if (!m.getName().equals(getName())) return false;
        
        if (m.getReturns() == null) return (getReturns() == null);
        if (!m.getReturns().equals(getReturns())) return false;

        List<JavaParameter> myParams = getParameters();
        List<JavaParameter> otherParams = m.getParameters();
        if (otherParams.size() != myParams.size()) return false;
        for (int i = 0; i < myParams.size(); i++) {
            if (!otherParams.get(i).equals(myParams.get(i))) return false;
        }

        return this.varArgs == m.isVarArgs();
    }

    public int hashCode() {
        int hashCode = getName().hashCode();
        if (returns != null) hashCode *= returns.hashCode();
        hashCode *= getParameters().size();
        return hashCode;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaMethod#isPropertyAccessor()
     */
    public boolean isPropertyAccessor() {
        if (isStatic()) return false;
        if (getParameters().size() != 0) return false;
        
        if (getName().startsWith("is")) {
            return (getName().length() > 2
                    && Character.isUpperCase(getName().charAt(2)));
        }
        if (getName().startsWith("get")) {
            return (getName().length() > 3
                    && Character.isUpperCase(getName().charAt(3)));
        }
        
        return false;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaMethod#isPropertyMutator()
     */
    public boolean isPropertyMutator() {
        if (isStatic()) return false;
        if (getParameters().size() != 1) return false;
        
        if (getName().startsWith("set")) {
            return (getName().length() > 3
                    && Character.isUpperCase(getName().charAt(3)));
        }

        return false;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaMethod#getPropertyType()
     */
    public Type getPropertyType() {
        if (isPropertyAccessor()) {
            return getReturns();
        }
        if (isPropertyMutator()) {
            return getParameters().get(0).getType();
        } 
        return null;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaMethod#getPropertyName()
     */
    public String getPropertyName() {
        int start = -1;
        if (getName().startsWith("get") || getName().startsWith("set")) {
            start = 3;
        } else if (getName().startsWith("is")) {
            start = 2;
        } else {
            return null;
        }
        return Introspector.decapitalize(getName().substring(start));
    }

    public int compareTo(Object o) {
        return getDeclarationSignature(false).compareTo(((JavaMethod)o).getDeclarationSignature(false));
    }

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
		if(isAbstract()) {
			result.append("abstract ");
		}
		if(isStatic()) {
			result.append("static ");
		}
		if(isFinal()) {
			result.append("final ");
		}
		if(isSynchronized()) {
			result.append("synchronized ");
		}
		if(isNative()) {
			result.append("native ");
		}
		if (!constructor) {
		    result.append(getReturns().getValue() + " ");
		}
		if(getParentClass() != null) {
			result.append(getParentClass().getFullyQualifiedName());
			if (!constructor) {
			    result.append(".");
			}
		}
		if (!constructor) {
		    result.append(getName());
		}
		result.append("(");
		for(int paramIndex=0;paramIndex<getParameters().size();paramIndex++) {
			if(paramIndex>0) {
				result.append(",");
			}
			String typeValue = getParameters().get(paramIndex).getType().getResolvedValue(getTypeParameters());
			result.append(typeValue);
		}
		result.append(")");
		if (exceptions.size() > 0) {
            result.append(" throws ");
            for (Iterator<Type> excIter = exceptions.iterator();excIter.hasNext();) {
                result.append(excIter.next().getValue());
                if(excIter.hasNext()) {
                    result.append(",");
                }
            }
        }
		return result.toString();
	}

	/* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaMethod#getGenericReturnType()
     */
    public Type getGenericReturnType()
    {
        return returns;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaMethod#getReturnType()
     */
    public Type getReturnType() {
	    return getReturnType( false );
	}
	
    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaMethod#getReturnType(boolean)
     */
    public Type getReturnType( boolean resolve )
    {
        return returns;
    }
}
