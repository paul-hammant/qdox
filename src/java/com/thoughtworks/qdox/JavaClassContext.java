package com.thoughtworks.qdox;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
	private Set sourceSet = new HashSet();  // <com.thoughtworks.qdox.model.JavaSource> 
	
	public JavaClassContext(){
	}
	
	public JavaClass getClassByName(String name) {
		JavaClass result = (JavaClass) classMap.get( name );
		return result;
	}
	
	public JavaClass removeClassByName(String name) {
	    return (JavaClass) classMap.remove( name );
	}
	
	public JavaClass[] getClasses() {
		return (JavaClass[]) classMap.values().toArray( new JavaClass[0]);
	}
	public void add(JavaClass javaClass) {
	    classMap.put(javaClass.getFullyQualifiedName(), javaClass);
		
		JavaPackage jPackage = getPackageByName( javaClass.getPackageName() );
		if(jPackage != null) {
		    jPackage.addClass( javaClass );
		}
	}
	
    public JavaPackage getPackageByName( String name )
    {
        return (JavaPackage) packageMap.get( name );
    }
    
    public JavaPackage removePackageByName( String name )
    {
        return (JavaPackage) packageMap.remove( name );
    }

    public void add( JavaPackage jPackage )
    {
        String packageName = jPackage.getName();
        JavaPackage javaPackage = getPackageByName( packageName );
        if ( javaPackage == null ) {
            javaPackage = new JavaPackage( packageName );
            javaPackage.setContext( this );
            packageMap.put( packageName, javaPackage );
        }
        jPackage.setContext( this );
    }


    public JavaPackage[] getPackages()
    {
        return (JavaPackage[]) packageMap.values().toArray( new JavaPackage[0] );
        
    }

    public void add( JavaSource source )
    {
        sourceSet.add( source );
        add(source.getPackage());
        for(int index = 0; index < source.getClasses().length; index++) {
            add(source.getClasses()[index]);
        }
    }

    public JavaSource[] getSources()
    {
        return (JavaSource[]) sourceSet.toArray( new JavaSource[0] );
    }
}
