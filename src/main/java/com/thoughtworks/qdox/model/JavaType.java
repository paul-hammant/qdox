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
    /**
     * <blockquote>
     * The class or interface must be named by its binary name, which must meet the following constraints:
     * <ul>
     *   <li>The binary name of a top level type is its canonical name.</li>
     *   <li>The binary name of a member type consists of the binary name of its immediately enclosing type, followed by $, followed by the simple name of the member.</li>
     * </ul>
     * </blockquote>
     * 
     * @return the binary name
     * @since 2.0
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-13.html#jls-13.1">https://docs.oracle.com/javase/specs/jls/se8/html/jls-13.html#jls-13.1</a>
     */
    String getBinaryName();
    
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
     * <blockquote>
     * Every primitive type, named package, top level class, and top level interface has a fully qualified name:
     * <ul>
     *   <li>The fully qualified name of a primitive type is the keyword for that primitive type, namely byte, short, char, int, long, float, double, or boolean.</li>
     *   <li>The fully qualified name of a named package that is not a subpackage of a named package is its simple name.</li>
     *   <li>The fully qualified name of a named package that is a subpackage of another named package consists of the fully qualified name of the containing package, followed by ".", followed by the simple (member) name of the subpackage.</li>
     *   <li>The fully qualified name of a top level class or top level interface that is declared in an unnamed package is the simple name of the class or interface.</li>
     *   <li>The fully qualified name of a top level class or top level interface that is declared in a named package consists of the fully qualified name of the package, followed by ".", followed by the simple name of the class or interface.</li>
     * </ul>
     * Each member class, member interface, and array type may have a fully qualified name:
     * <ul>
     *   <li>A member class or member interface M of another class or interface C has a fully qualified name if and only if C has a fully qualified name.</li>
     *   <li>In that case, the fully qualified name of M consists of the fully qualified name of C, followed by ".", followed by the simple name of M.</li>
     *   <li>An array type has a fully qualified name if and only if its element type has a fully qualified name.</li>
     *   <li>In that case, the fully qualified name of an array type consists of the fully qualified name of the component type of the array type followed by "[]".</li>
     * </ul>
     * </blockquote>
     * 
     * Some examples how names will be translated 
     * <pre>
     * Object &gt; java.lang.Object
     * java.util.List &gt; java.util.List
     * ?  &gt; ?
     * T  &gt; T
     * anypackage.Outer.Inner &gt; anypackage.Outer.Inner
     * String[][] &gt; java.lang.String[][]
     * </pre>
     * 
     * @return the fully qualified name, never <code>null</code>
     * @see JavaClass#getComponentType()
     * @see #getBinaryName()
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-6.html#jls-6.7">https://docs.oracle.com/javase/specs/jls/se8/html/jls-6.html#jls-6.7</a>
     */
    // @TODO make clear difference between FQN and canonicalName, specs say FQN can be null
    String getFullyQualifiedName();

    /**
     * The fully qualified name with generic information.
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
     *  private String fieldA;             // getValue() will return "String"
     *  private java.lang.String fieldA;   // getValue() will return "java.lang.String"
     *  private List&gt;String&gt; aList;  // getValue() will return "List"
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
     *  private String fieldA;             // getValue() will return "String"
     *  private java.lang.String fieldA;   // getValue() will return "java.lang.String"
     *  private List&gt;String&gt; aList;  // getValue() will return "List&gt;String&gt;"
     * </pre>
     * @return the generic name of the class as used in the source
     */
    String getGenericValue();

    String toGenericString();
}