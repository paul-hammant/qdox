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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.model.BeanProperty;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaConstructor;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaInitializer;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.JavaType;

/**
 * @author <a href="mailto:joew@thoughtworks.com">Joe Walnes</a>
 * @author Aslak Helles&oslash;y
 */
public class DefaultJavaClass
    extends AbstractInheritableJavaEntity
    implements JavaClass
{

    private List<JavaConstructor> constructors = new LinkedList<JavaConstructor>();

    private List<JavaMethod> methods = new LinkedList<JavaMethod>();

    private List<JavaField> fields = new LinkedList<JavaField>();

    private List<JavaClass> classes = new LinkedList<JavaClass>();

    private boolean anInterface;

    private boolean anEnum;

    private boolean anAnnotation;

    private JavaType superClass;

    private List<JavaClass> implementz = new LinkedList<JavaClass>();
    
    private List<JavaInitializer> initializers = new LinkedList<JavaInitializer>();

    private List<DefaultJavaTypeVariable<JavaClass>> typeParameters =
        new LinkedList<DefaultJavaTypeVariable<JavaClass>>();

    // sourceless class can use this property
    private JavaPackage javaPackage;

    protected DefaultJavaClass()
    {
    }

    public DefaultJavaClass( String name )
    {
        setName( name );
    }

    public DefaultJavaClass( JavaSource source )
    {
        setSource( source );
    }

    /** {@inheritDoc} */
    public boolean isInterface()
    {
        return anInterface;
    }

    /** {@inheritDoc} */
    public boolean isPrimitive()
    {
        final String name = getName();
        return "void".equals( name ) || "boolean".equals( name ) || "byte".equals( name ) || "char".equals( name )
            || "short".equals( name ) || "int".equals( name ) || "long".equals( name ) || "float".equals( name )
            || "double".equals( name );
    }
    
    /** {@inheritDoc} */
    public boolean isVoid()
    {
        return "void".equals( getName() );
    }

    /** {@inheritDoc} */
    public boolean isEnum()
    {
        return anEnum;
    }

    /** {@inheritDoc} */
    public boolean isAnnotation()
    {
        return anAnnotation;
    }

    /** {@inheritDoc} */
    public boolean isArray()
    {
        return false;
    }

    /** {@inheritDoc} */
    public JavaClass getComponentType()
    {
        return null;
    }

    /** {@inheritDoc} */
    public int getDimensions()
    {
        return 0;
    }

    /** {@inheritDoc} */
    public JavaType getSuperClass()
    {
        JavaType result = null;

        if(isPrimitive()) {
            return null;
        }

        JavaClass OBJECT_JAVACLASS = getJavaClassLibrary().getJavaClass( "java.lang.Object" );
        JavaClass ENUM_JAVACLASS = getJavaClassLibrary().getJavaClass( "java.lang.Enum" );

        boolean iAmJavaLangObject = OBJECT_JAVACLASS.equals( this );

        if ( anEnum )
        {
            result = ENUM_JAVACLASS;
        }
        else if ( !anInterface && !anAnnotation && ( superClass == null ) && !iAmJavaLangObject )
        {
            result = OBJECT_JAVACLASS;
        }
        else 
        {
            result = superClass;
        }
        return result;
    }

    /** {@inheritDoc} */
    public JavaClass getSuperJavaClass()
    {
        JavaClass result = null;
        JavaType superType = getSuperClass();
        if ( superType instanceof JavaClass )
        {
            result = ( JavaClass ) superType;
        }
        return result;
    }

    /** {@inheritDoc} */
    public List<JavaType> getImplements()
    {
        return new LinkedList<JavaType>( implementz );
    }

    /** {@inheritDoc} */
    public List<JavaClass> getInterfaces()
    {
        return new LinkedList<JavaClass>( implementz );
    }

    /** {@inheritDoc} */
    public String getCodeBlock()
    {
        return getModelWriter().writeClass( this ).toString();
    }

    public void setInterface( boolean anInterface )
    {
        this.anInterface = anInterface;
    }

    public void setEnum( boolean anEnum )
    {
        this.anEnum = anEnum;
    }

    public void setAnnotation( boolean anAnnotation )
    {
        this.anAnnotation = anAnnotation;
    }

    public void addConstructor( JavaConstructor constructor )
    {
        constructors.add( constructor );
    }

    public void addMethod( JavaMethod meth )
    {
        methods.add( meth );
    }

    public void setSuperClass( JavaType type )
    {
        if ( anEnum )
        {
            throw new IllegalArgumentException( "enums cannot extend other classes" );
        }
        superClass = type;
    }

    public void setImplementz( List<JavaClass> implementz )
    {
        this.implementz = implementz;
    }

    /** {@inheritDoc} */
    public List<DefaultJavaTypeVariable<JavaClass>> getTypeParameters()
    {
        return typeParameters;
    }

    public void setTypeParameters( List<DefaultJavaTypeVariable<JavaClass>> typeParameters )
    {
        this.typeParameters = typeParameters;
    }

    public void addField( JavaField javaField )
    {
        fields.add( javaField );
    }

    /**
     * Only used when constructing the model by hand / without source
     * 
     * @param javaPackage the package
     */
    public void setJavaPackage( JavaPackage javaPackage )
    {
        this.javaPackage = javaPackage;
    }

    /** {@inheritDoc} */
    public JavaSource getParentSource()
    {
        return ( getDeclaringClass() != null ? getDeclaringClass().getParentSource() : super.getSource() );
    }

    /** {@inheritDoc} */
    @Override
	public JavaSource getSource()
    {
        return getParentSource();
    }

    /** {@inheritDoc} */
    public JavaPackage getPackage()
    {
        return getParentSource() != null ? getParentSource().getPackage() : javaPackage;
    }

    /** {@inheritDoc} */
    public String getPackageName()
    {
        JavaPackage pckg = getPackage();
        return ( pckg != null && pckg.getName() != null ) ? pckg.getName() : "";
    }

    /** {@inheritDoc} */
    public String getSimpleName()
    {
        return getName();
    }
    
    /** {@inheritDoc} */
    public String getBinaryName()
    {
        return ( getDeclaringClass() == null ? getCanonicalName() : getDeclaringClass().getBinaryName() + '$' + getSimpleName() ); 
    }
    
    /** {@inheritDoc} */
    public String getFullyQualifiedName()
    {
        if(isPrimitive())
        {
            return getName();
        }
        else if ( getDeclaringClass() == null )
        {
            return (getPackage() == null ? "" :  getPackage().getName() + '.') +getSimpleName(); 
        }
        else {
            return getDeclaringClass().getFullyQualifiedName() + "." + getSimpleName();
        }
    }

    /** {@inheritDoc} */
    public String getGenericFullyQualifiedName()
    {
        return getFullyQualifiedName();
    }

    /** {@inheritDoc} */
    public String getCanonicalName()
    {
        return getFullyQualifiedName().replace( '$', '.' );
    }

    /** {@inheritDoc} */
    public String getGenericCanonicalName()
    {
        return getCanonicalName();
    }

    /** {@inheritDoc} */
    public String getValue()
    {
        return getCanonicalName().substring( getSource().getClassNamePrefix().length() );
    }

    /** {@inheritDoc} */
    public String getGenericValue()
    {
        return getValue();
    }

    /** {@inheritDoc} */
    public boolean isInner()
    {
        return getDeclaringClass() != null;
    }

    /** {@inheritDoc} */
    public List<JavaInitializer> getInitializers()
    {
        return initializers;
    }

    /** {@inheritDoc} */
    public List<JavaConstructor> getConstructors()
    {
        return constructors;
    }

    /** {@inheritDoc} */
    public JavaConstructor getConstructor( List<JavaType> parameterTypes )
    {
        return getConstructor( parameterTypes, false );
    }

    /** {@inheritDoc} */
    public JavaConstructor getConstructor( List<JavaType> parameterTypes, boolean varArgs )
    {
        for ( JavaConstructor constructor : getConstructors() )
        {
            if ( constructor.signatureMatches( parameterTypes, varArgs ) )
            {
                return constructor;
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    public List<JavaMethod> getMethods()
    {
        return methods;
    }

    /** {@inheritDoc} */
    public List<JavaMethod> getMethods( boolean superclasses )
    {
        if ( superclasses )
        {
            return new LinkedList<JavaMethod>( getMethodsFromSuperclassAndInterfaces( this, this ).values() );
        }
        else
        {
            return getMethods();
        }
    }

    private static Map<String, JavaMethod> getMethodsFromSuperclassAndInterfaces( JavaClass rootClass,
                                                                                  JavaClass callingClazz )
    {

        Map<String, JavaMethod> result = new LinkedHashMap<String, JavaMethod>();

        for ( JavaMethod method : callingClazz.getMethods() )
        {
            if ( !method.isPrivate() )
            {
                String signature = method.getDeclarationSignature( false );
                
                result.put( signature, method );
            }
        }

        JavaClass superclass = callingClazz.getSuperJavaClass();
        if ( superclass != null )
        {
            Map<String, JavaMethod> superClassMethods =
                getMethodsFromSuperclassAndInterfaces( callingClazz, superclass );
            for ( Map.Entry<String, JavaMethod> methodEntry : superClassMethods.entrySet() )
            {
                if ( !result.containsKey( methodEntry.getKey() ) )
                {
                    JavaMethod method;
                    if ( superclass.equals( rootClass ) )
                    {
                        method = methodEntry.getValue();
                    }
                    else
                    {
                        method = new JavaMethodDelegate( callingClazz, methodEntry.getValue() );
                    }
                    result.put( methodEntry.getKey(), method );
                }
            }

        }

        for ( JavaClass clazz : callingClazz.getInterfaces() )
        {
            Map<String, JavaMethod> interfaceMethods = getMethodsFromSuperclassAndInterfaces( callingClazz, clazz );
            for ( Map.Entry<String, JavaMethod> methodEntry : interfaceMethods.entrySet() )
            {
                if ( !result.containsKey( methodEntry.getKey() ) )
                {
                    JavaMethod method;
                    if ( clazz.equals( rootClass ) )
                    {
                        method = methodEntry.getValue();
                    }
                    else
                    {
                        method = new JavaMethodDelegate( callingClazz, methodEntry.getValue() );
                    }
                    result.put( methodEntry.getKey(), method );
                }
            }

        }
        return result;
    }

    /** {@inheritDoc} */
    public JavaMethod getMethodBySignature( String name, List<JavaType> parameterTypes )
    {
        return getMethod( name, parameterTypes, false );
    }

    /** {@inheritDoc} */
    public JavaMethod getMethod( String name, List<JavaType> parameterTypes, boolean varArgs )
    {
        for ( JavaMethod method : getMethods() )
        {
            if ( method.signatureMatches( name, parameterTypes, varArgs ) )
            {
                return method;
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    public JavaMethod getMethodBySignature( String name, List<JavaType> parameterTypes, boolean superclasses )
    {
        return getMethodBySignature( name, parameterTypes, superclasses, false );
    }

    /** {@inheritDoc} */
    public JavaMethod getMethodBySignature( String name, List<JavaType> parameterTypes, boolean superclasses,
                                            boolean varArg )
    {

        List<JavaMethod> result = getMethodsBySignature( name, parameterTypes, superclasses, varArg );

        return ( result.size() > 0 ) ? result.get( 0 ) : null;
    }

    /** {@inheritDoc} */
    public List<JavaMethod> getMethodsBySignature( String name, List<JavaType> parameterTypes, boolean superclasses )
    {
        return getMethodsBySignature( name, parameterTypes, superclasses, false );
    }

    /** {@inheritDoc} */
    public List<JavaMethod> getMethodsBySignature( String name, List<JavaType> parameterTypes, boolean superclasses,
                                                   boolean varArg )
    {
        List<JavaMethod> result = new LinkedList<JavaMethod>();

        JavaMethod methodInThisClass = getMethod( name, parameterTypes, varArg );

        if ( methodInThisClass != null )
        {
            result.add( methodInThisClass );
        }

        if ( superclasses )
        {
            JavaClass superclass = getSuperJavaClass();

            if ( superclass != null )
            {
                JavaMethod method = superclass.getMethodBySignature( name, parameterTypes, true, varArg );

                // todo: ideally we should check on package privacy too. oh well.
                if ( ( method != null ) && !method.isPrivate() )
                {
                    result.add( new JavaMethodDelegate( this, method ) );
                }
            }

            for ( JavaClass clazz : getInterfaces() )
            {
                JavaMethod method = clazz.getMethodBySignature( name, parameterTypes, true, varArg );
                if ( method != null )
                {
                    result.add( new JavaMethodDelegate( this, method ) );
                }
            }
        }

        return result;
    }

    /** {@inheritDoc} */
    public List<JavaField> getFields()
    {
        return fields;
    }

    /** {@inheritDoc} */
    public JavaField getFieldByName( String name )
    {
        for ( JavaField field : getFields() )
        {
            if ( field.getName().equals( name ) )
            {
                return field;
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    public List<JavaField> getEnumConstants()
    {
        List<JavaField> result = isEnum() ? new LinkedList<JavaField>() : null;
        if ( isEnum() )
        {
            for ( JavaField field : getFields() )
            {
                if ( field.isEnumConstant() )
                {
                    result.add( field );
                }
            }
        }
        return result;
    }

    /** {@inheritDoc} */
    public JavaField getEnumConstantByName( String name )
    {
        JavaField field = getFieldByName( name );
        return field.isEnumConstant() ? field : null;
    }
    
    public void addInitializer( JavaInitializer initializer )
    {
        initializers.add( initializer );
    }

    public void addClass( JavaClass cls )
    {
        classes.add( cls );
    }

    /** {@inheritDoc} */
    public List<JavaClass> getNestedClasses()
    {
        return classes;
    }

    /** {@inheritDoc} */
    public JavaClass getNestedClassByName( String name )
    {
        
        int separatorIndex = name.indexOf( '.' );
        String directInnerClassName = ( separatorIndex > 0 ? name.substring( 0, separatorIndex ) : name );
        for ( JavaClass jClass : getNestedClasses() )
        {
            if ( jClass.getName().equals( directInnerClassName ) )
            {
                if ( separatorIndex > 0 )
                {
                    return jClass.getNestedClassByName( name.substring( separatorIndex + 1 ) );
                }
                else
                {
                    return jClass;
                }
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    public boolean isA( String fullClassName )
    {
        if ( fullClassName == null )
        {
            return false;
        }
        if ( fullClassName.equals( getFullyQualifiedName() ) )
        {
            return true;
        }
        for ( JavaClass implementz : getInterfaces() )
        {
            if ( implementz.isA( fullClassName ) )
            {
                return true;
            }
        }
        JavaClass superClass = getSuperJavaClass();
        if ( superClass != null )
        {
            return superClass.isA( fullClassName );
        }
        return false;
    }

    /** {@inheritDoc} */
    public boolean isA( JavaClass javaClass )
    {
        if ( this == javaClass )
        {
            return true;
        }
        else if ( this.equals( javaClass ) )
        {
            return true;
        }
        else if ( javaClass != null )
        {
            // ask our interfaces
            for ( JavaClass intrfc : getInterfaces() )
            {
                if ( intrfc.isA( javaClass ) )
                {
                    return true;
                }
            }
            // ask our superclass
            JavaClass superClass = getSuperJavaClass();
            if ( superClass != null )
            {
                return superClass.isA( javaClass );
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    public List<BeanProperty> getBeanProperties()
    {
        return getBeanProperties( false );
    }

    /** {@inheritDoc} */
    public List<BeanProperty> getBeanProperties( boolean superclasses )
    {
        Map<String, BeanProperty> beanPropertyMap = getBeanPropertyMap( superclasses );
        Collection<BeanProperty> beanPropertyCollection = beanPropertyMap.values();

        return new LinkedList<BeanProperty>( beanPropertyCollection );
    }

    private Map<String, BeanProperty> getBeanPropertyMap( boolean superclasses )
    {
        List<JavaMethod> superMethods = getMethods( superclasses );
        Map<String, DefaultBeanProperty> beanPropertyMap = new LinkedHashMap<String, DefaultBeanProperty>();

        // loop over the methods.
        for ( JavaMethod superMethod : superMethods )
        {
            if ( superMethod.isPropertyAccessor() )
            {
                String propertyName = superMethod.getPropertyName();
                DefaultBeanProperty beanProperty = getOrCreateProperty( beanPropertyMap, propertyName );

                beanProperty.setAccessor( superMethod );
                beanProperty.setType( superMethod.getPropertyType() );
            }
            else if ( superMethod.isPropertyMutator() )
            {
                String propertyName = superMethod.getPropertyName();
                DefaultBeanProperty beanProperty = getOrCreateProperty( beanPropertyMap, propertyName );

                beanProperty.setMutator( superMethod );
                beanProperty.setType( superMethod.getPropertyType() );
            }
        }
        return new LinkedHashMap<String, BeanProperty>( beanPropertyMap );
    }

    private DefaultBeanProperty getOrCreateProperty( Map<String, DefaultBeanProperty> beanPropertyMap,
                                                     String propertyName )
    {
        DefaultBeanProperty result = beanPropertyMap.get( propertyName );

        if ( result == null )
        {
            result = new DefaultBeanProperty( propertyName );
            beanPropertyMap.put( propertyName, result );
        }

        return result;
    }

    /** {@inheritDoc} */
    public BeanProperty getBeanProperty( String propertyName )
    {
        return getBeanProperty( propertyName, false );
    }

    /** {@inheritDoc} */
    public BeanProperty getBeanProperty( String propertyName, boolean superclasses )
    {
        return getBeanPropertyMap( superclasses ).get( propertyName );
    }

    /** {@inheritDoc} */
    public List<JavaClass> getDerivedClasses()
    {
        List<JavaClass> result = new LinkedList<JavaClass>();
        for ( JavaClass clazz : getSource().getJavaClassLibrary().getJavaClasses() )
        {
            if ( clazz.isA( this ) && !( clazz == this ) )
            {
                result.add( clazz );
            }
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
	public List<DocletTag> getTagsByName( String name, boolean superclasses )
    {
        return getTagsRecursive( this, name, superclasses );
    }

    private List<DocletTag> getTagsRecursive( JavaClass javaClass, String name, boolean superclasses )
    {
        Set<DocletTag> result = new LinkedHashSet<DocletTag>();
        result.addAll( javaClass.getTagsByName( name ) );
        if ( superclasses )
        {
            JavaClass superclass = javaClass.getSuperJavaClass();

            if ( superclass != null )
            {
                result.addAll( getTagsRecursive( superclass, name, superclasses ) );
            }

            for ( JavaClass intrfc : javaClass.getInterfaces() )
            {
                if ( intrfc != null )
                {
                    result.addAll( getTagsRecursive( intrfc, name, superclasses ) );
                }
            }
        }
        return new LinkedList<DocletTag>( result );
    }

    /**
     * @see java.lang.Class#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        if ( isPrimitive() )
        {
            sb.append( getName() );
        }
        else
        {
            sb.append( isInterface() ? "interface" : "class" );
            sb.append( " " );
            sb.append( getFullyQualifiedName() );
        }
        return sb.toString();
    }

    public String toGenericString()
    {
        return toString();
    }

    @Override
    public int hashCode()
    {
        return 2 + getFullyQualifiedName().hashCode();
    }

    // ideally this shouldn't be required, but we must as long as Types can be created without classLibrary
    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( !( obj instanceof JavaClass ) )
        {
            return false;
        }
        JavaClass clazz = (JavaClass) obj;
        return this.getFullyQualifiedName().equals( clazz.getFullyQualifiedName() );
    }

    /** {@inheritDoc} */
    public ClassLibrary getJavaClassLibrary()
    {
        return getSource().getJavaClassLibrary();
    }

}