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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.thoughtworks.qdox.io.IndentBuffer;

public class DefaultJavaMethod extends AbstractInheritableJavaEntity implements JavaMethod {

	private List<TypeVariable> typeParameters = Collections.emptyList(); 
    private Type returns = Type.VOID;
    private List<JavaParameter> parameters = new LinkedList<JavaParameter>();
    private List<Type> exceptions = Collections.emptyList();
    private boolean constructor;
    private String sourceCode;
    private boolean varArgs;

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
     * @see com.thoughtworks.qdox.model.JavaMethod#getParameters()
     */
    public List<JavaParameter> getParameters() {
        return parameters;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaMethod#getParameterByName(java.lang.String)
     */
    public JavaParameter getParameterByName(String name) {
        for (JavaParameter parameter : getParameters()) {
            if (parameter.getName().equals(name)) {
                return parameter;
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaMethod#getExceptions()
     */
    public List<Type> getExceptions() {
        return exceptions;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaMethod#isConstructor()
     */
    public boolean isConstructor() {
        return constructor;
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaMethod#isVarArgs()
     */
    public boolean isVarArgs()
    {
        return varArgs;
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaMethod#getCodeBlock()
     */
    public String getCodeBlock()
    {
        return getSource().getModelWriter().writeMethod( this ).toString();
    }

    /**
     * @since 1.3
     */
    protected void writeBody(IndentBuffer result, boolean withModifiers, boolean isDeclaration, boolean isPrettyPrint) {
        if (withModifiers) {
            writeAccessibilityModifier(result);
            writeNonAccessibilityModifiers(result);
        }

        if (!constructor) {
            if(isDeclaration) {
                result.write(returns.toString());
                result.write(' ');
            }
        }

        result.write(name);
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
     * @since 1.3
     */
    private String getSignature(boolean withModifiers, boolean isDeclaration) {
        IndentBuffer result = new IndentBuffer();
        writeBody(result, withModifiers, isDeclaration, false);
        return result.toString();
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaMethod#getDeclarationSignature(boolean)
     */
    public String getDeclarationSignature(boolean withModifiers) {
        return getSignature(withModifiers, true);
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaMethod#getCallSignature()
     */
    public String getCallSignature() {
        return getSignature(false, false);
    }

    /**
     * Define the return type of this method
     * 
     * @param returns the return type
     */
    public void setReturns(Type returns) {
        this.returns = returns;
    }

    public void addParameter(JavaParameter javaParameter) {
        parameters.add( javaParameter );
        this.varArgs = javaParameter.isVarArgs();
    }

    public void setExceptions(List<Type> exceptions) {
        this.exceptions = exceptions;
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

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaMethod#signatureMatches(java.lang.String, java.util.List)
     */
    public boolean signatureMatches(String name, List<Type> parameterTypes) {
        return signatureMatches( name, parameterTypes, false );
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaMethod#signatureMatches(java.lang.String, java.util.List, boolean)
     */
    public boolean signatureMatches(String name, List<Type> parameterTypes, boolean varArg) {
        if (!name.equals(this.name)) return false;
        
        List<Type> parameterTypeList;
        if( parameterTypes == null) {
            parameterTypeList = Collections.emptyList();
        }
        else {
            parameterTypeList = parameterTypes;
        }
        
        if (parameterTypeList.size() != this.getParameters().size()) return false;
        
        for (int i = 0; i < parameters.size(); i++) {
            if (!parameters.get(i).getType().equals(parameterTypes.get(i))) {
                return false;
            }
        }
        return (this.varArgs == varArg);
    }

    public int hashCode() {
        int hashCode = name.hashCode();
        if (returns != null) hashCode *= returns.hashCode();
        hashCode *= getParameters().size();
        return hashCode;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaMethod#isPublic()
     */
    public boolean isPublic() {
        return super.isPublic() || (getParentClass() != null ? getParentClass().isInterface() : false);
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

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaMethod#getTagsByName(java.lang.String, boolean)
     */
    public List<DocletTag> getTagsByName(String name, boolean inherited) {
        JavaClass clazz = getParentClass();
        List<Type> types = new LinkedList<Type>();
        for (JavaParameter parameter : getParameters()) {
            types.add(parameter.getType());
        }
        List<JavaMethod> methods = clazz.getMethodsBySignature(getName(), types, true);

        List<DocletTag> result = new LinkedList<DocletTag>();
        for (JavaMethod method : methods) {
            List<DocletTag> tags = method.getTagsByName(name);
            for (DocletTag tag : tags) {
                if(!result.contains(tag)) {
                    result.add(tag);
                }
            }
        }
        return result;
    }

    public int compareTo(Object o) {
        return getDeclarationSignature(false).compareTo(((JavaMethod)o).getDeclarationSignature(false));
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaMethod#getSourceCode()
     */
    public String getSourceCode(){
    	return sourceCode;
    }

    public void setSourceCode(String sourceCode){
    	this.sourceCode = sourceCode;
    }

	public void setTypeParameters(List<TypeVariable> typeParameters) {
		this.typeParameters = typeParameters;
	}
	
	/* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaMethod#getTypeParameters()
     */
	public List<TypeVariable> getTypeParameters() {
		return typeParameters;
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
    
//    /**
//     * 
//     * @param resolve
//     * @param callingClass
//     * @return
//     * @since 1.12
//     */
//    protected Type getReturnType ( boolean resolve, JavaClass callingClass) {
//        Type result = null;
//        if (this.getReturns() != null) {
//            result =  this.getReturns().resolve( this.getParentClass(), callingClass );
//            
//            //According to java-specs, if it could be resolved the upper boundary, so Object, should be returned  
//            if ( !resolve && !this.getReturns().getFullyQualifiedName().equals( result.getFullyQualifiedName() ) )
//            {
//                result = new Type( "java.lang.Object" );
//            }
//        }
//        return result;
//    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaMethod#getParameterTypes()
     */
    public List<Type> getParameterTypes() {
        return getParameterTypes( false );
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaMethod#getParameterTypes(boolean)
     */
    public List<Type> getParameterTypes( boolean resolve ) {
        List<Type> result = new LinkedList<Type>();
        for (JavaParameter parameter : this.getParameters()) {
            result.add( parameter.getType() );
        }
        return result;
    }
}
