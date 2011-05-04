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

    private static Type OBJECT_TYPE;
    private static Type ENUM_TYPE;
    private static Type ANNOTATION = new Type("java.lang.annotation.Annotation");
    
    private static JavaClass OBJECT_JAVACLASS;
    private static JavaClass ENUM_JAVACLASS;

    private List<JavaConstructor> constructors = new LinkedList<JavaConstructor>();
    private List<JavaMethod> methods = new LinkedList<JavaMethod>();
    private List<JavaField> fields = new LinkedList<JavaField>();
    private List<JavaClass> classes = new LinkedList<JavaClass>();
    private boolean interfce;
    private boolean isEnum;
    private boolean isAnnotation;

    // Don't access this directly. Use asType() to get my Type
    private Type type;
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

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#isInterface()
     */
    public boolean isInterface() {
        return interfce;
    }
    
	public boolean isPrimitive() {
		String name = getName();
		return "void".equals(name) || "boolean".equals(name)
				|| "byte".equals(name) || "char".equals(name)
				|| "short".equals(name) || "int".equals(name)
				|| "long".equals(name) || "float".equals(name)
				|| "double".equals(name);
	}

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#isEnum()
     */
    public boolean isEnum() {
        return isEnum;
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#isAnnotation()
     */
    public boolean isAnnotation()
    {
        return isAnnotation;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getSuperClass()
     */
    public Type getSuperClass() {
        if(OBJECT_TYPE == null) {
            if(getSource().getJavaClassLibrary() != null) {
                OBJECT_TYPE = getSource().getJavaClassLibrary().getJavaClass( "java.lang.Object" ).asType();
                ENUM_TYPE = getSource().getJavaClassLibrary().getJavaClass( "java.lang.Enum" ).asType();
            }
        }
        
        boolean iAmJavaLangObject = OBJECT_TYPE.equals(asType());

        if (isEnum) {
            return ENUM_TYPE;
        } else if (!interfce && !isAnnotation && (superClass == null) && !iAmJavaLangObject) {
            return OBJECT_TYPE;
        }

        return superClass;
    }
    
    /**
     * Shorthand for getSuperClass().getJavaClass() with null checking.
     */
    public JavaClass getSuperJavaClass() {
        JavaClass result = null;
        if(OBJECT_JAVACLASS == null) {
            if(getSource().getJavaClassLibrary() != null) {
                OBJECT_JAVACLASS = getSource().getJavaClassLibrary().getJavaClass( "java.lang.Object" );
                ENUM_JAVACLASS = getSource().getJavaClassLibrary().getJavaClass( "java.lang.Enum" );
            }
        }
        
        boolean iAmJavaLangObject = OBJECT_JAVACLASS.equals(this);
        
        if (isEnum) {
            result = ENUM_JAVACLASS;
        } else if (!interfce && !isAnnotation && (superClass == null) && !iAmJavaLangObject) {
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
    
    public void setInterface(boolean interfce) {
        this.interfce = interfce;
    }

    public void setEnum(boolean isEnum) {
        this.isEnum = isEnum;
    }

    public void setAnnotation(boolean isAnnotation) {
        this.isAnnotation = isAnnotation;
    }

    public void addConstructor( JavaConstructor constructor )
    {
        constructors.add( constructor );
    }

    public void addMethod(JavaMethod meth) {
        methods.add(meth);
    }

    public void setSuperClass(Type type) {
        if (isEnum) 
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
    public JavaSource getParentSource() {
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
        if (result == null) {
            result = getParentSource();
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getPackageName()
     */
    public String getPackageName() {
        JavaPackage pckg = getPackage();
        return ( pckg != null && pckg.getName() != null ) ? pckg.getName() : "";
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getFullyQualifiedName()
     */
    public String getFullyQualifiedName() {
        return (getParentClass() != null ? (getParentClass().getClassNamePrefix()) : getPackage() != null ? (getPackage().getName()+".") : "") + getName();
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#isInner()
     */
    public boolean isInner() {
        return getParentClass() != null;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#resolveType(java.lang.String)
     */
    public String resolveType(String typeName) {
        // Maybe it's an inner class?
        for (JavaClass innerClass : getNestedClasses()) {
            if (innerClass.getName().equals(typeName)) {
                return innerClass.getFullyQualifiedName();
            }
        }
        return getParent().resolveType(typeName);
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getClassNamePrefix()
     */
    public String getClassNamePrefix() {
        return getFullyQualifiedName() + "$";
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#asType()
     */
    public Type asType() {
        if (type == null) {
            type = new Type(getFullyQualifiedName(), 0, this);
        }

        return type;
    }

    /**
     * @since 2.0
     */
    public List<JavaConstructor> getConstructors()
    {
        return constructors;
    }
    
    /**
     * @since 2.0
     */
    public JavaConstructor getConstructor( List<Type> parameterTypes )
    {
        return getConstructor( parameterTypes, false );
    }
    
    /**
     * @since 2.0
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
    public List<JavaMethod> getMethods() {
        return methods;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getMethods(boolean)
     */
    public List<JavaMethod> getMethods(boolean superclasses) {
        if (superclasses) {
            return new LinkedList<JavaMethod>(getMethodsFromSuperclassAndInterfaces(this, this).values());
        } else {
            return getMethods();
        }
    }
    
    private static Map<String, JavaMethod> getMethodsFromSuperclassAndInterfaces(JavaClass rootClass, JavaClass callingClazz) {

        Map<String, JavaMethod> result = new LinkedHashMap<String, JavaMethod>();
        
        for (JavaMethod method : callingClazz.getMethods()) {
            if (!method.isPrivate()) {
                String signature = method.getDeclarationSignature(false);
                result.put( signature, new JavaMethodDelegate( rootClass, method ) );
            }
        }

        JavaClass superclass = callingClazz.getSuperJavaClass();

        // TODO workaround for a bug in getSuperJavaClass
        if ((superclass != null) && (superclass != callingClazz)) {
            Map<String, JavaMethod> superClassMethods = getMethodsFromSuperclassAndInterfaces(callingClazz, superclass);
            for(Map.Entry<String, JavaMethod> methodEntry : superClassMethods.entrySet()) {
                if (!result.containsKey(methodEntry.getKey())) {
                    result.put( methodEntry.getKey(), new JavaMethodDelegate( superclass, methodEntry.getValue() ) );
                }
            }

        }

        for (JavaClass clazz : callingClazz.getImplementedInterfaces()) {
            Map<String, JavaMethod> interfaceMethods = getMethodsFromSuperclassAndInterfaces(callingClazz, clazz);
            for(Map.Entry<String, JavaMethod> methodEntry : interfaceMethods.entrySet()) {
                if (!result.containsKey(methodEntry.getKey())) {
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
    public JavaField getFieldByName(String name) {
        for ( JavaField field : getFields()) {
            if (field.getName().equals(name)) {
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

    /**
     * @since 1.3
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
            for (JavaClass implementz : getImplementedInterfaces()) 
            {
                if (implementz.isA(javaClass)) 
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
    public List<BeanProperty> getBeanProperties() {
        return getBeanProperties(false);
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getBeanProperties(boolean)
     */
    public List<BeanProperty> getBeanProperties(boolean superclasses) {
        Map<String, BeanProperty> beanPropertyMap = getBeanPropertyMap(superclasses);
        Collection<BeanProperty> beanPropertyCollection = beanPropertyMap.values();

        return new LinkedList<BeanProperty>(beanPropertyCollection);
    }

    private Map<String, BeanProperty> getBeanPropertyMap(boolean superclasses) {
        List<JavaMethod> methods = getMethods(superclasses);
        Map<String, BeanProperty> beanPropertyMap = new LinkedHashMap<String, BeanProperty>();

        // loop over the methods.
        for (JavaMethod method:methods) {
            if (method.isPropertyAccessor()) {
                String propertyName = method.getPropertyName();
                BeanProperty beanProperty = getOrCreateProperty(beanPropertyMap,
                        propertyName);

                beanProperty.setAccessor(method);
                beanProperty.setType(method.getPropertyType());
            } else if (method.isPropertyMutator()) {
                String propertyName = method.getPropertyName();
                BeanProperty beanProperty = getOrCreateProperty(beanPropertyMap,
                        propertyName);

                beanProperty.setMutator(method);
                beanProperty.setType(method.getPropertyType());
            }
        }

        return beanPropertyMap;
    }

    private BeanProperty getOrCreateProperty(Map<String, BeanProperty> beanPropertyMap,
                                             String propertyName) {
        BeanProperty result = (BeanProperty) beanPropertyMap.get(propertyName);

        if (result == null) {
            result = new BeanProperty(propertyName);
            beanPropertyMap.put(propertyName, result);
        }

        return result;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getBeanProperty(java.lang.String)
     */
    public BeanProperty getBeanProperty(String propertyName) {
        return getBeanProperty(propertyName, false);
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getBeanProperty(java.lang.String, boolean)
     */
    public BeanProperty getBeanProperty(String propertyName,
                                        boolean superclasses) {
        return getBeanPropertyMap(superclasses).get(propertyName);
    }

    /**
     * Gets the known derived classes. That is, subclasses or implementing classes.
     */
    public List<JavaClass> getDerivedClasses() {
        List<JavaClass> result = new LinkedList<JavaClass>();
        for (JavaClass clazz : getSource().getJavaClassLibrary().getJavaClasses()) {
            if (clazz.isA(this) && !(clazz == this)) {
                result.add(clazz);
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getTagsByName(java.lang.String, boolean)
     */
    public List<DocletTag> getTagsByName(String name, boolean superclasses) {
        return getTagsRecursive(this, name, superclasses);
    }

    private List<DocletTag> getTagsRecursive(JavaClass javaClass, String name, boolean superclasses) {
        Set<DocletTag> result = new LinkedHashSet<DocletTag>();
        result.addAll(javaClass.getTagsByName(name));
        if (superclasses) {
            JavaClass superclass = javaClass.getSuperJavaClass();

            // THIS IS A HACK AROUND A BUG THAT MUST BE SOLVED!!!
            // SOMETIMES A CLASS RETURNS ITSELF AS SUPER ?!?!?!?!?!
            if ((superclass != null) && (superclass != javaClass)) {
                result.addAll(getTagsRecursive(superclass, name, superclasses));
            }

            for (JavaClass implementz : javaClass.getImplementedInterfaces()) {
                if (implementz != null) {
                    result.addAll(getTagsRecursive(implementz, name, superclasses));
                }
            }
        }
        return new LinkedList<DocletTag>(result);
    }

    /**
     * @see http://java.sun.com/j2se/1.5.0/docs/api/java/lang/Class.html#toString()
     */
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	if(isPrimitive()) {
    		sb.append(getName());
    	}
    	else {
        	sb.append(isInterface() ? "interface" : "class");
        	sb.append(" ");
        	sb.append(getFullyQualifiedName());
    	}
    	return sb.toString();
    }
    
    @Override
    public int hashCode()
    {
        return getFullyQualifiedName().hashCode();
    }

    //ideally this shouldn't be required, but we must as long as Types can be created without classLibrary
    @Override
    public boolean equals(Object obj) {
    	if(obj == null) {
    		return false;
    	}
    	if(this == obj) {
    		return true;
    	}
    	if(!(obj instanceof JavaClass)) {
    		return false;
    	}
    	JavaClass clazz = (JavaClass) obj;
    	return this.getFullyQualifiedName().equals(clazz.getFullyQualifiedName());
    	
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaClass#getJavaClassLibrary()
     */
    public ClassLibrary getJavaClassLibrary()
    {
        return getSource().getJavaClassLibrary();
    }

}
