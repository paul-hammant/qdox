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

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.qdox.library.ClassLibrary;

public class Type implements JavaClass, JavaType, JavaParameterizedType, Serializable {

    public static final Type VOID = new Type("void");

    private String name;
    private JavaClassParent context;
    private String fullName;
    private int dimensions;
    private List<JavaType> actualArgumentTypes = Collections.emptyList();
    
    public Type(String fullName, String name, int dimensions, JavaClassParent context) {
        this.fullName = fullName;
        this.name = name;
        this.dimensions = dimensions;
        this.context = context;
    }
    
    public Type(String fullName, int dimensions, JavaClassParent context) {
        this(fullName, (String) null, dimensions, context);
    }

    public Type(String fullName, int dimensions) {
        this(fullName, dimensions, null);
    }

    /**
     * Should only be used by primitives, since they don't have a classloader.
     * 
     * @param fullName the name of the primitive
     */
    public Type( String fullName ) 
    {
        this( fullName, 0 );
    }
    
	public static Type createUnresolved(String name, int dimensions, JavaClassParent context) {
        return new Type(null, name, dimensions, context);
    }
    
	public JavaClassParent getJavaClassParent() {
        return context;
    }

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
    public String getFullyQualifiedName() {
        StringBuffer result = new StringBuffer( isResolved() ? fullName : name );
        for (int i = 0; i < dimensions; i++) 
        {
            result.append("[]");
        }
        return result.toString();
    }

    /**
     * Equivalent of {@link Class#getComponentType()}
     * If this type is an array, return its component type
     * 
     * @return the type of array if it's one, otherwise <code>null</code>
     */
    public JavaClass getComponentType() {
      return isArray() ? resolveRealClass() : null;
    }
    
    /**
     * The FQN representation of an Object for code usage
     * This implementation ignores generics
     *
     * Some examples how Objects will be translated
     * <pre>
     * Object > java.lang.object
     * java.util.List<T> > java.util.List
     * ? > ?
     * T > T
     * anypackage.Outer.Inner > anypackage.Outer.Inner
     * </pre>
     * 
     * @return type representation for code usage
     */
    public String getValue() {
        return ( name != null ?  name : getFullyQualifiedName().replaceAll( "\\$", "." ) );
    }
    
    /**
     * The FQN representation of an Object for code usage
     * This implementation ignores generics
     *
     * Some examples how Objects will be translated
     * <pre>
     * Object > java.lang.object
     * java.util.List<T> > java.util.List
     * ? > ?
     * T > T
     * anypackage.Outer.Inner > anypackage.Outer.Inner
     * </pre>

     * @since 1.8
     * @return generic type representation for code usage 
     */
    public String getGenericValue()
    {
        StringBuffer result = new StringBuffer( getValue() );
        if ( !actualArgumentTypes.isEmpty() )
        {
            result.append( "<" );
            for ( Iterator<JavaType> iter = actualArgumentTypes.iterator(); iter.hasNext(); )
            {
                result.append( iter.next().getGenericValue() );
                if ( iter.hasNext() )
                {
                    result.append( "," );
                }
            }
            result.append( ">" );
        }
        for ( int i = 0; i < dimensions; i++ )
        {
            result.append( "[]" );
        }
        return result.toString();
    }
    
    protected static <D extends JavaGenericDeclaration> String getGenericValue( JavaType base, List<TypeVariable<D>> typeVariableList )
    {
        StringBuffer result = new StringBuffer( getResolvedValue( base, typeVariableList ) );
        for ( Iterator<JavaType> iter = getActualTypeArguments( base ).iterator(); iter.hasNext(); )
        {
            result.append( Type.resolve( base, typeVariableList ) );
            if ( iter.hasNext() )
            {
                result.append( "," );
            }
        }
        return result.toString();
    }
    
    private static List<JavaType> getActualTypeArguments( JavaType base )
    {
        List<JavaType> result;
        if ( base instanceof JavaParameterizedType )
        {
            result = ( (JavaParameterizedType) base ).getActualTypeArguments();
        }
        else
        {
            result = Collections.emptyList();
        }
        return result;
    }

