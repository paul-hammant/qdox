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

import com.thoughtworks.qdox.model.ModelBuilder;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.FieldDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;
import com.thoughtworks.qdox.parser.structs.PackageDef;
import com.thoughtworks.qdox.parser.structs.TypeDef;

public class BinaryClassParser
{
    private Class<?> clazz;
    private ModelBuilder binaryBuilder;
    
    public BinaryClassParser(Class<?> clazz, ModelBuilder modelBuilder)
    {
        this.clazz = clazz;
        this.binaryBuilder = modelBuilder;
    }
    
    public boolean parse() {
        try {
            String name = clazz.getName();

            // Set the package name and class name
            String packageName = getPackageName(name);
            binaryBuilder.addPackage(new PackageDef(packageName));

            ClassDef classDef = new ClassDef();
            classDef.name = getClassName(name);

            // Set the extended class and interfaces.
            Class<?>[] interfaces = clazz.getInterfaces();
            if (clazz.isInterface()) {
                // It's an interface
                classDef.type = ClassDef.INTERFACE;
                for (int i = 0; i < interfaces.length; i++) {
                    Class<?> anInterface = interfaces[i];
                    classDef.extendz.add(new TypeDef(anInterface.getName()));
                }
            } else {
                // It's a class
                for (int i = 0; i < interfaces.length; i++) {
                    Class<?> anInterface = interfaces[i];
                    classDef.implementz.add(new TypeDef(anInterface.getName()));
                }
                Class<?> superclass = clazz.getSuperclass();
                if (superclass != null) {
                    classDef.extendz.add(new TypeDef(superclass.getName()));
                }
            }

            addModifiers(classDef.modifiers, clazz.getModifiers());

            binaryBuilder.beginClass(classDef);

            // add the constructors
            //
            // This also adds the default constructor if any which is different
            // to the source code as that does not create a default constructor
            // if no constructor exists.
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            for (int i = 0; i < constructors.length; i++) {
                addMethodOrConstructor(constructors[i], binaryBuilder);
            }

            // add the methods
            Method[] methods = clazz.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                addMethodOrConstructor(methods[i], binaryBuilder);
            }

            Field[] fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                addField(fields[i], binaryBuilder);
            }

            binaryBuilder.endClass();
            
            return true;
        } catch (NoClassDefFoundError e) {
            return false;
        }
    }
    
    private void addModifiers(Set<String> set, int modifier) {
        String modifierString = Modifier.toString(modifier);
        for (StringTokenizer stringTokenizer = new StringTokenizer(modifierString); stringTokenizer.hasMoreTokens();) {
            set.add(stringTokenizer.nextToken());
        }
    }

    private void addField(Field field, ModelBuilder binaryBuilder) {
        FieldDef fieldDef = new FieldDef();
        Class<?> fieldType = field.getType();
        fieldDef.name = field.getName();
        fieldDef.type = getTypeDef(fieldType);
        fieldDef.dimensions = getDimension(fieldType);
        addModifiers( fieldDef.modifiers, field.getModifiers());
        binaryBuilder.addField(fieldDef);
    }

    private void addMethodOrConstructor(Member member, ModelBuilder binaryBuilder) {
        MethodDef methodDef = new MethodDef();
        // The name of constructors are qualified. Need to strip it.
        // This will work for regular methods too, since -1 + 1 = 0
        int lastDot = member.getName().lastIndexOf('.');
        methodDef.name = member.getName().substring(lastDot + 1);

        addModifiers(methodDef.modifiers, member.getModifiers());
        Class<?>[] exceptions;
        Class<?>[] parameterTypes;
        if (member instanceof Method) {
            methodDef.constructor = false;

            // For some stupid reason, these methods are not defined in Member,
            // but in both Method and Construcotr.
            exceptions = ((Method) member).getExceptionTypes();
            parameterTypes = ((Method) member).getParameterTypes();

            Class<?> returnType = ((Method) member).getReturnType();
            methodDef.returnType = getTypeDef(returnType);
            methodDef.dimensions = getDimension(returnType);

        } else {
            methodDef.constructor = true;

            exceptions = ((Constructor<?>) member).getExceptionTypes();
            parameterTypes = ((Constructor<?>) member).getParameterTypes();
        }
        for (int j = 0; j < exceptions.length; j++) {
            Class<?> exception = exceptions[j];
            methodDef.exceptions.add(exception.getName());
        }
        binaryBuilder.addMethod(methodDef);
        for (int j = 0; j < parameterTypes.length; j++) {
            FieldDef param = new FieldDef();
            Class<?> parameterType = parameterTypes[j];
            param.name = "p" + j;
            param.type = getTypeDef(parameterType);
            param.dimensions = getDimension(parameterType);
            binaryBuilder.addParameter( param );
        }
    }

    private static final int getDimension(Class<?> c) {
        return c.getName().lastIndexOf('[') + 1;
    }

    private static String getTypeName(Class<?> c) {
        return c.getComponentType() != null ? c.getComponentType().getName() : c.getName();
    }
    
    private static TypeDef getTypeDef(Class<?> c) {
        return new TypeDef(getTypeName(c));
    }
    

    private String getPackageName(String fullClassName) {
        int lastDot = fullClassName.lastIndexOf('.');
        return lastDot == -1 ? "" : fullClassName.substring(0, lastDot);
    }

    private String getClassName(String fullClassName) {
        int lastDot = fullClassName.lastIndexOf('.');
        return lastDot == -1 ? fullClassName : fullClassName.substring(lastDot + 1);
    }
}
