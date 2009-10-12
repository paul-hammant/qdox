package com.thoughtworks.qdox.model;

import java.beans.Introspector;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class JavaMethod extends AbstractInheritableJavaEntity implements Member {

	private TypeVariable[] typeParameters = TypeVariable.EMPTY_ARRAY; 
    private Type returns = Type.VOID;
    private List parameters = new LinkedList();
    private JavaParameter[] parametersArray = JavaParameter.EMPTY_ARRAY;
    private Type[] exceptions = Type.EMPTY_ARRAY;
    private boolean constructor;
    private String sourceCode;

    public JavaMethod() {
    }

    public JavaMethod(String name) {
        setName(name);
    }

    public JavaMethod(Type returns, String name) {
        setReturns(returns);
        setName(name);
    }
    
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

    public boolean isConstructor() {
        return constructor;
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

    public void setReturns(Type returns) {
        this.returns = returns;
    }

    public void addParameter(JavaParameter javaParameter) {
        javaParameter.setParentMethod( this );
        parameters.add( javaParameter );
        parametersArray = null;
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

        return true;
    }

    /**
     * @param name method name
     * @param parameterTypes parameter types or null if there are no parameters.
     * @return true if the signature and parameters match.
     */
    public boolean signatureMatches(String name, Type[] parameterTypes) {
        if (!name.equals(this.name)) return false;
        parameterTypes = (parameterTypes == null ? new Type[0] : parameterTypes);
        if (parameterTypes.length != this.getParameters().length) return false;
        for (int i = 0; i < parametersArray.length; i++) {
            if (!parametersArray[i].getType().equals(parameterTypes[i])) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int hashCode = name.hashCode();
        if (returns != null) hashCode *= returns.hashCode();
        hashCode *= getParameters().length;
        return hashCode;
    }

    public JavaClass getParentClass() {
        return (JavaClass) getParent();
    }

    public void setParentClass(JavaClass parentClass) {
        setParent(parentClass);
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
		result.append(getReturns().getValue() + " ");
		if(getParentClass() != null) {
			result.append(getParentClass().getFullyQualifiedName() + ".");
		}
		result.append(getName());
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
}
