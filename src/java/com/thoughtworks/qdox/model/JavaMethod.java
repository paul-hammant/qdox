package com.thoughtworks.qdox.model;

import java.beans.Introspector;
import java.util.List;
import java.util.ArrayList;

public class JavaMethod extends AbstractInheritableJavaEntity {

    protected Type returns;
    private JavaParameter[] parameters = JavaParameter.EMPTY_ARRAY;
    private Type[] exceptions = Type.EMPTY_ARRAY;
    private boolean constructor;

    private JavaClass parentClass;

    public Type getReturns() {
        return returns;
    }

    public JavaParameter[] getParameters() {
        return parameters;
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
        for (int i = 0; i < parameters.length; i++) {
            JavaParameter parameter = parameters[i];
            if (i > 0) result.write(", ");
            if (isDeclaration) {
                result.write(parameter.getType().toString());
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
            result.write(';');
            result.newline();
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

    public void setParameters(JavaParameter[] parameters) {
        for (int i = 0; i < parameters.length; i++) {
            parameters[i].setParentMethod(this);
        }
        this.parameters = parameters;
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
        parameterTypes = parameterTypes == null ? new Type[0] : parameterTypes;
        if (parameterTypes.length != this.parameters.length) return false;
        for (int i = 0; i < parameters.length; i++) {
            if (!parameters[i].getType().equals(parameterTypes[i])) {
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
        return parentClass;
    }

    public void setParentClass(JavaClass parentClass) {
        this.parentClass = parentClass;
    }

    /**
     * @return true if this method is a Java Bean accessor
     * @since 1.3
     */
    public boolean isPropertyAccessor() {
        boolean signatureOk = false;
        boolean nameOk = false;

        if (getName().startsWith("is")) {
            String returnType = getReturns().getValue();
            signatureOk = returnType.equals("boolean") || returnType.equals("java.lang.Boolean");
            signatureOk = signatureOk && getReturns().getDimensions() == 0;
            if (getName().length() > 2) {
                nameOk = Character.isUpperCase(getName().charAt(2));
            }
        }
        if (getName().startsWith("get")) {
            signatureOk = true;
            if (getName().length() > 3) {
                nameOk = Character.isUpperCase(getName().charAt(3));
            }
        }
        boolean noParams = getParameters().length == 0;
        return signatureOk && nameOk && noParams;
    }

    /**
     * @return true if this method is a Java Bean accessor
     * @since 1.3
     */
    public boolean isPropertyMutator() {
        boolean nameOk = false;
        if (getName().startsWith("set")) {
            if (getName().length() > 3) {
                nameOk = Character.isUpperCase(getName().charAt(3));
            }
        }

        boolean oneParam = getParameters().length == 1;
        return nameOk && oneParam;
    }

    /**
     * @return the type of the property this method represents, or null if this method
     * is not a property mutator or property accessor.
     * @since 1.3
     */
    public Type getPropertyType() {
        Type result = null;
        if (isPropertyAccessor()){
            result = getReturns();
        } else if(isPropertyMutator()){
            result = getParameters()[0].getType();
        } else {
            result = null;
        }
        return result;
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
}