    protected static <D extends JavaGenericDeclaration> String getResolvedValue( JavaType base, List<TypeVariable<D>> typeParameters )
    {
        String result = base.getValue();
        for ( TypeVariable<?> typeParameter : typeParameters )
        {
            if ( typeParameter.getName().equals( base.getValue() ) )
            {
                result = typeParameter.getBounds().get( 0 ).getValue();
                break;
            }
        }
        return result;
    }
    
    protected static <D extends JavaGenericDeclaration> TypeVariable<D> resolve( JavaType base, List<TypeVariable<D>> typeParameters )
    {
        TypeVariable<D> result = null;
        // String result = getGenericValue(typeParameters);
        for ( TypeVariable<D> typeParameter : typeParameters )
        {
            if ( typeParameter.getName().equals( base.getValue() ) )
            {
                result = typeParameter;
                break;
            }
        }
        return result;
    }

    protected boolean isResolved()
    {
        if ( fullName == null && context != null )
        {
            fullName = context.resolveType( name );
        }
        return ( fullName != null );
    }

    /**
     * Returns true if this Type is an array
     * 
     * @return true if this type is an array, otherwise <code>null</code>
     */
    public boolean isArray() {
        return dimensions > 0;
    }

    /**
     * Returns the depth of this array, 0 if it's not an array
     * 
     * @return The depth of this array, at least <code>0</code>
     */
    public int getDimensions() {
        return dimensions;
    }

    /**
     * 
     * @return the actualTypeArguments or null
     */
    public List<JavaType> getActualTypeArguments()
    {
        return actualArgumentTypes;
    }
    
    public void setActualArgumentTypes( List<JavaType> actualArgumentTypes )
    {
        this.actualArgumentTypes = actualArgumentTypes;
    }
    
    /**
     * Equivalent of {@link Class#toString()}. 
     * Converts the object to a string.
     * 
     * @return a string representation of this type.
     * @see Class#toString()
     */
    public String toString()
    {
        return getFullyQualifiedName();
    }

    /**
     * Returns getGenericValue() extended with the array information
     * 
     * <pre>
     * Object > java.lang.Object
     * Object[] > java.lang.Object[]
     * List&lt;Object&gt; > java.lang.List<java.lang.Object>
     * Outer.Inner > Outer$Inner
     * Outer.Inner&lt;Object&gt;[][] > Outer$Inner<java.lang.Object>[][] 
     * </pre>
     * @return a generic string representation of this type.
     */
    public String toGenericString() {
        return getGenericFullyQualifiedName();
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( !( obj instanceof Type ) )
        {
            return false;
        }
        Type t = (Type) obj;
        return getFullyQualifiedName().equals( t.getFullyQualifiedName() ) && t.getDimensions() == getDimensions();
    }

    @Override
    public int hashCode() {
        return getFullyQualifiedName().hashCode();
    }
    
    private JavaClass resolveRealClass() 
    {
        JavaClass result;
        String qualifiedName = isResolved() ? fullName : name;
        if ( isPrimitive( qualifiedName ) )
        {
            result = new DefaultJavaClass( qualifiedName );
        }
        else
        {
            result = getJavaClassParent().getNestedClassByName( qualifiedName );
            if ( result == null )
            {
                result = getJavaClassLibrary().getJavaClass( qualifiedName, true );
            }
        }

        return result;
    }

    /**
     *  
     * @return this
     * @deprecated Type already has the JavaClass interface
     */
    public JavaClass getJavaClass()
    {
        return this;
    }

    /**
     * @since 1.3
     */
    public boolean isA( Type type )
    {
        if ( this == type )
        {
            return true;
        }
        else
        {
            return this.isA( type );
        }
    }

    /**
     * @since 1.6
     */
    public boolean isPrimitive() {
       return isPrimitive( getValue() );
    }
    
    private static boolean isPrimitive( String value )
    {
        return "void".equals(value)           
        || "boolean".equals(value)
        || "byte".equals(value)
        || "char".equals(value)
        || "short".equals(value)
        || "int".equals(value)
        || "long".equals(value)
        || "float".equals(value)
        || "double".equals(value);
        
    }

