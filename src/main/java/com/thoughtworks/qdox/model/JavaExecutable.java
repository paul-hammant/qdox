package com.thoughtworks.qdox.model;

import java.util.List;

/**
 * A shared interface for the common functionality of Method and Constructor.
 * 
 * @author Robert Scholte
 * @since 2.0
 * 
 * @see java.lang.reflect.Executable
 */
public interface JavaExecutable extends JavaAnnotatedElement, JavaGenericDeclaration, JavaMember
{
    JavaClass getDeclaringClass();
    
    /**
     * Equivalent of {@link java.lang.reflect.Executable#getExceptionTypes()}
     * 
     * @return a list of Exceptions, never <code>null</code>
     */
    List<JavaClass> getExceptions();
    
    List<JavaType> getExceptionTypes();
    
    /**
     * 
     * @param name the name of the parameter
     * @return the {@link JavaParameter} matching the name, otherwise <code>null</code>
     */
    JavaParameter getParameterByName( String name );
    
    /**
     * Equivalent of {@link java.lang.reflect.Executable#getParameterTypes()}, where a JavaParameter also contains the original name if available.
     * 
     * @return a list of JavaParameters, never <code>null</code>
     */
    List<JavaParameter> getParameters();
    
    /**
     * Equivalent of {@link java.lang.reflect.Executable#getParameterTypes()}
     * 
     * @return a list of JavaParameters, never <code>null</code>
     * @since 1.12 
     */
    List<JavaType> getParameterTypes();

    /**
     * If a class inherits this method from a generic class or interface, you can use this method to get the resolved parameter types
     * 
     * @param resolve {@code true} if the resolved types should be returned, otherwise {@code false}
     * @return the parameter types
     * @since 1.12
     */
    List<JavaType> getParameterTypes( boolean resolve );
    
    /**
     * Get the original source code of the body of this method.
     *
     * @return Code as string.
     */
    String getSourceCode();
    
    /**
     * Equivalent of {@link java.lang.reflect.Executable#isVarArgs()} 
     * 
     * @return <code>true</code> if the final parameter is a varArg, otherwise <code>false</code>
     */
    boolean isVarArgs();

    String getCallSignature();
}
