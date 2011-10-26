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

/**
 * Equivalent of {@link java.lang.reflect.Type}.
 * 
 */
public interface JavaType
{
    JavaType VOID = new JavaType() 
    {
        private final String value = "void";

        public String getCanonicalName()
        {
            return value;
        }

        public String getGenericCanonicalName()
        {
            return value;
        }

        public String getFullyQualifiedName()
        {
            return value;
        }

        public String getGenericFullyQualifiedName()
        {
            return value;
        }

        public String getValue()
        {
            return value;
        }

        public String getGenericValue()
        {
            return value;
        }

        public String toGenericString()
        {
            return value;
        }
    };

    /**
     * Equivalent of (@link {@link java.lang.Class#getCanonicalName()}.
     * 
     * @return the canonical name of this class
     */
    String getCanonicalName();

    /**
     * The canonical name with generic information.
     * 
     * @return the generic canonical name
     */
    String getGenericCanonicalName();

    /**
     * Returns the FQN of an Object or the handler of a Type.
     * If the name of the can't be resolved based on the imports and the classes on the classpath the name will be returned.
     * InnerClasses will use the $ sign.
     * If the type is an array, the brackets will be included. The get only the name, use {@link #getComponentType()}.
     * 
     * Some examples how names will be translated 
     * <pre>
     * Object > java.lang.Object
     * java.util.List > java.util.List
     * ?  > ?
     * T  > T
     * anypackage.Outer.Inner > anypackage.Outer$Inner
     * String[][] > java.lang.String[][]
     * </pre>
     * 
     * @return the fully qualified name, never <code>null</code>
     * @see #getComponentType()
     */
    String getFullyQualifiedName();

    /**
     * The fully qualified nate with generic information.
     * 
     * @return the generic fully qualified name
     */
    String getGenericFullyQualifiedName();

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
     * </pre>
     * @return the generic name of the class as used in the source
     */
    String getGenericValue();

    @Deprecated
    String toGenericString();
}