    /**
     * @since 1.6
     */
    public boolean isVoid() {
        return "void".equals(getValue());
    }

//    String superSource = "public abstract class Test<T> {\n" + 
//    "        private T me;\n" + 
//    "        public Test(T me) {\n" + 
//    "            this.me = me;\n" + 
//    "        }\n" + 
//    "        public T getValue() {\n" + 
//    "            return me;\n" + 
//    "        }\n" + 
//    "    }";
//String subSource = "public class StringTest extends Test<String> {\n" + 
//    "        public StringTest(String s) {\n" + 
//    "            super(s);\n" + 
//    "        }\n" + 
//    "    }";
    
    /**
     *  Consider the following example
     *  
     *  <pre>
     *  public abstract class AbstractClass&lt;T&gt; 
     *  {
     *    private T value;
     *    
     *    public AbstractClass( T value ) { this.value = value; }
     *    
     *    public T getValue() { return value; }
     *  }
     *  
     *  public class ConcreteClass extends AbstractClass&lt;String&gt;
     *  {
     *    public ConcreteClass( String s ) { super( s ); }
     *  }
     *  </pre>
     *  <p>
     *  We want to know the resolved returnType when calling <code>ConcreteClass.getValue()</code>.
     *  The expected type is String.
     *  </p>
     *  
     *  <ul>
     *   <li>{@code this} would be T</li>
     *   <li>{@code declaringClass} would be AbstractClass, since that's where T is used</li>
     *   <li>{@code callingClass}  would be ConcreteClass</li>
     *  </ul>
     * 
     * @param declaringClass
     * @param callingClass
     * @return
     */
    protected static JavaType resolve( JavaType base, JavaClass declaringClass, JavaClass callingClass )
    {
        JavaType result = base;

        int typeIndex = getTypeVariableIndex( declaringClass, base.getFullyQualifiedName() );

        if ( typeIndex >= 0 )
        {
            String fqn = declaringClass.getFullyQualifiedName();
            if ( callingClass.getSuperClass() != null
                && fqn.equals( callingClass.getSuperClass().getFullyQualifiedName() ) )
            {
                result = getActualTypeArguments( callingClass.getSuperClass() ).get( typeIndex );
            }
            else
            {
                for ( JavaClass implement : callingClass.getInterfaces() )
                {
                    if ( fqn.equals( implement.getFullyQualifiedName() ) )
                    {
                        result = resolve( getActualTypeArguments( implement ).get( typeIndex ), implement, implement );
                        break;
                    }
                }
                // no direct interface available, try indirect
            }
        }

        List<JavaType> actualTypeArguments = getActualTypeArguments(base); 
        if ( !actualTypeArguments.isEmpty() )
        {
            Type typeResult =
                new Type( base.getFullyQualifiedName(), base.getValue(), ((Type)base).getDimensions(),
                          ((Type)base).getJavaClassParent() );

            List<JavaType> actualTypes = new LinkedList<JavaType>();
            for ( JavaType actualArgType : actualTypeArguments )
            {
                actualTypes.add( resolve( actualArgType, declaringClass, callingClass ) );
            }
            typeResult.setActualArgumentTypes( actualTypes );
            result = typeResult;
        }
        return result;
    }

    private static int getTypeVariableIndex( JavaClass declaringClass, String fqn )
    {
        int typeIndex = -1;
        for ( Object typeVariable : declaringClass.getTypeParameters() )
        {
            typeIndex++;
            if ( ((TypeVariable<?>) typeVariable).getFullyQualifiedName().equals( fqn ) )
            {
                return typeIndex;
            }
        }
        return -1;
    }

    /**
     * 
     * @return a generic string representation of this type with fully qualified names.
     */
    public String getGenericFullyQualifiedName()
    {
        StringBuffer result = new StringBuffer( isResolved() ? fullName : name );
        if ( !actualArgumentTypes.isEmpty() )
        {
            result.append( "<" );
            for ( Iterator<JavaType> iter = actualArgumentTypes.iterator(); iter.hasNext(); )
            {
                result.append( iter.next().getGenericFullyQualifiedName() );
                if ( iter.hasNext() )
                {
                    result.append( "," );
                }
            }
            result.append( ">" );
        }
        for ( int i = 0; i < dimensions; i++ )
        {
            result.append( "[]" );
        }
        return result.toString();
    }

