package com.thoughtworks.qdox.parser.impl;

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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.StringTokenizer;

import com.thoughtworks.qdox.builder.Builder;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.FieldDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;
import com.thoughtworks.qdox.parser.structs.PackageDef;
import com.thoughtworks.qdox.parser.structs.TypeDef;

public class BinaryClassParser
{
    private Class<?> declaringClazz;

    private Builder binaryBuilder;

    public BinaryClassParser( Class<?> declaringClazz, Builder modelBuilder )
    {
        this.declaringClazz = declaringClazz;
        this.binaryBuilder = modelBuilder;
    }

    public boolean parse()
    {
        try
        {
            // Spec change in Java 9:
            // "If this class represents an array type, a primitive type or void, this method returns null."
            // This means that classes without package will get a package with empty name
            if ( declaringClazz.getPackage() != null && !"".equals( declaringClazz.getPackage().getName() ) )
            {
                binaryBuilder.addPackage( new PackageDef( declaringClazz.getPackage().getName() ) );
            }

            addClass( declaringClazz );

            return true;
        }
        catch ( NoClassDefFoundError e )
        {
            return false;
        }
    }

    private void addClass( Class<?> clazz )
    {
        ClassDef classDef = new ClassDef( clazz.getSimpleName() );
        
        // Set the extended class and interfaces.
        Class<?>[] interfaces = clazz.getInterfaces();
        
        if ( clazz.isEnum() )
        {
            classDef.setType( ClassDef.ENUM );
        }
        else if ( clazz.isAnnotation() )
        {
            classDef.setType( ClassDef.ANNOTATION_TYPE );
        }
        else if ( clazz.isInterface() )
        {
            classDef.setType( ClassDef.INTERFACE );
            for ( int i = 0; i < interfaces.length; i++ )
            {
                Class<?> anInterface = interfaces[i];
                classDef.getExtends().add( new TypeDef( anInterface.getName() ) );
            }
        }
        else
        {
            for ( int i = 0; i < interfaces.length; i++ )
            {
                Class<?> anInterface = interfaces[i];
                classDef.getImplements().add( new TypeDef( anInterface.getName() ) );
            }
            Class<?> superclass = clazz.getSuperclass();
            if ( superclass != null )
            {
                classDef.getExtends().add( new TypeDef( superclass.getName() ) );
            }
        }

        addModifiers( classDef.getModifiers(), clazz.getModifiers() );

        binaryBuilder.beginClass( classDef );

        // add the constructors
        //
        // This also adds the default constructor if any which is different
        // to the source code as that does not create a default constructor
        // if no constructor exists.
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for ( int i = 0; i < constructors.length; i++ )
        {
            binaryBuilder.beginConstructor();
            MethodDef methodDef = createMethodDef( constructors[i] );
            binaryBuilder.endConstructor( methodDef );
        }

        // add the methods
        Method[] methods = clazz.getDeclaredMethods();
        for ( int i = 0; i < methods.length; i++ )
        {
            binaryBuilder.beginMethod();
            MethodDef methodDef = createMethodDef( methods[i] );
            binaryBuilder.endMethod( methodDef );
        }

        Field[] fields = clazz.getDeclaredFields();
        for ( int i = 0; i < fields.length; i++ )
        {
            addField( fields[i] );
        }
        
        Class<?>[] classes = clazz.getDeclaredClasses();
        for ( int i = 0; i < classes.length; i++ )
        {
            addClass( classes[i] );
        }

        binaryBuilder.endClass();
    }

    private static void addModifiers( Set<String> set, int modifier )
    {
        String modifierString = Modifier.toString( modifier );
        for ( StringTokenizer stringTokenizer = new StringTokenizer( modifierString ); stringTokenizer.hasMoreTokens(); )
        {
            set.add( stringTokenizer.nextToken() );
        }
    }

    private void addField( Field field )
    {
        FieldDef fieldDef = new FieldDef( field.getName() );
        Class<?> fieldType = field.getType();
        fieldDef.setType( getTypeDef( fieldType ) );
        fieldDef.setDimensions( getDimension( fieldType ) );
        fieldDef.setEnumConstant( field.isEnumConstant() );
        addModifiers( fieldDef.getModifiers(), field.getModifiers() );
        binaryBuilder.beginField( fieldDef );
        binaryBuilder.endField();
    }

    private MethodDef createMethodDef( Member member )
    {
        MethodDef methodDef = new MethodDef();
        // The name of constructors are qualified. Need to strip it.
        // This will work for regular methods too, since -1 + 1 = 0
        int lastDot = member.getName().lastIndexOf( '.' );
        methodDef.setName( member.getName().substring( lastDot + 1 ) );

        addModifiers( methodDef.getModifiers(), member.getModifiers() );
        Class<?>[] exceptions;
        Class<?>[] parameterTypes;
        if ( member instanceof Method )
        {
            methodDef.setConstructor( false );

            // For some stupid reason, these methods are not defined in Member,
            // but in both Method and Construcotr.
            exceptions = ( (Method) member ).getExceptionTypes();
            parameterTypes = ( (Method) member ).getParameterTypes();

            Class<?> returnType = ( (Method) member ).getReturnType();
            methodDef.setReturnType( getTypeDef( returnType ) );
            methodDef.setDimensions( getDimension( returnType ) );

        }
        else
        {
            methodDef.setConstructor( true );

            exceptions = ( (Constructor<?>) member ).getExceptionTypes();
            parameterTypes = ( (Constructor<?>) member ).getParameterTypes();
        }
        for ( int j = 0; j < exceptions.length; j++ )
        {
            Class<?> exception = exceptions[j];
            methodDef.getExceptions().add( getTypeDef( exception ) );
        }
        for ( int j = 0; j < parameterTypes.length; j++ )
        {
            FieldDef param = new FieldDef( "p" + j );
            Class<?> parameterType = parameterTypes[j];
            param.setType( getTypeDef( parameterType ) );
            param.setDimensions( getDimension( parameterType ) );
            binaryBuilder.addParameter( param );
        }
        return methodDef;
    }

    private static int getDimension( Class<?> c )
    {
        return c.getName().lastIndexOf( '[' ) + 1;
    }

    private static String getTypeName( Class<?> c )
    {
        return c.getComponentType() != null ? c.getComponentType().getName() : c.getName();
    }

    private static TypeDef getTypeDef( Class<?> c )
    {
        return new TypeDef( getTypeName( c ) );
    }
}
