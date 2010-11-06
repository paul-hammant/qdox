package com.thoughtworks.qdox.model;

public interface JavaField extends JavaAnnotatedElement, JavaMember, JavaModel
{

    public Type getType();

    public String getCodeBlock();

    public int compareTo( Object o );

    public String getDeclarationSignature( boolean withModifiers );

    public String getCallSignature();

    /**
     * Get the original expression used to initialize the field.
     *
     * @return initialization as string.
     */
    public String getInitializationExpression();

}