    protected static <D extends JavaGenericDeclaration> String getResolvedGenericValue( JavaType base, List<TypeVariable<D>> typeParameters )
    {
        StringBuffer result = new StringBuffer();
        TypeVariable<?> variable = resolve( base, typeParameters );
        result.append( variable == null ? base.getValue() : variable.getBounds().get(0).getValue() );
        List<JavaType> actualTypeArguments = getActualTypeArguments( base );
        if ( !actualTypeArguments.isEmpty() )
        {
            result.append( "<" );
            for ( Iterator<JavaType> iter = actualTypeArguments.iterator(); iter.hasNext(); )
            {
                result.append( getGenericValue( iter.next(), typeParameters) );
                if ( iter.hasNext() )
                {
                    result.append( "," );
                }
            }
            result.append( ">" );
        }
        if( base instanceof JavaClass )
        {
            for ( int i = 0; i < ( (JavaClass) base ).getDimensions(); i++ )
            {
                result.append( "[]" );
            }
        }
        return result.toString();
    }

    protected static <D extends JavaGenericDeclaration> String getResolvedGenericFullyQualifiedName( JavaType base, List<TypeVariable<D>> typeParameters )
    {
        StringBuffer result = new StringBuffer();
        TypeVariable<D> variable = resolve( base, typeParameters );
        result.append( variable == null ? base.getFullyQualifiedName() : variable.getBounds().get(0).getFullyQualifiedName() );
        List<JavaType> actualTypeArguments = getActualTypeArguments( base );
        if ( !actualTypeArguments.isEmpty() )
        {
            result.append( "<" );
            for ( Iterator<JavaType> iter = actualTypeArguments.iterator(); iter.hasNext(); )
            {
                result.append( getResolvedFullyQualifiedName( iter.next(), typeParameters) );
                if ( iter.hasNext() )
                {
                    result.append( "," );
                }
            }
            result.append( ">" );
        }
        if ( base instanceof JavaClass )
        {
            for ( int i = 0; i < ( (JavaClass) base ).getDimensions(); i++ )
            {
                result.append( "[]" );
            }
        }
        return result.toString();
    }

    protected static <D extends JavaGenericDeclaration> String getResolvedFullyQualifiedName( JavaType base, List<TypeVariable<D>> typeParameters )
    {
        TypeVariable<D> variable = resolve( base, typeParameters );
        return (variable == null ? base.getFullyQualifiedName() : variable.getBounds().get(0).getFullyQualifiedName() );
    }

