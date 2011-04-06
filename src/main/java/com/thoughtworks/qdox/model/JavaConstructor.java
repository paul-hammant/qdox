package com.thoughtworks.qdox.model;

import java.util.List;

public interface JavaConstructor
    extends JavaAnnotatedElement, JavaGenericDeclaration, JavaMember
{

    public List<JavaParameter> getParameters();

    public JavaParameter getParameterByName( String name );
    
    public List<Type> getParameterTypes();
    
    public List<Type> getExceptions();

    public String getSourceCode();
    
    public String getCodeBlock();

    public boolean isVarArgs();

}
