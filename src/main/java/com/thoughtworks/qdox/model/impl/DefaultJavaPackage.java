package com.thoughtworks.qdox.model.impl;

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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaModelUtils;
import com.thoughtworks.qdox.model.JavaPackage;

/**
 * The default implementation of {@link JavaPackage}, representing a {@link Package}.
 * 
 * @since 1.9
 */
public class DefaultJavaPackage extends AbstractBaseJavaEntity implements JavaPackage {

    private ClassLibrary classLibrary;
	private String name;
	private List<JavaClass> classes = new LinkedList<JavaClass>();

	/**
	 * 
	 * @param name the name of the package, should never be <code>null</code>
	 */
    public DefaultJavaPackage(String name) {
		this.name= name;
    }

    /** {@inheritDoc} */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    /** {@inheritDoc} */
	public String getCodeBlock() {
		return getModelWriter().writePackage(this).toString();
	}
	
	public void setClassLibrary( ClassLibrary classLibrary )
    {
        this.classLibrary = classLibrary;
    }
	
    /** {@inheritDoc} */
	public ClassLibrary getJavaClassLibrary()
    {
        return classLibrary;
    }

	public void addClass(JavaClass clazz) {
		classes.add(clazz);
	}

    /** {@inheritDoc} */
	public Collection<JavaClass> getClasses() {
	    //avoid infinitive  recursion
	    if (this == classLibrary.getJavaPackage( name )) {
	        return classes;
	    }
	    else {
	        return classLibrary.getJavaPackage( name ).getClasses();
	    }
	}
	
    /** {@inheritDoc} */
	public JavaClass getClassByName(String name) 
    {
        JavaClass result = null;
        
        for ( JavaClass candidateCls : classes )
        {
            result = JavaModelUtils.getClassByName( candidateCls, name );
            if ( result != null ) 
            {
                result = candidateCls;
                break;
            }
        }
        return result;
    }
	
    /** {@inheritDoc} */
    public JavaPackage getParentPackage()
    {
        String parentName = name.substring( 0, name.lastIndexOf( '.' ) );
        return classLibrary.getJavaPackage( parentName );
    }

    /** {@inheritDoc} */
    public List<JavaPackage> getSubPackages() {
        String expected = name + ".";
        Collection<JavaPackage> jPackages = classLibrary.getJavaPackages();
        List<JavaPackage> retList = new LinkedList<JavaPackage>();
        for (JavaPackage jPackage : jPackages) {
            String pName = jPackage.getName();
            if (pName.startsWith(expected) && !(pName.substring(expected.length()).indexOf(".") > -1)) {
                retList.add(classLibrary.getJavaPackage( pName ));
            }
        }
        return retList;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof JavaPackage ) )
        {
            return false;
        }

        JavaPackage that = (JavaPackage) o;

        return ( name.equals( that.getName() ) );
    }

    @Override
    public int hashCode() {
        return 11 + name.hashCode();
    }

    @Override
    public String toString() {
    	return "package " + name;
    }
}
