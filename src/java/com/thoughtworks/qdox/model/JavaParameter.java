package com.thoughtworks.qdox.model;

import java.io.Serializable;

public class JavaParameter extends AbstractBaseJavaEntity implements Serializable {

    public static final JavaParameter[]
            EMPTY_ARRAY = new JavaParameter[0];

    private String name;
    private Type type;
    private JavaMethod parentMethod;
    private boolean varArgs;

    public JavaParameter(Type type, String name) {
        this(type, name, false);
    }

    public JavaParameter(Type type, String name, boolean varArgs) {
        this.name = name;
        this.type = type;
        this.varArgs = varArgs;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public boolean equals(Object obj) {
        JavaParameter p = (JavaParameter) obj;
        // name isn't used in equality check.
        return getType().equals(p.getType());
    }

    public int hashCode() {
        return getType().hashCode();
    }

    public JavaMethod getParentMethod() {
        return parentMethod;
    }

    public void setParentMethod(JavaMethod parentMethod) {
        this.parentMethod = parentMethod;
    }
    
    public JavaClass getParentClass()
    {
        return getParentMethod().getParentClass();
    }

    /**
     * Is this a Java 5 var args type specified using three dots. e.g. void doStuff(Object... thing)
     * @since 1.6
     */
    public boolean isVarArgs() {
        return varArgs;
    }
    
    public String toString() {
    	return getResolvedValue() + " "+ name;
    }
    
    /**
     * 
     * @return the resolved value if the method has typeParameters, otherwise type's value
     * @since 1.10
     */
    public String getResolvedValue() {
		return type.getResolvedValue(getParentMethod().getTypeParameters());
    }

	public String getResolvedGenericValue() {
		return type.getResolvedGenericValue(getParentMethod().getTypeParameters());
	}
	

}
