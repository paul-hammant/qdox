package com.thoughtworks.qdox.model.impl;

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
import com.thoughtworks.qdox.model.BeanProperty;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaConstructor;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaGenericDeclaration;
import com.thoughtworks.qdox.model.JavaInitializer;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaParameterizedType;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.JavaTypeVariable;
import com.thoughtworks.qdox.type.TypeResolver;

/**
 * The default implementation for {@link JavaType}
 * 
 * @author Robert Scholte
 * @since 2.0
 */
public class DefaultJavaType implements JavaClass, JavaType, Serializable {

    protected final String name;
    protected String fullName;
    private int dimensions;
    
    private TypeResolver typeResolver;
    
    DefaultJavaType( String name, TypeResolver typeResolver )
    {
        this.name = name;
        this.typeResolver = typeResolver;
    }

    DefaultJavaType(String fullName, String name, int dimensions, TypeResolver typeResolver) {
        this.fullName = fullName;
        this.name = name;
        this.dimensions = dimensions;
        this.typeResolver = typeResolver;
    }

    /**
     * Should only be used by primitives, since they don't have a classloader.
     * 
     * @param fullName the name of the primitive
     * @param dimensions number of dimensions
     */
    DefaultJavaType(String fullName, int dimensions) {
        this.name = fullName;
        this.fullName = fullName;
        this.dimensions = dimensions;
    }

    /**
     * Should only be used by primitives and wildcard, since they don't have a classloader.
     * 
     * @param fullName the name of the primitive or ?
     */
    DefaultJavaType( String fullName ) 
    {
        this( fullName, 0 );
    }
    
    /** {@inheritDoc} */
	public String getBinaryName()
	{
	    return resolveRealClass().getBinaryName(); 
	}
	
	/** {@inheritDoc} */
	public String getSimpleName()
	{
	    StringBuilder result = new StringBuilder( resolveRealClass().getSimpleName() );
        for (int i = 0; i < dimensions; i++) 
        {
            result.append("[]");
        }
        return result.toString();
	}
	
    /** {@inheritDoc} */
    public String getFullyQualifiedName() {
        StringBuilder result = new StringBuilder( resolveRealClass().getFullyQualifiedName() );
        for (int i = 0; i < dimensions; i++) 
        {
            result.append("[]");
        }
        return result.toString();
    }

    /** {@inheritDoc}*/
    public JavaClass getComponentType() {
      return isArray() ? resolveRealClass() : null;
    }
    
    /** {@inheritDoc}*/
    public String getValue() {
        StringBuilder result = new StringBuilder( name );
        for (int i = 0; i < dimensions; i++) 
        {
            result.append("[]");
        }
        return result.toString();
    }
    
    /** {@inheritDoc}*/
    public String getGenericValue()
    {
        StringBuilder result = new StringBuilder( getValue() );
        for ( int i = 0; i < dimensions; i++ )
        {
            result.append( "[]" );
        }
        return result.toString();
    }
    
