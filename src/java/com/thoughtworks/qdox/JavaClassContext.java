package com.thoughtworks.qdox;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * JavaClassContext gives you a mechanism to get a JavaClass.
 * If a class couldn't be found in the cache, the class will be pulled from the classLibrary, the builder will create the corresponding JavaClass and put it in the cache.  
 * 
 * 
 * @author Robert Scholte
 *
 */
public class JavaClassContext implements Serializable {

	
	private Map classMap = new LinkedHashMap();  // <String, com.thoughtworks.qdox.model.JavaClass>
	private Map packageMap = new LinkedHashMap(); // <String, com.thoughtworks.qdox.model.JavaPackage> 
	private Set sourceSet = new LinkedHashSet();  // <com.thoughtworks.qdox.model.JavaSource> 
	
	public JavaClassContext(){
	}
	
	/**
	 * Retrieve the {@link JavaClass} based on the name
	 * 
	 * @param name the fully qualified name of the class
	 * @return the stored {@link JavaClass}, otherwise <code>null</code>
	 */
	public JavaClass getClassByName(String name) {
		return (JavaClass) classMap.get( name );
	}
	
	/**
	 * Remove and return the {@link JavaClass} based on the name
	 * 
	 * @param name the fully qualified name of the class
	 * @return the removed {@link JavaClass}, otherwise <code>null</code> 
	 */
	public JavaClass removeClassByName(String name) {
	    return (JavaClass) classMap.remove( name );
	}
	
	/**
	 * Return all stored JavaClasses
	 * 
	 * @return an array of JavaClasses, never <code>null</code>
	 */
	public JavaClass[] getClasses() {
		return (JavaClass[]) classMap.values().toArray( new JavaClass[0]);
	}
	
	/**
	 * Store this JavaClass based on its fully qualified name
	 * 
	 * @param javaClass the {@link JavaClass} to add
	 */
	public void add(JavaClass javaClass) {
	    classMap.put(javaClass.getFullyQualifiedName(), javaClass);
		
		JavaPackage jPackage = getPackageByName( javaClass.getPackageName() );
		if(jPackage != null) {
		    jPackage.addClass( javaClass );
		}
	}
	
	/**
	 * Retrieve the {@link JavaPackage} based on the name
	 * 
	 * @param name the fully qualified name of the package
	 * @return the stored {@link JavaPackage}, otherwise <code>null</code>
	 */
    public JavaPackage getPackageByName( String name )
    {
        return (JavaPackage) packageMap.get( name );
    }
    
    /**
     * Remove and return the {@link JavaPacakge} based on the name
     * 
     * @param name the fully qualified name of the class
     * @return the removed {@link JavaPackage}, otherwise <code>null</code> 
     */
    public JavaPackage removePackageByName( String name )
    {
        return (JavaPackage) packageMap.remove( name );
    }

    /**
     * A null-safe implementation to store a JavaPackage in this context 
     * 
     * @param jPackage the {@link JavaPackage} to add
     */
    public void add( JavaPackage jPackage )
    {
        if(jPackage != null) {
            String packageName = jPackage.getName();
            JavaPackage javaPackage = getPackageByName( packageName );
            if ( javaPackage == null ) {
                javaPackage = new JavaPackage( packageName );
                javaPackage.setContext( this );
                packageMap.put( packageName, javaPackage );
            }
            jPackage.setContext( this );
        }
    }

    /**
     * Return all stored JavaPackages
     * 
     * @return an array of JavaPackages, never <code>null</code>
     */
    public JavaPackage[] getPackages()
    {
        return (JavaPackage[]) packageMap.values().toArray( new JavaPackage[0] );
        
    }

    /**
     * Store a {@link JavaSource} in this context 
     * 
     * @param source the {@link JavaSource} to add
     */
    public void add( JavaSource source )
    {
        sourceSet.add( source );
    }

    /**
     * Return all stored JavaSources
     * 
     * @return an array of JavaSources, never <code>null</code>
     */
    public JavaSource[] getSources()
    {
        return (JavaSource[]) sourceSet.toArray( new JavaSource[0] );
    }
}
