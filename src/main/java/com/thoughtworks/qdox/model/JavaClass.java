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
 * Equivalent of {@link java.lang.Class}, providing the most important methods.
 * Where the original Class is using an Array, this model is using a List.
 * 
 * @author Robert Scholte
 */
public interface JavaClass extends JavaModel, JavaType, JavaClassParent, JavaAnnotatedElement, JavaGenericDeclaration
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

    JavaType getSuperClass();

    /**
     * Shorthand for getSuperClass().getJavaClass() with null checking.
     */
    JavaClass getSuperJavaClass();

    List<JavaType> getImplements();

    /**
     * @since 1.3
     */
    List<JavaClass> getImplementedInterfaces();
    
    /**
     * Equivalent of {@link java.lang.Class#getInterfaces()}
     *  Determines the interfaces implemented by the class or interface represented by this object.     * 
     * 
     * @return a list of interfaces, never <code>null</code>
     * @since 2.0
     */
    List<JavaClass> getInterfaces();

    String getCodeBlock();

    JavaSource getParentSource();

    /**
     * Equivalent of {@link java.lang.Class#getPackage()}
     * @return
     */
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
     * @deprecated use {@link #resolveCanonicalName(String)} or {@link #resolveFullyQualifiedName(String)} instead
     */
    String resolveType( String name );
    
    /**
     * The name can be both absolute (including the package) or relative (matching a subclass or an import).
     * 
     * @param name the name to resolve
     * @return the resolved canonical name, otherwise <code>null</code>
     * @since 2.0
     */
    String resolveCanonicalName( String name );
    
    /**
     * The name can be both absolute (including the package) or relative (matching a subclass or an import).
     * 
     * @param name the name to resolve
     * @return the resolved fully qualified name, otherwise <code>null</code>
     * @since 2.0
     */
    String resolveFullyQualifiedName( String name );

    /**
     * If this class has a package, it will return the package name, followed by a "."(dot).
     * Otherwise it will return an empty String
     * 
     * @return the package name plus a dot if there's a package, otherwise an empty String
     */
    String getClassNamePrefix();

    /**
     * 
     * 
     * @deprecated the JavaClass should have the same methods
     */
    Type asType();

    /**
     * Equivalent of {@link java.lang.Class#getMethods()}
     * 
     * @return the methods declared or overridden in this class
     */
    List<JavaMethod> getMethods();
    
    /**
     * Equivalent of {@link java.lang.Class#getConstructors()}
     * 
     * @return the list of constructors
     * @since 2.0
     */
    List<JavaConstructor> getConstructors();
    
    
    /**
     * 
     * @param parameterTypes the parameter types of the constructor, can be <code>null</code>
     * @return the matching constructor, otherwise <code>null</code>
     * @since 2.0
     */
    JavaConstructor getConstructor(List<JavaType> parameterTypes);
    
    /**
     * 
     * @param parameterTypes the parameter types of the constructor, can be <code>null</code>
     * @param varArg define is the constructor has varArgs
     * @return the matching constructor, otherwise <code>null</code>
     * @since 2.0
     */
    JavaConstructor getConstructor(List<JavaType> parameterTypes, boolean varArg);
    

    /**
     * @since 1.3
     */
    List<JavaMethod> getMethods( boolean superclasses );

    /**
     * 
     * @param name the name of the method
     * @param parameterTypes the parameter types of the method, can be <code>null</code>.
     * @return the matching method, otherwise <code>null</code>
     */
    JavaMethod getMethodBySignature( String name, List<JavaType> parameterTypes );

    /**
     * This should be the signature for getMethodBySignature.
     * 
     * @param name the name of the method
     * @param parameterTypes the parameter types of the method, can be <code>null<code>
     * @param varArgs define if the method has varArgs
     * @return the matching method, otherwise <code>null</code>
     */
    JavaMethod getMethod( String name, List<JavaType> parameterTypes, boolean varArgs );

    /**
     * 
     * @param name the name of the method
     * @param parameterTypes the parameter types of the method, can be <code>null</code>
     * @param superclasses to define if superclasses should be included as well
     * @return the matching method, otherwise <code>null</code> 
     */
    JavaMethod getMethodBySignature( String name, List<JavaType> parameterTypes, boolean superclasses );

    /**
     * 
     * @param name the name of the method
     * @param parameterTypes the parameter types of the method, can be <code>null<code>
     * @param superclasses
     * @param varArg define if the method has varArgs
     * @return the matching method, otherwise <code>null</code>
     */
    JavaMethod getMethodBySignature( String name, List<JavaType> parameterTypes, boolean superclasses, boolean varArg );

    /**
     * 
     * @param name the name of the method
     * @param parameterTypes the parameter types of the method, can be <code>null<code>
     * @param superclasses to define if superclasses should be included as well
     * @return the matching methods, otherwise <code>null</code>
     */
    List<JavaMethod> getMethodsBySignature( String name, List<JavaType> parameterTypes, boolean superclasses );

    /**
     * 
     * @param name the name of the method
     * @param parameterTypes the parameter types of the method, can be <code>null<code>
     * @param superclasses to define if superclasses should be included as well
     * @param varArg define if the method has varArgs
     * @return the matching methods, otherwise <code>null</code>
     */
    List<JavaMethod> getMethodsBySignature( String name, List<JavaType> parameterTypes, boolean superclasses,
                                                   boolean varArg );

    /**
     * Equivalent of {@link java.lang.Class#getFields()}
     * 
     * @return a list of fiels, never <code>null</code>
     */
    List<JavaField> getFields();

    /**
     * Equivalent of {@link java.lang.Class#getField(String)}, where this method can resolve every field
     * 
     * @param name
     * @return
     */
    JavaField getFieldByName( String name );
    
    /**
     * Based on {@link java.lang.Class#getEnumConstants()}.
     *  
     * 
     * @return a List of enum constants if this class is an <code>enum</code>, otherwise <code>null</code>
     */
    List<JavaField> getEnumConstants();

    /**
     * 
     * 
     * @return the enumConstant matching the {@code name}, otherwise <code>null</code>
     */
    JavaField getEnumConstantByName( String name );

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
    boolean isA( String fullyQualifiedName );

    /**
     * @param javaClass 
     * @since 1.3
     */
    boolean isA( JavaClass javaClass );

    /**
     * 
     * @return the number of dimensions, at least 0
     * @since 2.0
     */
    int getDimensions();
    
    /**
     * 
     * @return <code>true</code> if this JavaClass is an array, otherwise <code>false</code>
     * @since 2.0
     */
    boolean isArray();

    /**
     * Gets bean properties without looking in superclasses or interfaces.
     *
     * @since 1.3
     */
    List<BeanProperty> getBeanProperties();

    /**
     * 
     * @param superclasses to define if superclasses should be included as well
     * @since 1.3
     */
    List<BeanProperty> getBeanProperties( boolean superclasses );

    /**
     * Gets bean property without looking in superclasses or interfaces.
     *
     * @param propertyName the name of the property
     * @since 1.3
     */
    BeanProperty getBeanProperty( String propertyName );

    /**
     * @param propertyName the name of the property
     * @param superclasses to define if superclasses should be included as well
     * 
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
     *  private List<String> aList;      // getValue() will return "List"
     * </pre>
     * 
     * @return the name of the class as used in the source
     */
    String getValue();
    
    /**
     * A java5+ representation of the class.
     * When including all imports, you should be safe to use this method.
     * 
     * Examples:
     * <pre>
     *  private String fieldA;           // getValue() will return "String"
     *  private java.lang.String fieldA; // getValue() will return "java.lang.String"
     *  private List<String> aList;      // getValue() will return "List<String>"
     * </pre>     * 
     * @return the generic name of the class as used in the source
     */
    String getGenericValue();
    
    /**
     * Equivalent of {@link Class#getModifiers()}
     * 
     * <strong>This does not follow the java-api</strong>
     * The Class.getModifiers() returns an <code>int</code>, which should be decoded with the {@link java.lang.reflect.Modifier}.
     * This method will return a list of strings representing the modifiers.
     * If this member was extracted from a source, it will keep its order. 
     * Otherwise if will be in the preferred order of the java-api.
     * 
     * @return all modifiers is this member
     */
    List<String> getModifiers();
    
    /**
     * (API description of {@link java.lang.reflect.Modifier#isPublic(int)})
     * <p>
     * Return <code>true</code> if the class includes the public modifier, <code>false</code> otherwise.
     * <p>
     * 
     * @return <code>true</code> if class has the public modifier, otherwise <code>false</code>
     */
    boolean isPublic();
    
    /**
     * (API description of {@link java.lang.reflect.Modifier#isProtected(int)})
     * <p>
     * Return <code>true</code> if the class includes the protected modifier, <code>false</code> otherwise.
     * </p>
     * 
     * @return <code>true</code> if class has the protected modifier, otherwise <code>false</code>
     */
    boolean isProtected();
    
    /**
     * (API description of {@link java.lang.reflect.Modifier#isPrivate(int)})
     * <p>
     * Return <code>true</code> if the class includes the private modifier, <code>false</code> otherwise.
     * </p>
     * 
     * @return <code>true</code> if class has the private modifier, otherwise <code>false</code>
     */
    boolean isPrivate();
    
    /**
     * (API description of {@link java.lang.reflect.Modifier#isFinal(int)})
     * <p>
     * Return <code>true</code> if the class includes the final modifier, <code>false</code> otherwise.
     * </p>
     * 
     * @return <code>true</code> if class has the final modifier, otherwise <code>false</code>
     */
    boolean isFinal();
    
    /**
     * (API description of {@link java.lang.reflect.Modifier#isStatic(int)})
     * <p>
     * Return <code>true</code> if the class includes the static modifier, <code>false</code> otherwise.
     * </p>
     * 
     * @return <code>true</code> if class the static modifier, otherwise <code>false</code>
     */
    boolean isStatic();
    
    /**
     * (API description of {@link java.lang.reflect.Modifier#isAbstract(int)})
     * 
     * Return <code>true</code> if the class includes the abstract modifier, <code>false</code> otherwise.
     * 
     * @return <code>true</code> if class has the abstract modifier, otherwise <code>false</code>
     */
    boolean isAbstract();
    
    /**
     *  Equivalent of  {@link java.lang.Class#isPrimitive()}
     *  
     * @return <code>true</code> if this class represents a primitive, otherwise <code>false</code>
     */
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

    String getGenericFullyQualifiedName();
}