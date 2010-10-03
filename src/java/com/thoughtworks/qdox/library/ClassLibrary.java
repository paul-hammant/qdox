package com.thoughtworks.qdox.library;

import java.io.Serializable;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * Main methods of a ClassLibrary, which can be used by every Model 
 * 
 * @author Robert Scholte
 * @since 2.0
 */
public interface ClassLibrary
    extends Serializable
{
    /**
     * Check if this library should be able to build the JavaClass
     * 
     * @param name
     * @return
     */
    public boolean hasJavaClass( String name );

    /**
     * Get the JavaClass or null if it's not possible
     * 
     * @param name The fully qualified name of the JavaClass
     * @return The JavaClass or null
     */
    public JavaClass getJavaClass( String name );

    /**
     * 
     * It's up to the library to decide if also collects JavaClasses from it's ancestors 
     * 
     * @return all JavaClasses, never <code>null</code>
     */
    public JavaClass[] getJavaClasses();
    
    /**
     * 
     * It's up to the library to decide if also collects JavaSources from it's ancestors 
     * 
     * @return all JavaSources, never <code>null</code>
     */
    public JavaSource[] getJavaSources();
    
    /**
     * Get the JavaPackage or null if it's not possible
     * 
     * @param name The fully qualified name of the JavaPackage
     * @return The JavaPackage or null
     */
    public JavaPackage getJavaPackage( String name );

    /**
     * Return all JavaPackages of the current library.
     * It's up to the library to decide if also collects JavaPackages from it's ancestors 
     * 
     * @return  
     */
    public JavaPackage[] getJavaPackages();
}
