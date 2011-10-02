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

import java.util.LinkedList;
import java.util.List;

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
            result =  Type.resolve( result, originalMethod.getParentClass(), callingClass );
            
            //According to java-specs, if it could be resolved the upper boundary, so Object, should be returned  
            if ( !resolve && !this.getReturns().getFullyQualifiedName().equals( result.getFullyQualifiedName() ) )
            {
                result = new Type( "java.lang.Object", 0, callingClass );
            }
        }
        
        return result;
    }

    public List<JavaType> getParameterTypes( boolean resolve )
    {
        List<JavaType> result = new LinkedList<JavaType>();
        for (JavaType type: originalMethod.getParameterTypes( resolve ) )
        {
            JavaType curType = Type.resolve( type, originalMethod.getParentClass(), callingClass );
            //According to java-specs, if it could be resolved the upper boundary, so Object, should be returned  
            if ( !resolve && !type.getFullyQualifiedName().equals( curType.getFullyQualifiedName() ) )
            {
                result.add(new Type( "java.lang.Object", 0, callingClass ));
            }
            else {
                result.add(curType);
            }
            
        }
        return result;
    }
    
    public boolean equals( Object obj )
    {
        return originalMethod.equals( obj );
    }

    public List<Annotation> getAnnotations()
    {
        return originalMethod.getAnnotations();
    }

    public String getCallSignature()
    {
        return originalMethod.getCallSignature();
    }

    public String getCodeBlock()
    {
        return originalMethod.getCodeBlock();
    }

    public String getComment()
    {
        return originalMethod.getComment();
    }

    public JavaClass getDeclaringClass() {
    	return originalMethod.getDeclaringClass();
    }
    
    public String getDeclarationSignature( boolean withModifiers )
    {
        return originalMethod.getDeclarationSignature( withModifiers );
    }

    public List<JavaClass> getExceptions()
    {
        return originalMethod.getExceptions();
    }

    public JavaClass getGenericReturnType()
    {
        return originalMethod.getGenericReturnType();
    }

    public int getLineNumber()
    {
        return originalMethod.getLineNumber();
    }

    public List<String> getModifiers()
    {
        return originalMethod.getModifiers();
    }

    public String getName()
    {
        return originalMethod.getName();
    }

    public String getNamedParameter( String tagName, String parameterName )
    {
        return originalMethod.getNamedParameter( tagName, parameterName );
    }

    public JavaParameter getParameterByName( String name )
    {
        return originalMethod.getParameterByName( name );
    }

    public List<JavaParameter> getParameters()
    {
        return originalMethod.getParameters();
    }
    
    public JavaClass getParentClass()
    {
        return originalMethod.getParentClass();
    }

    public String getPropertyName()
    {
        return originalMethod.getPropertyName();
    }

    public JavaType getPropertyType()
    {
        return originalMethod.getPropertyType();
    }

    public Type getReturns()
    {
        return originalMethod.getReturns();
    }

    public JavaSource getSource()
    {
        return originalMethod.getSource();
    }

    public String getSourceCode()
    {
        return originalMethod.getSourceCode();
    }

    public DocletTag getTagByName( String name, boolean inherited )
    {
        return originalMethod.getTagByName( name, inherited );
    }

    public DocletTag getTagByName( String name )
    {
        return originalMethod.getTagByName( name );
    }

    public List<DocletTag> getTags()
    {
        return originalMethod.getTags();
    }

    public List<DocletTag> getTagsByName( String name, boolean inherited )
    {
        return originalMethod.getTagsByName( name, inherited );
    }

    public List<DocletTag> getTagsByName( String name )
    {
        return originalMethod.getTagsByName( name );
    }

    public List<TypeVariable<JavaMethod>> getTypeParameters()
    {
        return originalMethod.getTypeParameters();
    }

    public int hashCode()
    {
        return originalMethod.hashCode();
    }

    public boolean isAbstract()
    {
        return originalMethod.isAbstract();
    }

    public boolean isFinal()
    {
        return originalMethod.isFinal();
    }

    public boolean isNative()
    {
        return originalMethod.isNative();
    }

    public boolean isPrivate()
    {
        return originalMethod.isPrivate();
    }

    public boolean isPropertyAccessor()
    {
        return originalMethod.isPropertyAccessor();
    }

    public boolean isPropertyMutator()
    {
        return originalMethod.isPropertyMutator();
    }

    public boolean isProtected()
    {
        return originalMethod.isProtected();
    }

    public boolean isPublic()
    {
        return originalMethod.isPublic();
    }

    public boolean isStatic()
    {
        return originalMethod.isStatic();
    }

    public boolean isStrictfp()
    {
        return originalMethod.isStrictfp();
    }

    public boolean isSynchronized()
    {
        return originalMethod.isSynchronized();
    }

    public boolean isTransient()
    {
        return originalMethod.isTransient();
    }

    public boolean isVarArgs()
    {
        return originalMethod.isVarArgs();
    }

    public boolean isVolatile()
    {
        return originalMethod.isVolatile();
    }

    public boolean signatureMatches( String name, List<JavaType> parameterTypes, boolean varArg )
    {
        return originalMethod.signatureMatches( name, parameterTypes, varArg );
    }

    public boolean signatureMatches( String name, List<JavaType> parameterTypes )
    {
        return originalMethod.signatureMatches( name, parameterTypes );
    }

    public String toString()
    {
        return originalMethod.toString();
    }

    public JavaType getReturnType()
    {
        return getReturnType( false );
    }

    public List<JavaType> getParameterTypes()
    {
        return getParameterTypes( false );
    }
}