    protected static <D extends JavaGenericDeclaration> String getGenericValue( JavaType base, List<JavaTypeVariable<D>> typeVariableList )
    {
        StringBuilder result = new StringBuilder( getResolvedValue( base, typeVariableList ) );
        for ( Iterator<JavaType> iter = getActualTypeArguments( base ).iterator(); iter.hasNext(); )
        {
            result.append( DefaultJavaType.resolve( base, typeVariableList ) );
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

    protected static <D extends JavaGenericDeclaration> String getResolvedValue( JavaType base, List<JavaTypeVariable<D>> typeParameters )
    {
        String result = base.getValue();
        for ( JavaTypeVariable<?> typeParameter : typeParameters )
        {
            if ( typeParameter.getName().equals( base.getValue() ) )
            {
                result = typeParameter.getBounds().get( 0 ).getValue();
                break;
            }
        }
        return result;
    }
    
    protected static <D extends JavaGenericDeclaration> JavaTypeVariable<D> resolve( JavaType base, List<JavaTypeVariable<D>> typeParameters )
    {
        JavaTypeVariable<D> result = null;
        // String result = getGenericValue(typeParameters);
        for ( JavaTypeVariable<D> typeParameter : typeParameters )
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
        if ( fullName == null && typeResolver != null )
        {
            fullName = typeResolver.resolveType( name );
        }
        return ( fullName != null );
    }

    /** {@inheritDoc} */
    public boolean isArray() {
        return dimensions > 0;
    }

    /** {@inheritDoc} */
    public int getDimensions() {
        return dimensions;
    }

    /**
     * Equivalent of {@link Class#toString()}. 
     * Converts the object to a string.
     * 
     * @return a string representation of this type.
     * @see Class#toString()
     */
    @Override
	public String toString()
    {
        return getFullyQualifiedName();
    }

    /**
     * Returns getGenericValue() extended with the array information
     * 
     * <pre>
     * Object &gt; java.lang.Object
     * Object[] &gt; java.lang.Object[]
     * List&lt;Object&gt; &gt; java.lang.List&lt;java.lang.Object&gt;
     * Outer.Inner &gt; Outer$Inner
     * Outer.Inner&lt;Object&gt;[][] &gt; Outer$Inner&lt;java.lang.Object&gt;[][] 
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
        if ( !( obj instanceof JavaType ) )
        {
            return false;
        }
        JavaType t = (JavaType) obj;
        return getFullyQualifiedName().equals( t.getFullyQualifiedName() );
    }

    @Override
    public int hashCode() {
        return getFullyQualifiedName().hashCode();
    }
    
    private JavaClass resolveRealClass() 
    {
        JavaClass result = null;
        String qualifiedName = isResolved() ? fullName : name;
        if ( isPrimitive( qualifiedName ) )
        {
            result = new DefaultJavaClass( qualifiedName );
        }
        else
        {
            result = typeResolver.getJavaClass( qualifiedName );
        }
        return result;
    }

    /**
     * @param type the type to match with
     * @return {@code true} if this type if of type, otherwise {@code false}
     * @since 1.3
     */
    public boolean isA( JavaType type )
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

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public boolean isVoid() {
        return "void".equals(getValue());
    }

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
     * @param base the base
     * @param declaringClass the declaring class
     * @param callingClass the calling class
     * @return the resolved type
     */
    protected static JavaType resolve( JavaType base, JavaClass declaringClass, JavaClass callingClass )
    {
        JavaType result = base;
        
        String concreteClassName;
        if ( base instanceof JavaClass )
        {
            JavaClass baseClass = (JavaClass) base;
            concreteClassName = ( baseClass.isArray() ?  baseClass.getComponentType().getFullyQualifiedName() : baseClass.getFullyQualifiedName() );
        }
        else
        {
            concreteClassName = base.getFullyQualifiedName();
        }

        int typeIndex = getTypeVariableIndex( declaringClass, concreteClassName );

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
                        JavaType actualType = getActualTypeArguments( implement ).get( typeIndex );
                        
                        TypeResolver typeResolver = TypeResolver.byPackageName( implement.getSource().getPackageName(),
                                                                                implement.getSource().getJavaClassLibrary(),
                                                                                implement.getSource().getImports() );
                        
                        JavaType resolvedType = new DefaultJavaType( actualType.getFullyQualifiedName(), actualType.getValue(), getDimensions( base ), typeResolver ); 
                        result = resolve( resolvedType , implement, implement );
                        break;
                    }
                    else
                    {
                        // no direct interface available, try indirect
                        result = resolve( base, implement, callingClass );
                    }
                }
            }
        }

        List<JavaType> actualTypeArguments = getActualTypeArguments(base); 
        if ( !actualTypeArguments.isEmpty() )
        {
            String value = base.getValue();
            if( value.indexOf( '[' ) > 0 )
            {
                value = value.substring( 0, value.indexOf( '[' ) );
            }
            
            TypeResolver typeResolver = TypeResolver.byPackageName(null, declaringClass.getJavaClassLibrary(), null );
            
            DefaultJavaParameterizedType typeResult =
                new DefaultJavaParameterizedType( concreteClassName, value, getDimensions( base ),
                                                  typeResolver );

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
    
    private static int getDimensions( JavaType type )
    {
        return type instanceof JavaClass ? ( (JavaClass) type ).getDimensions() : 0;
    }
    
    private static int getTypeVariableIndex( JavaClass declaringClass, String fqn )
    {
        int typeIndex = -1;
        for ( JavaTypeVariable<?> typeVariable : declaringClass.getTypeParameters() )
        {
            typeIndex++;
            if ( typeVariable.getFullyQualifiedName().equals( fqn ) )
            {
                return typeIndex;
            }
        }
        return -1;
    }

    /** {@inheritDoc} */
    public String getGenericFullyQualifiedName()
    {
        StringBuilder result = new StringBuilder( isResolved() ? fullName : name );
        for ( int i = 0; i < dimensions; i++ )
        {
            result.append( "[]" );
        }
        return result.toString();
    }
    
    /** {@inheritDoc} */
    public String getGenericCanonicalName()
    {
        StringBuilder result = new StringBuilder( getCanonicalName() );
        for ( int i = 0; i < dimensions; i++ )
        {
            result.append( "[]" );
        }
        return result.toString();
    }

    protected static <D extends JavaGenericDeclaration> String getResolvedGenericValue( JavaType base, List<JavaTypeVariable<D>> typeParameters )
    {
        StringBuilder result = new StringBuilder();
        JavaTypeVariable<?> variable = resolve( base, typeParameters );
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

    protected static <D extends JavaGenericDeclaration> String getResolvedGenericFullyQualifiedName( JavaType base, List<JavaTypeVariable<D>> typeParameters )
    {
        StringBuilder result = new StringBuilder();
        JavaTypeVariable<D> variable = resolve( base, typeParameters );
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

    protected static <D extends JavaGenericDeclaration> String getResolvedFullyQualifiedName( JavaType base, List<JavaTypeVariable<D>> typeParameters )
    {
        JavaTypeVariable<D> variable = resolve( base, typeParameters );
        return (variable == null ? base.getFullyQualifiedName() : variable.getBounds().get(0).getFullyQualifiedName() );
    }

    //Delegating methods

    /** {@inheritDoc} */
    public JavaSource getSource()
    {
        return resolveRealClass().getSource();
    }

    /** {@inheritDoc} */
    public int getLineNumber()
    {
        return resolveRealClass().getLineNumber();
    }

    /** {@inheritDoc} */
    public boolean isInterface()
    {
        return resolveRealClass().isInterface();
    }

    /** {@inheritDoc} */
    public List<JavaAnnotation> getAnnotations()
    {
        return resolveRealClass().getAnnotations();
    }

    /** {@inheritDoc} */
    public boolean isEnum()
    {
        return resolveRealClass().isEnum();
    }

    /** {@inheritDoc} */
    public String getComment()
    {
        return resolveRealClass().getComment();
    }

    /** {@inheritDoc} */
    public List<DocletTag> getTags()
    {
        return resolveRealClass().getTags();
    }

    /** {@inheritDoc} */
    public boolean isAnnotation()
    {
        return resolveRealClass().isAnnotation();
    }

    /** {@inheritDoc} */
    public List<DocletTag> getTagsByName( String name )
    {
        return resolveRealClass().getTagsByName( name );
    }

    /** {@inheritDoc} */
    public DocletTag getTagByName( String name )
    {
        return resolveRealClass().getTagByName( name );
    }

    /** {@inheritDoc} */
    public JavaType getSuperClass()
    {
        return resolveRealClass().getSuperClass();
    }

    /** {@inheritDoc} */
    public JavaClass getSuperJavaClass()
    {
        return resolveRealClass().getSuperJavaClass();
    }

    /** {@inheritDoc} */
    public List<JavaType> getImplements()
    {
        return resolveRealClass().getImplements();
    }

    /** {@inheritDoc} */
    public List<JavaClass> getInterfaces()
    {
        return resolveRealClass().getInterfaces();
    }

    /** {@inheritDoc} */
    public String getNamedParameter( String tagName, String parameterName )
    {
        return resolveRealClass().getNamedParameter( tagName, parameterName );
    }

    /** {@inheritDoc} */
    public String getCodeBlock()
    {
        return resolveRealClass().getCodeBlock();
    }

    /** {@inheritDoc} */
    public <D extends JavaGenericDeclaration> List<JavaTypeVariable<D>> getTypeParameters()
    {
        return resolveRealClass().getTypeParameters();
    }

    /** {@inheritDoc} */
    public JavaSource getParentSource()
    {
        return resolveRealClass().getParentSource();
    }

    /** {@inheritDoc} */
    public JavaPackage getPackage()
    {
        return resolveRealClass().getPackage();
    }

    /** {@inheritDoc} */
    public String getPackageName()
    {
        return resolveRealClass().getPackageName();
    }

    /** {@inheritDoc} */
    public boolean isInner()
    {
        return resolveRealClass().isInner();
    }

    /** {@inheritDoc} */
    public List<JavaInitializer> getInitializers()
    {
        return resolveRealClass().getInitializers();
    }

    /** {@inheritDoc} */
    public List<JavaMethod> getMethods()
    {
        return resolveRealClass().getMethods();
    }

    /** {@inheritDoc} */
    public List<JavaConstructor> getConstructors()
    {
        return resolveRealClass().getConstructors();
    }

    /** {@inheritDoc} */
    public JavaConstructor getConstructor( List<JavaType> parameterTypes )
    {
        return resolveRealClass().getConstructor( parameterTypes );
    }

    /** {@inheritDoc} */
    public JavaConstructor getConstructor( List<JavaType> parameterTypes, boolean varArg )
    {
        return resolveRealClass().getConstructor( parameterTypes, varArg );
    }

    /** {@inheritDoc} */
    public List<JavaMethod> getMethods( boolean superclasses )
    {
        return resolveRealClass().getMethods( superclasses );
    }

    /** {@inheritDoc} */
    public JavaMethod getMethodBySignature( String name, List<JavaType> parameterTypes )
    {
        return resolveRealClass().getMethodBySignature( name, parameterTypes );
    }

   /** {@inheritDoc} */
   public JavaMethod getMethod( String name, List<JavaType> parameterTypes, boolean varArgs )
    {
        return resolveRealClass().getMethod( name, parameterTypes, varArgs );
    }

   /** {@inheritDoc} */
    public JavaMethod getMethodBySignature( String name, List<JavaType> parameterTypes, boolean superclasses )
    {
        return resolveRealClass().getMethodBySignature( name, parameterTypes, superclasses );
    }

    /** {@inheritDoc} */
    public JavaMethod getMethodBySignature( String name, List<JavaType> parameterTypes, boolean superclasses, boolean varArg )
    {
        return resolveRealClass().getMethodBySignature( name, parameterTypes, superclasses, varArg );
    }

    /** {@inheritDoc} */
    public List<JavaMethod> getMethodsBySignature( String name, List<JavaType> parameterTypes, boolean superclasses )
    {
        return resolveRealClass().getMethodsBySignature( name, parameterTypes, superclasses );
    }

    /** {@inheritDoc} */
    public List<JavaMethod> getMethodsBySignature( String name, List<JavaType> parameterTypes, boolean superclasses,
                                                   boolean varArg )
    {
        return resolveRealClass().getMethodsBySignature( name, parameterTypes, superclasses, varArg );
    }

    /** {@inheritDoc} */
    public List<JavaField> getFields()
    {
        return resolveRealClass().getFields();
    }

    /** {@inheritDoc} */
    public JavaField getFieldByName( String name )
    {
        return resolveRealClass().getFieldByName( name );
    }
    
    /** {@inheritDoc} */
    public List<JavaField> getEnumConstants()
    {
        return resolveRealClass().getEnumConstants();
    }
    
    /** {@inheritDoc} */
    public JavaField getEnumConstantByName( String name )
    {
        return resolveRealClass().getEnumConstantByName( name );
    }
    
    /** {@inheritDoc} */
    public List<JavaClass> getNestedClasses()
    {
        return resolveRealClass().getNestedClasses();
    }

    /** {@inheritDoc} */
    public JavaClass getNestedClassByName( String name )
    {
        return resolveRealClass().getNestedClassByName( name );
    }

    /** {@inheritDoc} */
    public boolean isA( String fullClassName )
    {
        return resolveRealClass().isA( fullClassName );
    }

    /** {@inheritDoc} */
    public boolean isA( JavaClass javaClass )
    {
        return resolveRealClass().isA( javaClass );
    }

    /** {@inheritDoc} */
    public List<BeanProperty> getBeanProperties()
    {
        return resolveRealClass().getBeanProperties();
    }

    /** {@inheritDoc} */
    public List<BeanProperty> getBeanProperties( boolean superclasses )
    {
        return resolveRealClass().getBeanProperties( superclasses );
    }

    /** {@inheritDoc} */
    public BeanProperty getBeanProperty( String propertyName )
    {
        return resolveRealClass().getBeanProperty( propertyName );
    }

    /** {@inheritDoc} */
    public BeanProperty getBeanProperty( String propertyName, boolean superclasses )
    {
        return resolveRealClass().getBeanProperty( propertyName, superclasses );
    }

    /** {@inheritDoc} */
    public List<JavaClass> getDerivedClasses()
    {
        return resolveRealClass().getDerivedClasses();
    }
    
    /** {@inheritDoc} */
    public List<DocletTag> getTagsByName( String name, boolean superclasses )
    {
        return resolveRealClass().getTagsByName( name, superclasses );
    }

    /** {@inheritDoc} */
    public ClassLibrary getJavaClassLibrary()
    {
        return resolveRealClass().getJavaClassLibrary();
    }

    /** {@inheritDoc} */
    public String getName()
    {
        return resolveRealClass().getName();
    }

    /** {@inheritDoc} */
    public String getCanonicalName()
    {
        StringBuilder result = new StringBuilder( resolveRealClass().getCanonicalName() );
        for (int i = 0; i < dimensions; i++) 
        {
            result.append("[]");
        }
        return result.toString();
    }

    /** {@inheritDoc} */
    public List<String> getModifiers()
    {
        return resolveRealClass().getModifiers();
    }

    /** {@inheritDoc} */
    public boolean isPublic()
    {
        return resolveRealClass().isPublic();
    }

    /** {@inheritDoc} */
    public boolean isProtected()
    {
        return resolveRealClass().isProtected();
    }

    /** {@inheritDoc} */
    public boolean isPrivate()
    {
        return resolveRealClass().isPrivate();
    }

    /** {@inheritDoc} */
    public boolean isFinal()
    {
        return resolveRealClass().isFinal();
    }

    /** {@inheritDoc} */
    public boolean isStatic()
    {
        return resolveRealClass().isStatic();
    }

    /** {@inheritDoc} */
    public boolean isAbstract()
    {
        return resolveRealClass().isAbstract();
    }

    /** {@inheritDoc} */
    public JavaClass getDeclaringClass()
    {
        return resolveRealClass().getDeclaringClass();
    } 
}
