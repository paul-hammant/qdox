package com.thoughtworks.qdox.model;

/**
 * A shared interface for the common functionality of Method and Constructor.
 * 
 * @author Robert Scholte
 * @since 2.0
 * 
 * @see java.lang.reflect.Executable
 */
public interface JavaExecutable extends JavaMember, JavaGenericDeclaration
{
    JavaClass getDeclaringClass();

}
