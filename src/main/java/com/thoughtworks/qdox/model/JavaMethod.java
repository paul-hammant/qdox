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

public interface JavaMethod extends JavaAnnotatedElement, JavaMember, JavaModel, JavaGenericDeclaration
{

    /**
     * 
     * @return the return type
     * @deprecated it is recommended to use getReturnType()
     */
    Type getReturns();

    List<JavaParameter> getParameters();

    JavaParameter getParameterByName( String name );

    List<Type> getExceptions();

    /**
     * 
     * @return true is this method conains varArgs
     */
    boolean isVarArgs();

    String getCodeBlock();

    /**
     * @since 1.3
     */
    String getDeclarationSignature( boolean withModifiers );

    /**
     * @since 1.3
     */
    String getCallSignature();

    /**
     * This method is NOT varArg aware.
     * 
     * @param name
     * @param parameterTypes
     * @return
     */
    boolean signatureMatches( String name, List<Type> parameterTypes );

    /**
     * @param name method name
     * @param parameterTypes parameter types or null if there are no parameters.
     * @param varArg <code>true</code> is signature should match a varArg-method, otherwise <code>false</code>
     * @return true if the signature and parameters match.
     */
    boolean signatureMatches( String name, List<Type> parameterTypes, boolean varArg );

    /**
     * @return true if this method is a Java Bean accessor
     * @since 1.3
     */
    boolean isPropertyAccessor();

    /**
     * @return true if this method is a Java Bean accessor
     * @since 1.3
     */
    boolean isPropertyMutator();

    /**
     * @return the type of the property this method represents, or null if this method
     * is not a property mutator or property accessor.
     * @since 1.3
     */
    Type getPropertyType();

    /**
     * @return the name of the property this method represents, or null if this method
     * is not a property mutator or property accessor.
     * @since 1.3
     */
    String getPropertyName();

    List<DocletTag> getTagsByName( String name, boolean inherited );

    /**
     * Get the original source code of the body of this method.
     *
     * @return Code as string.
     */
    String getSourceCode();

    List<TypeVariable> getTypeParameters();

    /**
     * Equivalent of java.lang.reflect.Method.getGenericReturnType()
     * 
     * @return the generic returntype
     * @since 1.12
     */
    Type getGenericReturnType();

    /**
     * Equivalent of java.lang.reflect.Method.getReturnType()
     * 
     * @return
     * @since 1.12
     */
    Type getReturnType();

    /**
     * If a class inherits this method from a generic class or interface, you can use this method to get the resolved return type
     * 
     * @param resolve
     * @return
     * @since 1.12
     */
    Type getReturnType( boolean resolve );

    /**
     * 
     * @return the parameter types as array
     * @since 1.12
     */
    List<Type> getParameterTypes();

    /**
     * If a class inherits this method from a generic class or interface, you can use this method to get the resolved parameter types
     * 
     * @param resolve
     * @return the parameter types as array
     * @since 1.12
     */
    List<Type> getParameterTypes( boolean resolve );

    JavaClass getParentClass();

    DocletTag getTagByName( String string, boolean b );
}