package com.thoughtworks.qdox.model;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author Robert
 * @since 2.0
 */
public interface JavaConstructor
    extends JavaModel, JavaAnnotatedElement, JavaGenericDeclaration, JavaMember, Serializable, Comparable<JavaConstructor>
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
