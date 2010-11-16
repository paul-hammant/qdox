package com.thoughtworks.qdox.model;

import java.beans.Introspector;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class JavaMethod extends AbstractInheritableJavaEntity implements Member, JavaAnnotatedElement, JavaMember, JavaModel {

	private TypeVariable[] typeParameters = TypeVariable.EMPTY_ARRAY; 
    private Type returns = Type.VOID;
    private List<JavaParameter> parameters = new LinkedList<JavaParameter>();
    private List<Type> exceptions = Collections.emptyList();
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

    public List<JavaParameter> getParameters() {
        return parameters;
    }

    public JavaParameter getParameterByName(String name) {
        for (JavaParameter parameter : getParameters()) {
            if (parameter.getName().equals(name)) {
                return parameter;
            }
        }
        return null;
    }

    public List<Type> getExceptions() {
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
        ListIterator<JavaParameter> iter = getParameters().listIterator();
        while (iter.hasNext()) {
            if (iter.hasPrevious()) result.write(", ");
            JavaParameter parameter = iter.next();
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
            if (exceptions.size() > 0) {
                result.write(" throws ");
                Iterator<Type> excIter = exceptions.iterator();
                while (excIter.hasNext()) {
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
        this.varArgs = javaParameter.isVarArgs();
    }

    public void setExceptions(List<Type> exceptions) {
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

        List<JavaParameter> myParams = getParameters();
        List<JavaParameter> otherParams = m.getParameters();
        if (otherParams.size() != myParams.size()) return false;
        for (int i = 0; i < myParams.size(); i++) {
            if (!otherParams.get(i).equals(myParams.get(i))) return false;
        }

        return this.varArgs == m.isVarArgs();
    }

    /**
     * This method is NOT varArg aware. The overloaded method is.
     * 
     * @param name
     * @param parameterTypes
     * @return
     * @deprecated use overloaded method 
     */
    public boolean signatureMatches(String name, List<Type> parameterTypes) {
        return signatureMatches( name, parameterTypes, false );
    }
    
    /**
     * @param name method name
     * @param parameterTypes parameter types or null if there are no parameters.
     * @return true if the signature and parameters match.
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

    public boolean isPublic() {
        return super.isPublic() || (getParentClass() != null ? getParentClass().isInterface() : false);
    }

    /**
     * @return true if this method is a Java Bean accessor
     * @since 1.3
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

    /**
     * @return true if this method is a Java Bean accessor
     * @since 1.3
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
            return getParameters().get(0).getType();
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
            Iterator<Type> excIter = exceptions.iterator();
            while (excIter.hasNext()) {
                result.append(excIter.next().getValue());
                if(excIter.hasNext()) {
                    result.append(",");
                }
            }
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
        if (this.getReturns() != null) {
            result =  this.getReturns().resolve( this.getParentClass(), callingClass );
            
            //According to java-specs, if it could be resolved the upper boundary, so Object, should be returned  
            if ( !resolve && !this.getReturns().getFullyQualifiedName().equals( result.getFullyQualifiedName() ) )
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
        Type[] result = new Type[this.getParameters().size()];

        for (int paramIndex = 0; paramIndex < this.getParameters().size(); paramIndex++ )
        {
            Type curType = this.getParameters().get(paramIndex).getType().resolve( this.getParentClass(), callingClass );
            //According to java-specs, if it could be resolved the upper boundary, so Object, should be returned  
            if ( !resolve && this.getReturns() != null && !this.getReturns().getFullyQualifiedName().equals( curType.getFullyQualifiedName() ) )
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
