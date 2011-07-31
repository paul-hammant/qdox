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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.qdox.library.ClassLibrary;

/**
 * @author <a href="mailto:joew@thoughtworks.com">Joe Walnes</a>
 * @author Aslak Helles&oslash;y
 */
public class DefaultJavaClass extends AbstractInheritableJavaEntity implements JavaClass {

    private List<JavaConstructor> constructors = new LinkedList<JavaConstructor>();
    private List<JavaMethod> methods = new LinkedList<JavaMethod>();
    private List<JavaField> fields = new LinkedList<JavaField>();
    private List<JavaClass> classes = new LinkedList<JavaClass>();
    private boolean anInterface;
    private boolean anEnum;
    private boolean anAnnotation;

    private Type superClass;
    private List<Type> implementz = new LinkedList<Type>();
    private List<TypeVariable> typeParameters = new LinkedList<TypeVariable>(); 
    
    //sourceless class can use this property
	private JavaPackage javaPackage;
	
    protected DefaultJavaClass() {
    }
    
    public DefaultJavaClass(String name) {
        setName(name);
    }

    public DefaultJavaClass( JavaSource source )
    {
        setSource(source);
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#isInterface()
     */
    public boolean isInterface() {
        return anInterface;
    }
    
    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#isPrimitive()
     */
	public boolean isPrimitive() {
		final String name = getName();
		return "void".equals(name) || "boolean".equals(name)
				|| "byte".equals(name) || "char".equals(name)
				|| "short".equals(name) || "int".equals(name)
				|| "long".equals(name) || "float".equals(name)
				|| "double".equals(name);
	}

    public boolean isEnum() {
        return anEnum;
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#isAnnotation()
     */
    public boolean isAnnotation()
    {
        return anAnnotation;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getSuperClass()
     */
    public Type getSuperClass() {
        Type OBJECT_TYPE = getJavaClassLibrary().getJavaClass( "java.lang.Object" ).asType();
        Type ENUM_TYPE = getJavaClassLibrary().getJavaClass( "java.lang.Enum" ).asType();
        
        boolean iAmJavaLangObject = OBJECT_TYPE.equals(asType());

        if (anEnum) {
            return ENUM_TYPE;
        } else if (!anInterface && !anAnnotation && (superClass == null) && !iAmJavaLangObject) {
            return OBJECT_TYPE;
        }

        return superClass;
    }
    
    /**
     * Shorthand for getSuperClass().getJavaClass() with null checking.
     */
    public JavaClass getSuperJavaClass() {
        JavaClass result;
        JavaClass OBJECT_JAVACLASS = getJavaClassLibrary().getJavaClass( "java.lang.Object" );
        JavaClass ENUM_JAVACLASS = getJavaClassLibrary().getJavaClass( "java.lang.Enum" );
        
        boolean iAmJavaLangObject = OBJECT_JAVACLASS.equals(this);
        
        if (anEnum) {
            result = ENUM_JAVACLASS;
        } else if (!anInterface && !anAnnotation && (superClass == null) && !iAmJavaLangObject) {
            result = OBJECT_JAVACLASS;
        }
        else if(superClass != null) {
            result = superClass.getJavaClass();
        } else {
            result = null;
        }
        return result;
    }
    

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getImplements()
     */
    public List<Type> getImplements() {
        return implementz;
    }

    /**
     * @since 1.3
     */
    public List<JavaClass> getImplementedInterfaces() {
        List<JavaClass> result = new LinkedList<JavaClass>();
        for ( Type implType : getImplements() ) 
        {
            result.add( implType.getJavaClass() );
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getCodeBlock()
     */
    public String getCodeBlock()
    {
        return getModelWriter().writeClass( this ).toString();
    }
    
    public void setInterface(boolean anInterface) {
        this.anInterface = anInterface;
    }

    public void setEnum(boolean anEnum) {
        this.anEnum = anEnum;
    }

    public void setAnnotation(boolean anAnnotation) {
        this.anAnnotation = anAnnotation;
    }

    public void addConstructor( JavaConstructor constructor )
    {
        constructors.add( constructor );
    }

    public void addMethod(JavaMethod meth) {
        methods.add(meth);
    }

    public void setSuperClass(Type type) {
        if (anEnum) 
        {
            throw new IllegalArgumentException("enums cannot extend other classes");   
        }
        superClass = type;
    }

    public void setImplementz(List<Type> implementz) {
        this.implementz = implementz;
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getTypeParameters()
     */
    public List<TypeVariable> getTypeParameters()
    {
        return typeParameters;
    }
    
    public void setTypeParameters( List<TypeVariable> typeParameters )
    {
        this.typeParameters = typeParameters;
    }

    public void addField(JavaField javaField) {
        fields.add(javaField);
    }
    
    /**
     * Only used when constructing the model by hand / without source 
     * 
     * @param javaPackage
     */
    public void setJavaPackage(JavaPackage javaPackage) {
    	this.javaPackage = javaPackage;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getParentSource()
     */
    public JavaSource getParentSource() 
    {
        return (getParentClass() != null ? getParentClass().getParentSource() : super.getSource());
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getSource()
     */
    public JavaSource getSource()
    {
        return getParentSource();
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getPackage()
     */
    public JavaPackage getPackage() {
        return getParentSource() != null ? getParentSource().getPackage() : javaPackage;
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getParent()
     */
    public JavaClassParent getParent()
    {
        JavaClassParent result = getParentClass();
        if (result == null) 
        {
            result = getParentSource();
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getPackageName()
     */
    public String getPackageName()
    {
        JavaPackage pckg = getPackage();
        return ( pckg != null && pckg.getName() != null ) ? pckg.getName() : "";
    }

    /* 
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getFullyQualifiedName()
     */
    public String getFullyQualifiedName()
    {
        return ( getParentClass() != null  ? ( getParentClass().getClassNamePrefix() )
                        : getPackage() != null ? ( getPackage().getName() + "." ) : "" )
            + getName();
    }
    
    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getCanonicalName()
     */
    public String getCanonicalName()
    {
        return getFullyQualifiedName().replace( '$', '.' );
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getValue()
     */
    public String getValue()
    {
        return getName();
    }
    
    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getGenericValue()
     */
    public String getGenericValue()
    {
        return getValue();
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#isInner()
     */
    public boolean isInner()
    {
        return getParentClass() != null;
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#resolveType(java.lang.String)
     */
    public String resolveType( String typeName )
    {
        // Maybe it's an inner class?
        for ( JavaClass innerClass : getNestedClasses() )
        {
            if ( innerClass.getName().equals( typeName ) )
            {
                return innerClass.getFullyQualifiedName();
            }
        }
        return getParent().resolveType( typeName );
    }
    
    public String resolveCanonicalName( String name )
    {
        // Maybe it's an inner class?
        for ( JavaClass innerClass : getNestedClasses() )
        {
            if ( innerClass.getName().equals( name ) )
            {
                return innerClass.getName();
            }
        }
        return getParent().resolveCanonicalName( name );
    }
    
    public String resolveFullyQualifiedName( String name )
    {
        // Maybe it's an inner class?
        for ( JavaClass innerClass : getNestedClasses() )
        {
            if ( innerClass.getName().equals( name ) )
            {
                return innerClass.getFullyQualifiedName();
            }
        }
        return getParent().resolveFullyQualifiedName( name );
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getClassNamePrefix()
     */
    public String getClassNamePrefix()
    {
        return getFullyQualifiedName() + "$";
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#asType()
     */
    public Type asType() {
        return new Type(getFullyQualifiedName(), 0, this);
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getConstructors()
     */
    public List<JavaConstructor> getConstructors()
    {
        return constructors;
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getConstructor(java.util.List)
     */
    public JavaConstructor getConstructor( List<Type> parameterTypes )
    {
        return getConstructor( parameterTypes, false );
    }
    
    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getConstructor(java.util.List, boolean)
     */
    public JavaConstructor getConstructor( List<Type> parameterTypes, boolean varArgs )
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
    
    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getMethods()
     */
    public List<JavaMethod> getMethods()
    {
        return methods;
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getMethods(boolean)
     */
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
                result.put( signature, new JavaMethodDelegate( rootClass, method ) );
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
                    result.put( methodEntry.getKey(), new JavaMethodDelegate( superclass, methodEntry.getValue() ) );
                }
            }

        }

        for ( JavaClass clazz : callingClazz.getImplementedInterfaces() )
        {
            Map<String, JavaMethod> interfaceMethods = getMethodsFromSuperclassAndInterfaces( callingClazz, clazz );
            for ( Map.Entry<String, JavaMethod> methodEntry : interfaceMethods.entrySet() )
            {
                if ( !result.containsKey( methodEntry.getKey() ) )
                {
                    result.put( methodEntry.getKey(), new JavaMethodDelegate( clazz, methodEntry.getValue() ) );
                }
            }

        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getMethodBySignature(java.lang.String, java.util.List)
     */
    public JavaMethod getMethodBySignature(String name, List<Type> parameterTypes) {
        return getMethod( name, parameterTypes, false );
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getMethod(java.lang.String, java.util.List, boolean)
     */
    public JavaMethod getMethod(String name, List<Type> parameterTypes, boolean varArgs) {
        for (JavaMethod method : getMethods()) {
            if (method.signatureMatches(name, parameterTypes, varArgs)) {
                return method;
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getMethodBySignature(java.lang.String, java.util.List, boolean)
     */
    public JavaMethod getMethodBySignature(String name, List<Type> parameterTypes,
                                           boolean superclasses) {
        return getMethodBySignature( name, parameterTypes, superclasses, false );
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getMethodBySignature(java.lang.String, java.util.List, boolean, boolean)
     */
    public JavaMethod getMethodBySignature(String name, List<Type> parameterTypes,
                                           boolean superclasses, boolean varArg) {
        
        List<JavaMethod> result = getMethodsBySignature(name, parameterTypes,
                superclasses, varArg);

        return (result.size() > 0) ? result.get(0) : null;
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getMethodsBySignature(java.lang.String, java.util.List, boolean)
     */
    public List<JavaMethod> getMethodsBySignature(String name,
                                              List<Type> parameterTypes, boolean superclasses) {
        return getMethodsBySignature( name, parameterTypes, superclasses, false );
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getMethodsBySignature(java.lang.String, java.util.List, boolean, boolean)
     */
    public List<JavaMethod> getMethodsBySignature(String name,
                                              List<Type> parameterTypes, boolean superclasses, boolean varArg) {
        List<JavaMethod> result = new LinkedList<JavaMethod>();

        JavaMethod methodInThisClass = getMethod(name, parameterTypes, varArg);

        if (methodInThisClass != null) {
            result.add(methodInThisClass);
        }

        if (superclasses) {
            JavaClass superclass = getSuperJavaClass();

            if (superclass != null) {
                JavaMethod method = superclass.getMethodBySignature(name,
                        parameterTypes, true, varArg );

                // todo: ideally we should check on package privacy too. oh well.
                if ((method != null) && !method.isPrivate()) {
                    result.add( new JavaMethodDelegate( this, method ) );
                }
            }

            for (JavaClass clazz : getImplementedInterfaces()) {
                JavaMethod method = clazz.getMethodBySignature(name, parameterTypes, true, varArg );
                if (method != null) {
                    result.add( new JavaMethodDelegate( this, method ) );
                }
            }
        }

        return result;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getFields()
     */
    public List<JavaField> getFields() {
        return fields;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getFieldByName(java.lang.String)
     */
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

    public void addClass(JavaClass cls) {
        classes.add(cls);
    }

    /**
     * @deprecated Use {@link #getNestedClasses()} instead.
     */
    public List<JavaClass> getClasses() {
        return getNestedClasses();
    }

    /**
     * @since 1.3
     */
    public List<JavaClass> getNestedClasses() {
        return classes;
    }

    public JavaClass getNestedClassByName(String name) {
        int separatorIndex = name.indexOf('.');
        String directInnerClassName = (separatorIndex > 0 ? name.substring(0, separatorIndex) : name); 
        for (JavaClass jClass : getNestedClasses()) {
            if (jClass.getName().equals(directInnerClassName)) {
            	if(separatorIndex > 0) {
                    return jClass.getNestedClassByName(name.substring(separatorIndex+1));
            	}
            	else {
                    return jClass;
            	}
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#isA(java.lang.String)
     */
    public boolean isA(String fullClassName) {
        if(fullClassName == null) {
            return false;
        }
        if(fullClassName.equals(getFullyQualifiedName())) {
            return true;
        }
        for (JavaClass implementz : getImplementedInterfaces()) 
        {
            if (implementz.isA(fullClassName)) 
            {
                return true;
            }
        }
        JavaClass superClass = getSuperJavaClass();
        if (superClass != null) 
        {
            return superClass.isA(fullClassName);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#isA(com.thoughtworks.qdox.model.JavaClass)
     */
    public boolean isA(JavaClass javaClass) {
    	if (this == javaClass) 
    	{
    		return true;
    	}
    	else if (this.equals(javaClass)) 
    	{
            return true;
        } 
    	else if (javaClass != null) 
    	{
            // ask our interfaces
            for (JavaClass intrfc : getImplementedInterfaces()) 
            {
                if (intrfc.isA(javaClass)) 
                {
                    return true;
                }
            }
            // ask our superclass
            JavaClass superClass = getSuperJavaClass();
            if (superClass != null) 
            {
            	return superClass.isA(javaClass);
            }
        }
    	return false;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getBeanProperties()
     */
    public List<BeanProperty> getBeanProperties()
    {
        return getBeanProperties( false );
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getBeanProperties(boolean)
     */
    public List<BeanProperty> getBeanProperties( boolean superclasses )
    {
        Map<String, BeanProperty> beanPropertyMap = getBeanPropertyMap( superclasses );
        Collection<BeanProperty> beanPropertyCollection = beanPropertyMap.values();

        return new LinkedList<BeanProperty>( beanPropertyCollection );
    }

    private Map<String, BeanProperty> getBeanPropertyMap( boolean superclasses )
    {
        List<JavaMethod> superMethods = getMethods( superclasses );
        Map<String, BeanProperty> beanPropertyMap = new LinkedHashMap<String, BeanProperty>();

        // loop over the methods.
        for ( JavaMethod superMethod : superMethods )
        {
            if ( superMethod.isPropertyAccessor() )
            {
                String propertyName = superMethod.getPropertyName();
                BeanProperty beanProperty = getOrCreateProperty( beanPropertyMap, propertyName );

                beanProperty.setAccessor( superMethod );
                beanProperty.setType( superMethod.getPropertyType() );
            }
            else if ( superMethod.isPropertyMutator() )
            {
                String propertyName = superMethod.getPropertyName();
                BeanProperty beanProperty = getOrCreateProperty( beanPropertyMap, propertyName );

                beanProperty.setMutator( superMethod );
                beanProperty.setType( superMethod.getPropertyType() );
            }
        }

        return beanPropertyMap;
    }

    private BeanProperty getOrCreateProperty( Map<String, BeanProperty> beanPropertyMap, String propertyName )
    {
        BeanProperty result = beanPropertyMap.get( propertyName );

        if ( result == null )
        {
            result = new BeanProperty( propertyName );
            beanPropertyMap.put( propertyName, result );
        }

        return result;
    }

    /* 
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getBeanProperty(java.lang.String)
     */
    public BeanProperty getBeanProperty( String propertyName )
    {
        return getBeanProperty( propertyName, false );
    }

    /* 
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getBeanProperty(java.lang.String, boolean)
     */
    public BeanProperty getBeanProperty( String propertyName, boolean superclasses )
    {
        return getBeanPropertyMap( superclasses ).get( propertyName );
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getDerivedClasses()
     */
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

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getTagsByName(java.lang.String, boolean)
     */
    public List<DocletTag> getTagsByName( String name, boolean superclasses )
    {
        return getTagsRecursive( this, name, superclasses );
    }

    private List<DocletTag> getTagsRecursive(JavaClass javaClass, String name, boolean superclasses) {
        Set<DocletTag> result = new LinkedHashSet<DocletTag>();
        result.addAll(javaClass.getTagsByName(name));
        if (superclasses) {
            JavaClass superclass = javaClass.getSuperJavaClass();

            if ( superclass != null ) {
                result.addAll(getTagsRecursive(superclass, name, superclasses));
            }

            for (JavaClass intrfc : javaClass.getImplementedInterfaces()) {
                if (intrfc != null) {
                    result.addAll(getTagsRecursive(intrfc, name, superclasses));
                }
            }
        }
        return new LinkedList<DocletTag>(result);
    }

    /**
     * @see java.lang.Class#toString()
     */
    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
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
    
    @Override
    public int hashCode()
    {
        return getFullyQualifiedName().hashCode();
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

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getJavaClassLibrary()
     */
    public ClassLibrary getJavaClassLibrary()
    {
        return getSource().getJavaClassLibrary();
    }

}
