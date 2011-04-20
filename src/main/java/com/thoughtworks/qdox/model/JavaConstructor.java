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

    List<JavaParameter> getParameters();

    JavaParameter getParameterByName( String name );
    
    List<Type> getParameterTypes();
    
    List<Type> getExceptions();

    String getSourceCode();
    
    String getCodeBlock();

    boolean isVarArgs();

    boolean signatureMatches( List<Type> parameterTypes );

    boolean signatureMatches( List<Type> parameterTypes, boolean varArgs );

}
