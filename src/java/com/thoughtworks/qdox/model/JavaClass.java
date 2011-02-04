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

import java.util.List;

import com.thoughtworks.qdox.library.ClassLibrary;

/**
 * Modeled equivalent of {@link Class}, providing the most important methods.
 * Where the original Class is using an Array, this model is using a List.
 * 
 * @author Robert Scholte
 */
public interface JavaClass extends JavaModel, JavaClassParent, JavaAnnotatedElement, JavaGenericDeclaration
{

    /**
     * is interface?  (otherwise enum or class)
     */
    public boolean isInterface();

    /**
     * is enum?  (otherwise class or interface)
     */
    public boolean isEnum();

    /**
     * (don't know if this is required)
     * 
     * @return
     * @since 2.0 
     */
    public boolean isAnnotation();

    public Type getSuperClass();

    /**
     * Shorthand for getSuperClass().getJavaClass() with null checking.
     */
    public JavaClass getSuperJavaClass();

    public List<Type> getImplements();

    /**
     * @since 1.3
     */
    public List<JavaClass> getImplementedInterfaces();

    public String getCodeBlock();

    public List<TypeVariable> getTypeParameters();

    public JavaSource getParentSource();

    public JavaSource getSource();

    public JavaPackage getPackage();

    public JavaClassParent getParent();

    /**
     * If this class has a package, the packagename will be returned.
     * Otherwise an empty String.
     * 
     * @return
     */
    public String getPackageName();

    public String getFullyQualifiedName();

    /**
     * @since 1.3
     */
    public boolean isInner();

    public String resolveType( String typeName );

    public String getClassNamePrefix();

    @Deprecated
    public Type asType();

    public List<JavaMethod> getMethods();

    /**
     * @since 1.3
     */
    public List<JavaMethod> getMethods( boolean superclasses );

    /**
     * 
     * @param name           method name
     * @param parameterTypes parameter types or null if there are no parameters.
     * @return the matching method or null if no match is found.
     */
    public JavaMethod getMethodBySignature( String name, List<Type> parameterTypes );

    /**
     * This should be the signature for getMethodBySignature
     * 
     * @param name
     * @param parameterTypes
     * @param varArgs
     * @return
     */
    public JavaMethod getMethod( String name, List<Type> parameterTypes, boolean varArgs );

    /**
     * 
     * @param name
     * @param parameterTypes
     * @param superclasses
     * @return
     */
    public JavaMethod getMethodBySignature( String name, List<Type> parameterTypes, boolean superclasses );

    /**
     * 
     * @param name
     * @param parameterTypes
     * @param superclasses
     * @param varArg
     * @return
     */
    public JavaMethod getMethodBySignature( String name, List<Type> parameterTypes, boolean superclasses, boolean varArg );

    /**
     * 
     * @param name
     * @param parameterTypes
     * @param superclasses
     * @return
     */
    public List<JavaMethod> getMethodsBySignature( String name, List<Type> parameterTypes, boolean superclasses );

    /**
     * 
     * @param name
     * @param parameterTypes
     * @param superclasses
     * @param varArg
     * @return
     */
    public List<JavaMethod> getMethodsBySignature( String name, List<Type> parameterTypes, boolean superclasses,
                                                   boolean varArg );

    public List<JavaField> getFields();

    public JavaField getFieldByName( String name );

    /**
     * @deprecated Use {@link #getNestedClasses()} instead.
     */
    public List<JavaClass> getClasses();

    /**
     * @since 1.3
     */
    public List<JavaClass> getNestedClasses();

    public JavaClass getNestedClassByName( String name );

    /**
     * @since 1.3
     */
    public boolean isA( String fullClassName );

    /**
     * @since 1.3
     */
    public boolean isA( JavaClass javaClass );

    /**
     * Gets bean properties without looking in superclasses or interfaces.
     *
     * @since 1.3
     */
    public List<BeanProperty> getBeanProperties();

    /**
     * @since 1.3
     */
    public List<BeanProperty> getBeanProperties( boolean superclasses );

    /**
     * Gets bean property without looking in superclasses or interfaces.
     *
     * @since 1.3
     */
    public BeanProperty getBeanProperty( String propertyName );

    /**
     * @since 1.3
     */
    public BeanProperty getBeanProperty( String propertyName, boolean superclasses );

    /**
     * Gets the known derived classes. That is, subclasses or implementing classes.
     */
    public List<JavaClass> getDerivedClasses();

    public List<DocletTag> getTagsByName( String name, boolean superclasses );

    public int compareTo( Object o );

    public ClassLibrary getJavaClassLibrary();

    public String getName();
    
    public List<String> getModifiers();
    
    public boolean isPublic();
    
    public boolean isProtected();
    
    public boolean isPrivate();
    
    public boolean isFinal();
    
    public boolean isStatic();
    
    public boolean isAbstract();
    
    public boolean isPrimitive();
}