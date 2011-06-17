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
    boolean isInterface();

    /**
     * is enum?  (otherwise class or interface)
     */
    boolean isEnum();

    /**
     * (don't know if this is required)
     * 
     * @return
     * @since 2.0 
     */
    boolean isAnnotation();

    Type getSuperClass();

    /**
     * Shorthand for getSuperClass().getJavaClass() with null checking.
     */
    JavaClass getSuperJavaClass();

    List<Type> getImplements();

    /**
     * @since 1.3
     */
    List<JavaClass> getImplementedInterfaces();

    String getCodeBlock();

    List<TypeVariable> getTypeParameters();

    JavaSource getParentSource();

    JavaPackage getPackage();

    JavaClassParent getParent();

    /**
     * If this class has a package, the packagename will be returned.
     * Otherwise an empty String.
     * 
     * @return
     */
    String getPackageName();

    String getFullyQualifiedName();
    
    String getGenericFullyQualifiedName();

    /**
     * @since 1.3
     */
    boolean isInner();

    String resolveType( String typeName );

    String getClassNamePrefix();

    @Deprecated
    Type asType();

    List<JavaMethod> getMethods();
    
    /**
     * 
     * @return the list of constructors
     * @since 2.0
     */
    List<JavaConstructor> getConstructors();
    
    
    /**
     * 
     * @param parameterTypes
     * @return the constructor matching the parameterTypes, otherwise <code>null</code>
     * @since 2.0
     */
    JavaConstructor getConstructor(List<Type> parameterTypes);
    
    /**
     * 
     * @param parameterTypes
     * @param varArg
     * @return the constructor matching the parameterTypes and the varArg, otherwise <code>null</code>
     * @since 2.0
     */
    JavaConstructor getConstructor(List<Type> parameterTypes, boolean varArg);
    

    /**
     * @since 1.3
     */
    List<JavaMethod> getMethods( boolean superclasses );

    /**
     * 
     * @param name           method name
     * @param parameterTypes parameter types or null if there are no parameters.
     * @return the matching method or null if no match is found.
     */
    JavaMethod getMethodBySignature( String name, List<Type> parameterTypes );

    /**
     * This should be the signature for getMethodBySignature
     * 
     * @param name
     * @param parameterTypes
     * @param varArgs
     * @return
     */
    JavaMethod getMethod( String name, List<Type> parameterTypes, boolean varArgs );

    /**
     * 
     * @param name
     * @param parameterTypes
     * @param superclasses
     * @return
     */
    JavaMethod getMethodBySignature( String name, List<Type> parameterTypes, boolean superclasses );

    /**
     * 
     * @param name
     * @param parameterTypes
     * @param superclasses
     * @param varArg
     * @return
     */
    JavaMethod getMethodBySignature( String name, List<Type> parameterTypes, boolean superclasses, boolean varArg );

    /**
     * 
     * @param name
     * @param parameterTypes
     * @param superclasses
     * @return
     */
    List<JavaMethod> getMethodsBySignature( String name, List<Type> parameterTypes, boolean superclasses );

    /**
     * 
     * @param name
     * @param parameterTypes
     * @param superclasses
     * @param varArg
     * @return
     */
    List<JavaMethod> getMethodsBySignature( String name, List<Type> parameterTypes, boolean superclasses,
                                                   boolean varArg );

    List<JavaField> getFields();

    JavaField getFieldByName( String name );

    /**
     * @deprecated Use {@link #getNestedClasses()} instead.
     */
    List<JavaClass> getClasses();

    /**
     * @since 1.3
     */
    List<JavaClass> getNestedClasses();

    JavaClass getNestedClassByName( String name );

    /**
     * @since 1.3
     */
    boolean isA( String fullClassName );

    /**
     * @since 1.3
     */
    boolean isA( JavaClass javaClass );

    /**
     * Gets bean properties without looking in superclasses or interfaces.
     *
     * @since 1.3
     */
    List<BeanProperty> getBeanProperties();

    /**
     * @since 1.3
     */
    List<BeanProperty> getBeanProperties( boolean superclasses );

    /**
     * Gets bean property without looking in superclasses or interfaces.
     *
     * @since 1.3
     */
    BeanProperty getBeanProperty( String propertyName );

    /**
     * @since 1.3
     */
    BeanProperty getBeanProperty( String propertyName, boolean superclasses );

    /**
     * Gets the known derived classes. That is, subclasses or implementing classes.
     */
    List<JavaClass> getDerivedClasses();

    List<DocletTag> getTagsByName( String name, boolean superclasses );

    ClassLibrary getJavaClassLibrary();

    String getName();
    
    /**
     * If there's a reference to this class, use the value used in the code. Otherwise return the simple name.
     * When including all imports, you should be safe to use this method.
     * This won't return generics, so it's java1.4 safe.  
     * 
     * @return
     */
    String getValue();
    
    /**
     * A java5+ representation of the class.
     * When including all imports, you should be safe to use this method.
     * 
     * @return
     */
    String getGenericValue();
    
    List<String> getModifiers();
    
    /**
     * Return <code>true</code> if the class includes the public modifier, <code>false</code> otherwise.
     * 
     * @return <code>true</code> if class the public modifier; <code>false</code> otherwise.
     */
    boolean isPublic();
    
    /**
     * Return <code>true</code> if the class includes the protected modifier, <code>false</code> otherwise.
     * 
     * @return <code>true</code> if class the protected modifier; <code>false</code> otherwise.
     */
    boolean isProtected();
    
    /**
     * Return <code>true</code> if the class includes the private modifier, <code>false</code> otherwise.
     * 
     * @return <code>true</code> if class the private modifier; <code>false</code> otherwise.
     */
    boolean isPrivate();
    
    /**
     * Return <code>true</code> if the class includes the final modifier, <code>false</code> otherwise.
     * 
     * @return <code>true</code> if class the final modifier; <code>false</code> otherwise.
     */
    boolean isFinal();
    
    /**
     * Return <code>true</code> if the class includes the static modifier, <code>false</code> otherwise.
     * 
     * @return <code>true</code> if class the static modifier; <code>false</code> otherwise.
     */
    boolean isStatic();
    
    /**
     * Return <code>true</code> if the class includes the abstract modifier, <code>false</code> otherwise.
     * 
     * @return <code>true</code> if class the abstract modifier; <code>false</code> otherwise.
     */
    boolean isAbstract();
    
    boolean isPrimitive();
    
    /**
     * (API description of java.lang.Class.toString())
     * 
     * Converts the object to a string. 
     * The string representation is the string "class" or "interface", followed by a space, and then by the fully qualified name of the class in the format returned by <code>getName</code>. 
     * If this <code>Class</code> object represents a primitive type, this method returns the name of the primitive type. 
     * If this <code>Class</code> object represents void this method returns "void".
     *  
     * @return a string representation of this class object.
     */
    @Override
    String toString();
}