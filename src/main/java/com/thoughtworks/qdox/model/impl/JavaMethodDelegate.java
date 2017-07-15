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

import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.JavaTypeVariable;
import com.thoughtworks.qdox.type.TypeResolver;

/**
 * This class can be used to access overridden methods while keeping a reference to the original class.
 * This is especially useful when trying to resolve generics
 * 
 * @author Robert Scholte
 * @since 1.12
 */
public class JavaMethodDelegate implements JavaMethod
{

    private JavaClass callingClass;
    private JavaMethod originalMethod;
    
    public JavaMethodDelegate( JavaClass callingClass, JavaMethod originalMethod )
    {
        this.callingClass = callingClass;
        this.originalMethod = originalMethod;
    }
    
    public JavaType getReturnType( boolean resolve )
    {
        JavaType result = originalMethod.getReturnType( resolve );
        
        if (result != null) {
            String originalValue = result.getValue();
            
            result =  DefaultJavaType.resolve( result, originalMethod.getDeclaringClass(), callingClass );
            
            //According to java-specs, if it could be resolved the upper boundary, so Object, should be returned  
            if ( !resolve && !this.getReturns().getFullyQualifiedName().equals( result.getFullyQualifiedName() ) )
            {
                TypeResolver typeResolver =
                    TypeResolver.byClassName( callingClass.getBinaryName(), callingClass.getJavaClassLibrary(),
                                              callingClass.getSource().getImports() );
                result = new DefaultJavaType( "java.lang.Object", originalValue, 0, typeResolver );
            }
        }
        
        return result;
    }

    /** {@inheritDoc} */
    public List<JavaType> getParameterTypes( boolean resolve )
    {
        List<JavaType> result = new LinkedList<JavaType>();
        for ( JavaType type : originalMethod.getParameterTypes( resolve ) )
        {
            JavaType curType = DefaultJavaType.resolve( type, originalMethod.getDeclaringClass(), callingClass );
            // According to java-specs, if it could be resolved the upper boundary, so Object, should be returned
            if ( !resolve && !type.getFullyQualifiedName().equals( curType.getFullyQualifiedName() ) )
            {
                result.add( new DefaultJavaType( "java.lang.Object", 0 ) );
            }
            else
            {
                result.add( curType );
            }

        }
        return result;
    }
    
    @Override
    public boolean equals( Object obj )
    {
        return originalMethod.equals( obj );
    }

    /** {@inheritDoc} */
    public List<JavaAnnotation> getAnnotations()
    {
        return originalMethod.getAnnotations();
    }

    /** {@inheritDoc} */
    public String getCallSignature()
    {
        return originalMethod.getCallSignature();
    }

    /** {@inheritDoc} */
    public String getCodeBlock()
    {
        return originalMethod.getCodeBlock();
    }

    /** {@inheritDoc} */
    public String getComment()
    {
        return originalMethod.getComment();
    }

    /** {@inheritDoc} */
    public JavaClass getDeclaringClass() {
    	return originalMethod.getDeclaringClass();
    }
    
    /** {@inheritDoc} */
    public String getDeclarationSignature( boolean withModifiers )
    {
        return originalMethod.getDeclarationSignature( withModifiers );
    }

    /** {@inheritDoc} */
    public List<JavaClass> getExceptions()
    {
        return originalMethod.getExceptions();
    }
    
    /** {@inheritDoc} */
    public List<JavaType> getExceptionTypes()
    {
        return originalMethod.getExceptionTypes();
    }
    
    /** {@inheritDoc} */
    public boolean isDefault()
    {
        return originalMethod.isDefault();
    }

    /** {@inheritDoc} */
    public int getLineNumber()
    {
        return originalMethod.getLineNumber();
    }

    /** {@inheritDoc} */
    public List<String> getModifiers()
    {
        return originalMethod.getModifiers();
    }

    /** {@inheritDoc} */
    public String getName()
    {
        return originalMethod.getName();
    }

    /** {@inheritDoc} */
    public String getNamedParameter( String tagName, String parameterName )
    {
        return originalMethod.getNamedParameter( tagName, parameterName );
    }

