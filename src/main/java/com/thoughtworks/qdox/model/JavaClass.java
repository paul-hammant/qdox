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

import java.lang.reflect.Modifier;
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
     * (API description of {@link java.lang.Class#isInterface()})
     * <p>
     * Determines if the specified <code>Class</code> object represents an interface type.
     * </p>
     * 
     * @return <code>true</code> if this object represents an interface, otherwise <code>false</code>
     */
    boolean isInterface();

    /**
     * (API description of {@link java.lang.Class#isEnum()})
     * <p>
     * Returns <code>true</code> if and only if this class was declared as an enum in the source code.
     * </p>
     * 
     * @return <code>true</code> if this object represents an enum, otherwise <code>false</code>

     */
    boolean isEnum();

    /**
     * (API description of {@link java.lang.Class#isAnnotation()})
     * <p>Returns true if this <code>Class</code> object represents an annotation type. 
     *    Note that if this method returns true, {@link #isInterface()} would also return true, as all annotation types are also interfaces.
     * </p>
     * 
     * @return <code>true</code> if this object represents an annotation, otherwise <code>false</code>
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
     * @return the name of the package, otherwise an empty String
     */
    String getPackageName();

    String getFullyQualifiedName();
    
    /**
     * @since 1.3
     * @return <code>true</code> if this class is an inner class, otherwise <code>false</code>
     */
    boolean isInner();

    /**
     * Tries to return the fully qualified name based on the name.
     * The name tries to match the following:
     * <ul>
     *   <li>primitives or void</li>
     *   <li>java.lang.*</li>
     *   <li>inner classes</li>
     *   <li>explicit imports</li>
     *   <li>implicit imports</li>
     * </ul> 
     * 
     * @return the resolved name, otherwise <code>null</code>.
     */
    String resolveType( String name );
    
    /**
     * The name can be both absolute (including the package) or relative (matching a subclass or an import).
     * 
     * @param name
     * @return
     */
    String resolveCanonicalName( String name );
    
    /**
     * The name can be both absolute (including the package) or relative (matching a subclass or an import).
     * 
     * @param name the name to resolve
     * @return the resolved fully qualified name, otherwise <code>null</code> 
     */
    String resolveFullyQualifiedName( String name );

    /**
     * If this class has a package, it will return the package name, followed by a "."(dot).
     * Otherwise it will return an empty String
     * 
     * @return the package name plus a dot if there's a package, otherwise an empty String
     */
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
     * Equivalent of {@link Class#getDeclaredClasses()}
     * 
     * @return a list of declared classes, never <code>null</code>
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
     * Equivalent of {@link Class#getClasses()}
     * Gets the known derived classes. That is, subclasses or implementing classes.
     */
    List<JavaClass> getDerivedClasses();

    List<DocletTag> getTagsByName( String name, boolean superclasses );

    ClassLibrary getJavaClassLibrary();

    /**
     * Equivalent of {@link java.lang.Class#getName()}.
     * 
     * @return the fully qualified name of the class
     */
    String getName();
    
    /**
     * Equivalent of (@link {@link java.lang.Class#getCanonicalName()}.
     * 
     * @return the canonical name of this class
     */
    String getCanonicalName();
    
    /**
     * If there's a reference to this class, use the value used in the code. Otherwise return the simple name.
     * When including all imports, you should be safe to use this method.
     * This won't return generics, so it's java1.4 safe.
     * 
     * Examples:
     * <pre>
     *  private String fieldA;           // getValue() will return "String"
     *  private java.lang.String fieldA; // getValue() will return "java.lang.String"
     * </pre>
     * 
     * @return the name of the class as used in the source source
     */
    String getValue();
    
    /**
     * A java5+ representation of the class.
     * When including all imports, you should be safe to use this method.
     * 
     * @return
     */
    String getGenericValue();
    
    /**
     * Equivalent of {@link Class#getModifiers()}
     * 
     * <strong>This does not follow the java-api</strong>
     * The Class.getModifiers() returns an <code>int</code>, which should be decoded with the {@link Modifier}.
     * This method will return a list of strings representing the modifiers.
     * If this member was extracted from a source, it will keep its order. 
     * Otherwise if will be in the preferred order of the java-api.
     * 
     * @return all modifiers is this member
     */
    List<String> getModifiers();
    
    /**
     * (API description of {@link Modifier#isPublic(int)})
     * <p>
     * Return <code>true</code> if the class includes the public modifier, <code>false</code> otherwise.
     * <p>
     * 
     * @return <code>true</code> if class has the public modifier, otherwise <code>false</code>
     */
    boolean isPublic();
    
    /**
     * (API description of {@link Modifier#isProtected(int)})
     * <p>
     * Return <code>true</code> if the class includes the protected modifier, <code>false</code> otherwise.
     * </p>
     * 
     * @return <code>true</code> if class has the protected modifier, otherwise <code>false</code>
     */
    boolean isProtected();
    
    /**
     * (API description of {@link Modifier#isPrivate(int)})
     * <p>
     * Return <code>true</code> if the class includes the private modifier, <code>false</code> otherwise.
     * </p>
     * 
     * @return <code>true</code> if class has the private modifier, otherwise <code>false</code>
     */
    boolean isPrivate();
    
    /**
     * (API description of {@link Modifier#isFinal(int)})
     * <p>
     * Return <code>true</code> if the class includes the final modifier, <code>false</code> otherwise.
     * </p>
     * 
     * @return <code>true</code> if class has the final modifier, otherwise <code>false</code>
     */
    boolean isFinal();
    
    /**
     * (API description of {@link Modifier#isStatic((int)})
     * <p>
     * Return <code>true</code> if the class includes the static modifier, <code>false</code> otherwise.
     * </p>
     * 
     * @return <code>true</code> if class the static modifier, otherwise <code>false</code>
     */
    boolean isStatic();
    
    /**
     * (API description of {@link Modifier#isAbstract(int)})
     * 
     * Return <code>true</code> if the class includes the abstract modifier, <code>false</code> otherwise.
     * 
     * @return <code>true</code> if class has the abstract modifier, otherwise <code>false</code>
     */
    boolean isAbstract();
    
    boolean isPrimitive();
    
    /**
     * (API description of {@link java.lang.Class#toString()})
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