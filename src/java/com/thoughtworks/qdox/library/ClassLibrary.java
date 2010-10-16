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
     * Check if this library holds a reference based on the name.
     * 
     * @param name the Fully Qualified Name trying to resolve
     * @return true if the classLibrary has a reference
     */
    public boolean hasClassReference( String name );

    /**
     * Get the JavaClass or null if it's not possible
     * 
     * @param name The fully qualified name of the JavaClass
     * @return The JavaClass or null
     */
    public JavaClass getJavaClass( String name );

    /**
     * Return all JavaClasses of the current library.
     * It's up to the library to decide if also collects JavaClasses from it's ancestors 
     * 
     * @return all JavaClasses as array, never <code>null</code>
     */
    public JavaClass[] getJavaClasses();
    
    /**
     * Return all JavaSources of the current library.
     * It's up to the library to decide if also collects JavaSources from it's ancestors 
     * 
     * @return all JavaSources as array, never <code>null</code>
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
     * @return all JavaPackages as array, never <code>null</code>
     */
    public JavaPackage[] getJavaPackages();
}
