package com.thoughtworks.qdox.model;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class JavaMethod extends AbstractInheritableJavaEntity implements Member {

	private TypeVariable[] typeParameters = TypeVariable.EMPTY_ARRAY; 
    private Type returns = Type.VOID;
    private List parameters = new LinkedList();
    private JavaParameter[] parametersArray = JavaParameter.EMPTY_ARRAY;
    private Type[] exceptions = Type.EMPTY_ARRAY;
    private boolean constructor;
    private String sourceCode;
    private boolean varArgs;

    /**
     * The default constructor
     */
    public JavaMethod() {
    }

    /**
     * Create new method without parameters and return type
     * 
     * @param name the name of the method
     */
    public JavaMethod(String name) {
        setName(name);
    }

    /**
     * Create a new method without parameters
     * 
     * @param returns the return type
     * @param name the name of this method
     */
    public JavaMethod(Type returns, String name) {
        setReturns(returns);
        setName(name);
    }
    
    /**
     * 
     * @return the return type
     * @deprecated it is recommended to use getReturnType()
     */
    public Type getReturns() {
        return returns;
    }

    public JavaParameter[] getParameters() {
        if(parametersArray == null) {
            parametersArray = new JavaParameter[parameters.size()];
            parameters.toArray( parametersArray );
        }
        return parametersArray;
    }

    public JavaParameter getParameterByName(String name) {
        JavaParameter[] parameters = getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getName().equals(name)) {
                return parameters[i];
            }
        }
        return null;
    }

    public Type[] getExceptions() {
        return exceptions;
    }

    /**
     * 
     * @return true is this method is a constructor
     */
    public boolean isConstructor() {
        return constructor;
    }
    
    /**
     * 
     * @return true is this method conains varArgs
     */
    public boolean isVarArgs()
    {
        return varArgs;
    }

    protected void writeBody(IndentBuffer result) {
        writeBody(result, true, true, true);
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
        for (int i = 0; i < getParameters().length; i++) {
            JavaParameter parameter = parametersArray[i];
            if (i > 0) result.write(", ");
            if (isDeclaration) {
                result.write(parameter.getType().toString());
                if (parameter.isVarArgs()) {
                    result.write("...");
                }
                result.write(' ');
            }
            result.write(parameter.getName());
        }
        result.write(')');
        if (isDeclaration) {
            if (exceptions.length > 0) {
                result.write(" throws ");
                for (int i = 0; i < exceptions.length; i++) {
                    if (i > 0) result.write(", ");
                    result.write(exceptions[i].getValue());
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

    /**
     * @since 1.3
     */
    public String getDeclarationSignature(boolean withModifiers) {
        return getSignature(withModifiers, true);
    }

    /**
     * @since 1.3
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
        parametersArray = null;
        this.varArgs = javaParameter.isVarArgs();
    }

    public void setExceptions(Type[] exceptions) {
        this.exceptions = exceptions;
    }

    public void setConstructor(boolean constructor) {
        this.constructor = constructor;
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        JavaMethod m = (JavaMethod) obj;

        if (m.isConstructor() != isConstructor()) return false;

        if (m.getName() == null) return (getName() == null);
        if (!m.getName().equals(getName())) return false;
        
        if (m.getReturns() == null) return (getReturns() == null);
        if (!m.getReturns().equals(getReturns())) return false;

        JavaParameter[] myParams = getParameters();
        JavaParameter[] otherParams = m.getParameters();
        if (otherParams.length != myParams.length) return false;
        for (int i = 0; i < myParams.length; i++) {
            if (!otherParams[i].equals(myParams[i])) return false;
        }

        return this.varArgs == m.varArgs;
    }

    /**
     * This method is NOT varArg aware. The overloaded method is.
     * 
     * @param name
     * @param parameterTypes
     * @return
     * @deprecated use overloaded method 
     */
    public boolean signatureMatches(String name, Type[] parameterTypes) {
        return signatureMatches( name, parameterTypes, false );
    }
    
    /**
     * @param name method name
     * @param parameterTypes parameter types or null if there are no parameters.
     * @return true if the signature and parameters match.
     */
    public boolean signatureMatches(String name, Type[] parameterTypes, boolean varArg) {
        if (!name.equals(this.name)) return false;
        parameterTypes = (parameterTypes == null ? new Type[0] : parameterTypes);
        if (parameterTypes.length != this.getParameters().length) return false;
        for (int i = 0; i < parametersArray.length; i++) {
            if (!parametersArray[i].getType().equals(parameterTypes[i])) {
                return false;
            }
        }
        return (this.varArgs == varArg);
    }

    public int hashCode() {
        int hashCode = name.hashCode();
        if (returns != null) hashCode *= returns.hashCode();
        hashCode *= getParameters().length;
        return hashCode;
    }

    public boolean isPublic() {
        return super.isPublic() || (getParentClass() != null ? getParentClass().isInterface() : false);
    }

    /**
     * @return true if this method is a Java Bean accessor
     * @since 1.3
     */
    public boolean isPropertyAccessor() {
        if (isStatic()) return false;
        if (getParameters().length != 0) return false;
        
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

    /**
     * @return true if this method is a Java Bean accessor
     * @since 1.3
     */
    public boolean isPropertyMutator() {
        if (isStatic()) return false;
        if (getParameters().length != 1) return false;
        
        if (getName().startsWith("set")) {
            return (getName().length() > 3
                    && Character.isUpperCase(getName().charAt(3)));
        }

        return false;
    }

    /**
     * @return the type of the property this method represents, or null if this method
     * is not a property mutator or property accessor.
     * @since 1.3
     */
    public Type getPropertyType() {
        if (isPropertyAccessor()) {
            return getReturns();
        }
        if (isPropertyMutator()) {
            return getParameters()[0].getType();
        } 
        return null;
    }

    /**
     * @return the name of the property this method represents, or null if this method
     * is not a property mutator or property accessor.
     * @since 1.3
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

    public DocletTag[] getTagsByName(String name, boolean inherited) {
        JavaClass clazz = getParentClass();
        JavaParameter[] params = getParameters();
        Type[] types = new Type[params.length];
        for (int i = 0; i < types.length; i++) {
            types[i] = params[i].getType();
        }
        JavaMethod[] methods = clazz.getMethodsBySignature(getName(), types, true);

        List result = new ArrayList();
        for (int i = 0; i < methods.length; i++) {
            JavaMethod method = methods[i];
            DocletTag[] tags = method.getTagsByName(name);
            for (int j = 0; j < tags.length; j++) {
                DocletTag tag = tags[j];
                if(!result.contains(tag)) {
                    result.add(tag);
                }
            }
        }
        return (DocletTag[]) result.toArray(new DocletTag[result.size()]);
    }

    public int compareTo(Object o) {
        return getDeclarationSignature(false).compareTo(((JavaMethod)o).getDeclarationSignature(false));
    }

    /**
     * Get the original source code of the body of this method.
     *
     * @return Code as string.
     */
    public String getSourceCode(){
    	return sourceCode;
    }

    public void setSourceCode(String sourceCode){
    	this.sourceCode = sourceCode;
    }

	public void setTypeParameters(TypeVariable[] typeParameters) {
		this.typeParameters = typeParameters;
	}
	
	public TypeVariable[] getTypeParameters() {
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
		for(int paramIndex=0;paramIndex<getParameters().length;paramIndex++) {
			if(paramIndex>0) {
				result.append(",");
			}
			String typeValue = getParameters()[paramIndex].getType().getResolvedValue(getTypeParameters());
			result.append(typeValue);
		}
		result.append(")");
		for(int i = 0; i < exceptions.length; i++) {
			result.append(i==0 ? " throws " : ",");
			result.append(exceptions[i].getValue());
		}
		return result.toString();
	}

	/**
	 * Equivalent of java.lang.reflect.Method.getGenericReturnType()
	 * 
	 * @return the generic returntype
	 * @since 1.12
	 */
    public Type getGenericReturnType()
    {
        return returns;
    }

    /**
     * Equivalent of java.lang.reflect.Method.getReturnType()
     * 
     * @return
     * @since 1.12
     */
    public Type getReturnType() {
	    return getReturnType( false );
	}
	
    /**
     * If a class inherits this method from a generic class or interface, you can use this method to get the resolved return type
     * 
     * @param resolve
     * @return
     * @since 1.12
     */
    public Type getReturnType( boolean resolve )
    {
        return getReturnType( resolve, getParentClass() );
    }
    
    /**
     * 
     * @param resolve
     * @param callingClass
     * @return
     * @since 1.12
     */
    protected Type getReturnType ( boolean resolve, JavaClass callingClass) {
        Type result = null;
        if (getReturns() != null) {
            result =  getReturns().resolve( this.getParentClass(), callingClass );
            
            //According to java-specs, if it could be resolved the upper boundary, so Object, should be returned  
            if ( !resolve && !returns.getFullyQualifiedName().equals( result.getFullyQualifiedName() ) )
            {
                result = new Type( "java.lang.Object" );
            }
        }
        return result;
    }
    
    /**
     * 
     * @return the parameter types as array
     * @since 1.12
     */
    public Type[] getParameterTypes() {
        return getParameterTypes( false );
    }
    
    /**
     * If a class inherits this method from a generic class or interface, you can use this method to get the resolved parameter types
     * 
     * @param resolve
     * @return the parameter types as array
     * @since 1.12
     */
    public Type[] getParameterTypes( boolean resolve ) {
        return getParameterTypes( resolve, getParentClass() );
    }

    
    protected Type[] getParameterTypes ( boolean resolve, JavaClass callingClass) {
        Type[] result = new Type[getParameters().length];

        for (int paramIndex = 0; paramIndex < getParameters().length; paramIndex++ )
        {
            Type curType = getParameters()[paramIndex].getType().resolve( this.getParentClass(), callingClass );
            //According to java-specs, if it could be resolved the upper boundary, so Object, should be returned  
            if ( !resolve && returns != null && !returns.getFullyQualifiedName().equals( curType.getFullyQualifiedName() ) )
            {
                result[paramIndex] = new Type( "java.lang.Object" );
            }
            else {
                result[paramIndex] = curType;
            }
            
        }
        return result;
    }
}
