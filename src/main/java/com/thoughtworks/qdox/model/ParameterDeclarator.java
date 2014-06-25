package com.thoughtworks.qdox.model;

/**
 * Can either be a Method or a Constructor
 * 
 * @author Robert Scholte
 * @since 2.0
 */
public interface ParameterDeclarator extends JavaGenericDeclaration 
{
    JavaClass getParentClass();

}
