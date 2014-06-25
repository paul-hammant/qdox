package com.thoughtworks.qdox.model;

import java.util.List;

/**
 * Can either be a Method or a Constructor
 * 
 * @author Robert Scholte
 * @since 2.0
 */
public interface ParameterDeclarator<T extends JavaGenericDeclaration>
{

    List<JavaTypeVariable<T>> getTypeParameters();

    JavaClass getParentClass();

}
