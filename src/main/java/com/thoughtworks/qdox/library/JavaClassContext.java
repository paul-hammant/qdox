package com.thoughtworks.qdox.library;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * JavaClassContext gives you a mechanism to get a {@link JavaClass}.
 * If a class couldn't be found in the cache, the class will be pulled from the classLibrary, the builder will create the corresponding JavaClass and put it in the cache.  
 * 
 * 
 * @author Robert Scholte
 *
 */
public class JavaClassContext implements Serializable {

	
	private Map<String, JavaClass> classMap = new LinkedHashMap<String, JavaClass>();
	private Map<String, JavaPackage> packageMap = new LinkedHashMap<String, JavaPackage>();  
	private Set<JavaSource> sourceSet = new LinkedHashSet<JavaSource>(); 
	
	public JavaClassContext(){
	}
	
	/**
	 * Retrieve the {@link JavaClass} based on the {@code name}.
	 * 
	 * @param name the fully qualified name of the class
	 * @return the stored JavaClass, otherwise <code>null</code>
	 */
	public JavaClass getClassByName(String name) 
	{
		return classMap.get( name );
	}
	
	/**
	 * Remove and return the {@link JavaClass} based on the {@code name}.
	 * 
	 * @param name the fully qualified name of the class
	 * @return the removed JavaClass, otherwise <code>null</code> 
	 */
	public JavaClass removeClassByName(String name) 
	{
	    return classMap.remove( name );
	}
	
	/**
	 * Return all stored JavaClasses
	 * 
	 * @return a list of JavaClasses, never <code>null</code>
	 */
	public List<JavaClass> getClasses() {
		return Collections.unmodifiableList( new LinkedList<JavaClass>(classMap.values()) );
	}
	
	/**
	 * Store this {@link JavaClass} based on its fully qualified name
	 * 
	 * @param javaClass the JavaClass to add
	 */
	public void add(JavaClass javaClass) {
	    classMap.put(javaClass.getBinaryName(), javaClass);
	}
	
	/**
	 * Retrieve the {@link JavaPackage} based on the {@code name}.
	 * 
	 * @param name the fully qualified name of the package
	 * @return the stored JavaPackage, otherwise <code>null</code>
	 */
    public JavaPackage getPackageByName( String name )
    {
        return packageMap.get( name );
    }
    
    /**
     * Remove and return the {@link JavaPackage} based on the {@code name}.
     * 
     * @param name the fully qualified name of the class
     * @return the removed JavaPackage, otherwise <code>null</code> 
     */
    public JavaPackage removePackageByName( String name )
    {
        return packageMap.remove( name );
    }

    /**
     * A null-safe implementation to store a {@link JavaPackage} in this context 
     * 
     * @param jPackage the JavaPackage to add
     */
    public void add( JavaPackage jPackage )
    {
        if(jPackage != null) {
            packageMap.put( jPackage.getName(), jPackage );
        }
    }

    /**
     * Return all stored JavaPackages
     * 
     * @return a list of JavaPackages, never <code>null</code>
     */
    public List<JavaPackage> getPackages()
    {
        return Collections.unmodifiableList( new LinkedList<JavaPackage>(packageMap.values()) );
        
    }

    /**
     * Store a {@link JavaSource} in this context 
     * 
     * @param source the JavaSource to add
     */
    public void add( JavaSource source )
    {
        sourceSet.add( source );
    }

    /**
     * Return all stored JavaSources
     * 
     * @return a list of JavaSources, never <code>null</code>
     */
    public List<JavaSource> getSources()
    {
        return Collections.unmodifiableList( new LinkedList<JavaSource>(sourceSet) );
    }
}