    /** {@inheritDoc} */
    public JavaParameter getParameterByName( String name )
    {
        return originalMethod.getParameterByName( name );
    }

    /** {@inheritDoc} */
    public List<JavaParameter> getParameters()
    {
        return originalMethod.getParameters();
    }
    
    /** {@inheritDoc} */
    public String getPropertyName()
    {
        return originalMethod.getPropertyName();
    }

    /** {@inheritDoc} */
    public JavaType getPropertyType()
    {
        return originalMethod.getPropertyType();
    }

    /** {@inheritDoc} */
    public JavaClass getReturns()
    {
        return originalMethod.getReturns();
    }

    /** {@inheritDoc} */
    public String getSourceCode()
    {
        return originalMethod.getSourceCode();
    }

    /** {@inheritDoc} */
    public DocletTag getTagByName( String name, boolean inherited )
    {
        return originalMethod.getTagByName( name, inherited );
    }

    /** {@inheritDoc} */
    public DocletTag getTagByName( String name )
    {
        return originalMethod.getTagByName( name );
    }

    /** {@inheritDoc} */
    public List<DocletTag> getTags()
    {
        return originalMethod.getTags();
    }

    /** {@inheritDoc} */
    public List<DocletTag> getTagsByName( String name, boolean inherited )
    {
        return originalMethod.getTagsByName( name, inherited );
    }

    /** {@inheritDoc} */
    public List<DocletTag> getTagsByName( String name )
    {
        return originalMethod.getTagsByName( name );
    }

    /** {@inheritDoc} */
    public List<JavaTypeVariable<JavaMethod>> getTypeParameters()
    {
        return originalMethod.getTypeParameters();
    }

    @Override
    public int hashCode()
    {
        return originalMethod.hashCode();
    }

    /** {@inheritDoc} */
    public boolean isAbstract()
    {
        return originalMethod.isAbstract();
    }

    /** {@inheritDoc} */
    public boolean isFinal()
    {
        return originalMethod.isFinal();
    }

    /** {@inheritDoc} */
    public boolean isNative()
    {
        return originalMethod.isNative();
    }

    /** {@inheritDoc} */
    public boolean isPrivate()
    {
        return originalMethod.isPrivate();
    }

    /** {@inheritDoc} */
    public boolean isPropertyAccessor()
    {
        return originalMethod.isPropertyAccessor();
    }

    /** {@inheritDoc} */
    public boolean isPropertyMutator()
    {
        return originalMethod.isPropertyMutator();
    }

    /** {@inheritDoc} */
    public boolean isProtected()
    {
        return originalMethod.isProtected();
    }

    /** {@inheritDoc} */
    public boolean isPublic()
    {
        return originalMethod.isPublic();
    }

    /** {@inheritDoc} */
    public boolean isStatic()
    {
        return originalMethod.isStatic();
    }

    /** {@inheritDoc} */
    public boolean isStrictfp()
    {
        return originalMethod.isStrictfp();
    }

    /** {@inheritDoc} */
    public boolean isSynchronized()
    {
        return originalMethod.isSynchronized();
    }

    /** {@inheritDoc} */
    public boolean isTransient()
    {
        return originalMethod.isTransient();
    }

    /** {@inheritDoc} */
    public boolean isVarArgs()
    {
        return originalMethod.isVarArgs();
    }

    /** {@inheritDoc} */
    public boolean isVolatile()
    {
        return originalMethod.isVolatile();
    }

    /** {@inheritDoc} */
    public boolean signatureMatches( String name, List<JavaType> parameterTypes, boolean varArg )
    {
        return originalMethod.signatureMatches( name, parameterTypes, varArg );
    }

    /** {@inheritDoc} */
    public boolean signatureMatches( String name, List<JavaType> parameterTypes )
    {
        return originalMethod.signatureMatches( name, parameterTypes );
    }

    /** {@inheritDoc} */
    @Override
	public String toString()
    {
        return originalMethod.toString();
    }

    /** {@inheritDoc} */
    public JavaType getReturnType()
    {
        return getReturnType( false );
    }

    /** {@inheritDoc} */
    public List<JavaType> getParameterTypes()
    {
        return getParameterTypes( false );
    }
}
