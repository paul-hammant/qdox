package com.thoughtworks.qdox;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.thoughtworks.qdox.model.ClassLibrary;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaClassCache;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.util.OrderedMap;

/**
 * JavaClassContext gives you a mechanism to get a JavaClass.
 * If a class couldn't be found in the cache, the class will be pulled from the classLibrary, the builder will create the corresponding JavaClass and put it in the cache.  
 * 
 * 
 * @author Robert Scholte
 *
 */
public class JavaClassContext implements Serializable {

	private ClassLibrary classLibrary;
	private JavaDocBuilder builder;
	private Map classMap = new OrderedMap();  // <String, com.thoughtworks.qdox.model.JavaClass>
	private Map packageMap = new OrderedMap(); // <String, com.thoughtworks.qdox.model.JavaPackage> 
	private List sourceList = new ArrayList();  // <com.thoughtworks.qdox.model.JavaSource> 
	
	public JavaClassContext(){
	}
	
	public JavaClassContext(JavaDocBuilder builder) {
		this.builder = builder;
	}
	
	
	public JavaClassContext(ClassLibrary classLibrary) {
		this.classLibrary = classLibrary;
	}
	
	
	public void setClassLibrary(ClassLibrary classLibrary) {
		this.classLibrary = classLibrary;
	}
	
	/**
	 * temporary, this should be hidden
	 * @return classLibrary
	 * @todo remove
	 */
	public ClassLibrary getClassLibrary() {
		return classLibrary;
	}
	
	
	public JavaClass getClassByName(String name) {
		JavaClass result = (JavaClass) classMap.get( name );
		if(result == null && builder != null) {
			result = builder.createBinaryClass(name);
			
			if ( result == null ) {
			    result = builder.createSourceClass(name);
			}
			if ( result == null ) {
                result = builder.createUnknownClass(name);
			}
			
			if(result != null) {
				add(result);
		        result.setJavaClassContext(this);
			}
		}
		return result;
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
	
	
	public void add(String fullyQualifiedClassName) {
		classLibrary.add(fullyQualifiedClassName);
	}

    public JavaPackage getPackageByName( String name )
    {
        return (JavaPackage) packageMap.get( name );
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
        sourceList.add( source );
    }

    public JavaSource[] getSources()
    {
        return (JavaSource[]) sourceList.toArray( new JavaSource[0] );
    }
}
