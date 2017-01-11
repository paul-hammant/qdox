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

import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaModelUtils;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.writer.ModelWriter;
import com.thoughtworks.qdox.writer.ModelWriterFactory;
import com.thoughtworks.qdox.writer.impl.DefaultModelWriter;

public class DefaultJavaSource implements JavaSource, Serializable {

    private final ClassLibrary classLibrary;
    private ModelWriterFactory modelWriterFactory;
    
    private JavaPackage pkg;
    private final List<String> imports = new LinkedList<String>();
    private List<JavaClass> classes = new LinkedList<JavaClass>();
    private URL url;

    /**
     * Default constructor for the Default JavaSource 
     * 
     * @param classLibrary the classLibrary, should not be <code>null</code>
     */
    public DefaultJavaSource( ClassLibrary classLibrary )
    {
        this.classLibrary = classLibrary;
    }

    /**
     * @param url the URL of the source file
     * @since 1.4
     */
    public void setURL(URL url) {
        this.url = url;
    }

    /**  {@inheritDoc} */
    public URL getURL() {
        return url;
    }

    /**  {@inheritDoc} */
    public JavaPackage getPackage() {
        return pkg;
    }

    public void setPackage(JavaPackage pkg) {
        this.pkg = pkg;
    }

    public void addImport(String imp) {
        imports.add(imp);
    }

    /**  {@inheritDoc} */
    public List<String> getImports() {
        return imports;
    }

    public void addClass(JavaClass cls) {
        classes.add(cls);
    }

    /** {@inheritDoc} */
    public List<JavaClass> getClasses() {
      return Collections.unmodifiableList( classes );
    }

    /** {@inheritDoc} */
    public String getCodeBlock() {
        return getModelWriter().writeSource( this ).toString();
    }
    
    @Override
    public String toString() {
    	return getCodeBlock();
    }

    /**  {@inheritDoc} */
    public String getClassNamePrefix() {
        return ( pkg == null ? "" : pkg.getName() + '.' ); 
    }

    /**  {@inheritDoc} */
    public JavaClass getNestedClassByName(String name) {
        JavaClass result = null;
        
        for (JavaClass candidateCls : classes) {
            if (candidateCls.getName().equals(name)) {
                result = candidateCls;
                break;
            }
        }
        return result;
    }
    
    /**  {@inheritDoc} */
    public JavaClass getClassByName(String name) 
    {
        JavaClass result = null;
        
        for ( JavaClass candidateCls : classes )
        {
            result = JavaModelUtils.getClassByName( candidateCls, name );
            if ( result != null ) 
            {
                break;
            }
        }
        return result;
    }
    
    /**  {@inheritDoc} */
	public ClassLibrary getJavaClassLibrary()
	{
	    return classLibrary;
	}

    /**  {@inheritDoc} */
    public String getPackageName()
    {
        return ( pkg == null ? "" : pkg.getName() );
    }
    
    /**
     * 
     * @param modelWriterFactory the modelWriterFactory
     * @since 2.0
     */
    public void setModelWriterFactory( ModelWriterFactory modelWriterFactory )
    {
        this.modelWriterFactory = modelWriterFactory;
    }
    
    private ModelWriter getModelWriter()
    {
        ModelWriter result;
        if ( modelWriterFactory != null )
        {
            result = modelWriterFactory.newInstance();
        }
        else
        {
            result = new DefaultModelWriter();
        }
        return result;
    }
}