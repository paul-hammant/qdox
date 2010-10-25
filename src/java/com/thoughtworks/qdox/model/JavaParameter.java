package com.thoughtworks.qdox.model;

public interface JavaParameter
{

    public static final JavaParameter[] EMPTY_ARRAY = new JavaParameter[0];

    public String getName();

    public Type getType();

    public boolean equals( Object obj );

    public int hashCode();

    public JavaMethod getParentMethod();

    public JavaClass getParentClass();

    /**
     * Is this a Java 5 var args type specified using three dots. e.g. void doStuff(Object... thing)
     * @since 1.6
     */
    public boolean isVarArgs();

    public String toString();

    /**
     * 
     * @return the resolved value if the method has typeParameters, otherwise type's value
     * @since 1.10
     */
    public String getResolvedValue();

    public String getResolvedGenericValue();

    public Annotation[] getAnnotations();

}