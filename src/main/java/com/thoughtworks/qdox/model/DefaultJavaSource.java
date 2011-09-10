package com.thoughtworks.qdox.model;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.writer.DefaultModelWriter;
import com.thoughtworks.qdox.writer.ModelWriter;
import com.thoughtworks.qdox.writer.ModelWriterFactory;

public class DefaultJavaSource implements JavaSource, Serializable {

    private static final Set<String> PRIMITIVE_TYPES = new HashSet<String>();

    static {
        PRIMITIVE_TYPES.add("boolean");
        PRIMITIVE_TYPES.add("byte");
        PRIMITIVE_TYPES.add("char");
        PRIMITIVE_TYPES.add("double");
        PRIMITIVE_TYPES.add("float");
        PRIMITIVE_TYPES.add("int");
        PRIMITIVE_TYPES.add("long");
        PRIMITIVE_TYPES.add("short");
        PRIMITIVE_TYPES.add("void");
    }

    private final ClassLibrary classLibrary;
    private ModelWriterFactory modelWriterFactory;
    
    private JavaPackage pkg;
    private List<String> imports = new LinkedList<String>();
    private List<JavaClass> classes = new LinkedList<JavaClass>();
    private Map<String, String> resolvedTypeCache = new HashMap<String, String>();
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
     * @since 1.4
     */
    public void setURL(URL url) {
        this.url = url;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaSource#getURL()
     */
    public URL getURL() {
        return url;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaSource#getPackage()
     */
    public JavaPackage getPackage() {
        return pkg;
    }

    public void setPackage(JavaPackage pkg) {
        this.pkg = pkg;
    }

    public void addImport(String imp) {
        imports.add(imp);
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaSource#getImports()
     */
    public List<String> getImports() {
        return imports;
    }

    public void addClass(JavaClass cls) {
        classes.add(cls);
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaSource#getClasses()
     */
    public List<JavaClass> getClasses() {
      return Collections.unmodifiableList( classes );
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaSource#getCodeBlock()
     */
    public String getCodeBlock() {
        return getModelWriter().writeSource( this ).toString();
    }
    
    public String toString() {
    	return getCodeBlock();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaSource#resolveType(java.lang.String)
     */
    public String resolveType( String typeName )
    {
        return resolveFullyQualifiedName( typeName );
    }
    
    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClassParent#resolveFullyQualifiedName(java.lang.String)
     */
    public String resolveFullyQualifiedName( String name )
    {
        String result = resolvedTypeCache.get( name );
        if ( result == null )
        {
            result = resolveTypeInternal( name );
            if ( result != null )
            {
                resolvedTypeCache.put( name, result );
            }
        }
        return result;
    }
    
    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClassParent#resolveCanonicalName(java.lang.String)
     */
    public String resolveCanonicalName( String name )
    {
        String className = resolveFullyQualifiedName( name );
        String result = null;
        if ( className != null )
        {
            result = className.replace( '$', '.' );
        }
        return result;
    }
    
    /**
     * Resolves a type name
     * <p>
     * Follows the <a href="http://java.sun.com/docs/books/jls/third_edition/html/packages.html#7.5.1">
     * Java Language Specification, Version 3.0</a>.
     * <p>
     * Current resolution order is:
     * <ol>
     * <li>Single-Type-Import Declaration</li>
     * <li>Type-Import-on-Demand Declaration</li>
     * <li>Automatic Imports</li>
     * </ol>
     * 
     * @param typeName the name to resolve
     * @return the resolved type name, otherwise <code>null</code>
     */
    private String resolveTypeInternal(String typeName) {
        String resolvedName = null;

        lookup : {
            // primitive types
            if(PRIMITIVE_TYPES.contains( typeName )) {
                resolvedName = typeName;
                break lookup;
            }

            String outerName = typeName;
            String nestedName = typeName.replace('.', '$');
            int dotpos = typeName.indexOf( '.' );

            if(dotpos >= 0) {
                outerName = typeName.substring( 0, dotpos );
            }
            
            // Check single-type-import with fully qualified name
            resolvedName = resolveImportedType( typeName, nestedName, true );
                    
            if(resolvedName != null) {
                break lookup;
            }
            
            // Check single-type-import with outer name
            resolvedName = resolveImportedType( outerName, nestedName, false );
            
            if(resolvedName != null) {
                break lookup;
            }
            
            // check for class in the same package
            if (getPackage() != null) {
                resolvedName = resolveFullyQualifiedType( getPackageName() + '.' + typeName );
                
                if(resolvedName != null) {
                    break lookup;
                }
            }

            // check for a class globally
            resolvedName = resolveFullyQualifiedType( typeName );
            
            if(resolvedName != null) {
                break lookup;
            }

            // check for a class in the same package
            resolvedName = resolveFromLibrary( getClassNamePrefix() + nestedName );
            if(resolvedName != null) {
                break lookup;
            }
            
            // try java.lang.*
            resolvedName = resolveFromLibrary( "java.lang." + nestedName );
            if(resolvedName != null) {
                break lookup;
            }
            
            // Check type-import-on-demand
            resolvedName = resolveImportedType( "*", nestedName, false );

            if(resolvedName != null) {
                break lookup;
            }
        }
        
        return resolvedName;
    }
    
    private String resolveImportedType( String importSpec, String typeName, boolean fullMatch ) {
        String resolvedName = null;
        String dotSuffix = "." + importSpec;
            
        for (String imprt : getImports()) {
            //static imports can refer to inner classes
            if( imprt.startsWith( "static " ) ) 
            {
                imprt = imprt.substring( 7 );
            }
            if (imprt.equals(importSpec) || (!fullMatch && imprt.endsWith(dotSuffix))) {
                String candidateName = imprt.substring( 0, imprt.length() - importSpec.length()) + typeName;
                resolvedName = resolveFullyQualifiedType( candidateName );
                if(resolvedName == null && !"*".equals(importSpec)) {
                	resolvedName = candidateName;
                }
                if(resolvedName != null) {
                    break;
                }
            } 
        }
        
        return resolvedName;
    }
    
    private String resolveFromLibrary(String typeName) {
        return classLibrary.hasClassReference( typeName ) ? typeName : null;
    }
    
    private String resolveFullyQualifiedType(String typeName) {
        int indexOfLastDot = typeName.lastIndexOf('.');
        
        if (indexOfLastDot >= 0) {
            String root = typeName.substring(0,indexOfLastDot);
            String leaf = typeName.substring(indexOfLastDot+1);
            String resolvedTypeName = resolveFullyQualifiedType(root + '$' + leaf);
            
            if(resolvedTypeName != null) {
                return resolvedTypeName;
            }
        }

        if( classLibrary.hasClassReference( typeName )) 
        {
            return typeName;
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaSource#getClassNamePrefix()
     */
    public String getClassNamePrefix() {
        return ( pkg == null ? "" : pkg.getName() + '.' ); 
    }

    public JavaSource getParentSource() {
        return this;
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaSource#getNestedClassByName(java.lang.String)
     */
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
    
	/* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaSource#getJavaClassLibrary()
     */
	public ClassLibrary getJavaClassLibrary()
	{
	    return classLibrary;
	}

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaSource#getPackageName()
     */
    public String getPackageName()
    {
        return (pkg == null ? "" : pkg.getName());
    }
    
    /**
     * 
     * @param modelWriterFactory
     * @since 2.0
     */
    public void setModelWriterFactory( ModelWriterFactory modelWriterFactory )
    {
        this.modelWriterFactory = modelWriterFactory;
    }
    
    private ModelWriter getModelWriter()
    {
        ModelWriter result; 
        if (modelWriterFactory != null) {
            result = modelWriterFactory.newInstance();
        }
        else {
            result = new DefaultModelWriter();
        }
        return result;
    }

}