    //Delegating methods

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaModel#getSource()
     */
    public JavaSource getSource()
    {
        return resolveRealClass().getSource();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaModel#getLineNumber()
     */
    public int getLineNumber()
    {
        return resolveRealClass().getLineNumber();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#isInterface()
     */
    public boolean isInterface()
    {
        return resolveRealClass().isInterface();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaAnnotatedElement#getAnnotations()
     */
    public List<Annotation> getAnnotations()
    {
        return resolveRealClass().getAnnotations();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#isEnum()
     */
    public boolean isEnum()
    {
        return resolveRealClass().isEnum();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaAnnotatedElement#getComment()
     */
    public String getComment()
    {
        return resolveRealClass().getComment();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaAnnotatedElement#getTags()
     */
    public List<DocletTag> getTags()
    {
        return resolveRealClass().getTags();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#isAnnotation()
     */
    public boolean isAnnotation()
    {
        return resolveRealClass().isAnnotation();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaAnnotatedElement#getTagsByName(java.lang.String)
     */
    public List<DocletTag> getTagsByName( String name )
    {
        return resolveRealClass().getTagsByName( name );
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaAnnotatedElement#getTagByName(java.lang.String)
     */
    public DocletTag getTagByName( String name )
    {
        return resolveRealClass().getTagByName( name );
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getSuperClass()
     */
    public JavaType getSuperClass()
    {
        return resolveRealClass().getSuperClass();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getSuperJavaClass()
     */
    public JavaClass getSuperJavaClass()
    {
        return resolveRealClass().getSuperJavaClass();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getImplements()
     */
    public List<JavaType> getImplements()
    {
        return resolveRealClass().getImplements();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getImplementedInterfaces()
     */
    public List<JavaClass> getImplementedInterfaces()
    {
        return resolveRealClass().getImplementedInterfaces();
    }
    
    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getInterfaces()
     */
    public List<JavaClass> getInterfaces()
    {
        return resolveRealClass().getImplementedInterfaces();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaAnnotatedElement#getNamedParameter(java.lang.String, java.lang.String)
     */
    public String getNamedParameter( String tagName, String parameterName )
    {
        return resolveRealClass().getNamedParameter( tagName, parameterName );
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getCodeBlock()
     */
    public String getCodeBlock()
    {
        return resolveRealClass().getCodeBlock();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaGenericDeclaration#getTypeParameters()
     */
    public <D extends JavaGenericDeclaration> List<TypeVariable<D>> getTypeParameters()
    {
        return resolveRealClass().getTypeParameters();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getParentSource()
     */
    public JavaSource getParentSource()
    {
        return resolveRealClass().getParentSource();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getPackage()
     */
    public JavaPackage getPackage()
    {
        return resolveRealClass().getPackage();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getParent()
     */
    public JavaClassParent getParent()
    {
        return context;
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getPackageName()
     */
    public String getPackageName()
    {
        return resolveRealClass().getPackageName();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#isInner()
     */
    public boolean isInner()
    {
        return resolveRealClass().isInner();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#resolveType(java.lang.String)
     */
    public String resolveType( String name )
    {
        return resolveRealClass().resolveType( name );
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#resolveCanonicalName(java.lang.String)
     */
    public String resolveCanonicalName( String name )
    {
        return resolveRealClass().resolveCanonicalName( name );
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#resolveFullyQualifiedName(java.lang.String)
     */
    public String resolveFullyQualifiedName( String name )
    {
        return resolveRealClass().resolveFullyQualifiedName( name );
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getClassNamePrefix()
     */
    public String getClassNamePrefix()
    {
        return resolveRealClass().getClassNamePrefix();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#asType()
     */
    public Type asType()
    {
        return resolveRealClass().asType();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getMethods()
     */
    public List<JavaMethod> getMethods()
    {
        return resolveRealClass().getMethods();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getConstructors()
     */
    public List<JavaConstructor> getConstructors()
    {
        return resolveRealClass().getConstructors();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getConstructor(java.util.List)
     */
    public JavaConstructor getConstructor( List<Type> parameterTypes )
    {
        return resolveRealClass().getConstructor( parameterTypes );
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getConstructor(java.util.List, boolean)
     */
    public JavaConstructor getConstructor( List<Type> parameterTypes, boolean varArg )
    {
        return resolveRealClass().getConstructor( parameterTypes, varArg );
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getMethods(boolean)
     */
    public List<JavaMethod> getMethods( boolean superclasses )
    {
        return resolveRealClass().getMethods( superclasses );
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getMethodBySignature(java.lang.String, java.util.List)
     */
    public JavaMethod getMethodBySignature( String name, List<Type> parameterTypes )
    {
        return resolveRealClass().getMethodBySignature( name, parameterTypes );
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getMethod(java.lang.String, java.util.List, boolean)
     */
    public JavaMethod getMethod( String name, List<Type> parameterTypes, boolean varArgs )
    {
        return resolveRealClass().getMethod( name, parameterTypes, varArgs );
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getMethodBySignature(java.lang.String, java.util.List, boolean)
     */
    public JavaMethod getMethodBySignature( String name, List<Type> parameterTypes, boolean superclasses )
    {
        return resolveRealClass().getMethodBySignature( name, parameterTypes, superclasses );
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getMethodBySignature(java.lang.String, java.util.List, boolean, boolean)
     */
    public JavaMethod getMethodBySignature( String name, List<Type> parameterTypes, boolean superclasses, boolean varArg )
    {
        return resolveRealClass().getMethodBySignature( name, parameterTypes, superclasses, varArg );
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getMethodsBySignature(java.lang.String, java.util.List, boolean)
     */
    public List<JavaMethod> getMethodsBySignature( String name, List<Type> parameterTypes, boolean superclasses )
    {
        return resolveRealClass().getMethodsBySignature( name, parameterTypes, superclasses );
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getMethodsBySignature(java.lang.String, java.util.List, boolean, boolean)
     */
    public List<JavaMethod> getMethodsBySignature( String name, List<Type> parameterTypes, boolean superclasses,
                                                   boolean varArg )
    {
        return resolveRealClass().getMethodsBySignature( name, parameterTypes, superclasses, varArg );
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getFields()
     */
    public List<JavaField> getFields()
    {
        return resolveRealClass().getFields();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getFieldByName(java.lang.String)
     */
    public JavaField getFieldByName( String name )
    {
        return resolveRealClass().getFieldByName( name );
    }
    
    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getEnumConstants()
     */
    public List<JavaField> getEnumConstants()
    {
        return resolveRealClass().getEnumConstants();
    }
    
    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getEnumConstantByName(java.lang.String)
     */
    public JavaField getEnumConstantByName( String name )
    {
        return resolveRealClass().getEnumConstantByName( name );
    }
    
    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getClasses()
     */
    public List<JavaClass> getClasses()
    {
        return resolveRealClass().getClasses();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getNestedClasses()
     */
    public List<JavaClass> getNestedClasses()
    {
        return resolveRealClass().getNestedClasses();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getNestedClassByName(java.lang.String)
     */
    public JavaClass getNestedClassByName( String name )
    {
        return resolveRealClass().getNestedClassByName( name );
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#isA(java.lang.String)
     */
    public boolean isA( String fullClassName )
    {
        return resolveRealClass().isA( fullClassName );
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#isA(com.thoughtworks.qdox.model.JavaClass)
     */
    public boolean isA( JavaClass javaClass )
    {
        return resolveRealClass().isA( javaClass );
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getBeanProperties()
     */
    public List<BeanProperty> getBeanProperties()
    {
        return resolveRealClass().getBeanProperties();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getBeanProperties(boolean)
     */
    public List<BeanProperty> getBeanProperties( boolean superclasses )
    {
        return resolveRealClass().getBeanProperties( superclasses );
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getBeanProperty(java.lang.String)
     */
    public BeanProperty getBeanProperty( String propertyName )
    {
        return resolveRealClass().getBeanProperty( propertyName );
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getBeanProperty(java.lang.String, boolean)
     */
    public BeanProperty getBeanProperty( String propertyName, boolean superclasses )
    {
        return resolveRealClass().getBeanProperty( propertyName, superclasses );
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getDerivedClasses()
     */
    public List<JavaClass> getDerivedClasses()
    {
        return resolveRealClass().getDerivedClasses();
    }
    
    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getTagsByName(java.lang.String, boolean)
     */
    public List<DocletTag> getTagsByName( String name, boolean superclasses )
    {
        return resolveRealClass().getTagsByName( name, superclasses );
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getJavaClassLibrary()
     */
    public ClassLibrary getJavaClassLibrary()
    {
        return context.getJavaClassLibrary();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getName()
     */
    public String getName()
    {
        return resolveRealClass().getName();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getCanonicalName()
     */
    public String getCanonicalName()
    {
        return resolveRealClass().getCanonicalName();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getModifiers()
     */
    public List<String> getModifiers()
    {
        return resolveRealClass().getModifiers();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#isPublic()
     */
    public boolean isPublic()
    {
        return resolveRealClass().isPublic();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#isProtected()
     */
    public boolean isProtected()
    {
        return resolveRealClass().isProtected();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#isPrivate()
     */
    public boolean isPrivate()
    {
        return resolveRealClass().isPrivate();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#isFinal()
     */
    public boolean isFinal()
    {
        return resolveRealClass().isFinal();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#isStatic()
     */
    public boolean isStatic()
    {
        return resolveRealClass().isStatic();
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#isAbstract()
     */
    public boolean isAbstract()
    {
        return resolveRealClass().isAbstract();
    }

}
