package com.thoughtworks.qdox.model;

import java.util.List;

/**
 * 
 * @author Robert
 * @since 2.0
 */
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

    public boolean signatureMatches( List<Type> parameterTypes );

    public boolean signatureMatches( List<Type> parameterTypes, boolean varArgs );

}
