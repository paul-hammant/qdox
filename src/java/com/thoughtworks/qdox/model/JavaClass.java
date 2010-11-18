package com.thoughtworks.qdox.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
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
public class JavaClass extends AbstractInheritableJavaEntity implements JavaClassParent, JavaAnnotatedElement, JavaMember {

    private static Type OBJECT;
    private static Type ENUM;
    private static Type ANNOTATION = new Type("java.lang.annotation.Annotation");

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
	
	private JavaSource source;

    public JavaClass() {
    }
    
    public JavaClass(String name) {
        setName(name);
    }

    public JavaClass( JavaSource source )
    {
        this.source = source;
    }

    /**
     * is interface?  (otherwise enum or class)
     */
    public boolean isInterface() {
        return interfce;
    }

    /**
     * is enum?  (otherwise class or interface)
     */
    public boolean isEnum() {
        return isEnum;
    }
    
    /**
     * (don't know if this is required)
     * 
     * @return
     * @since 2.0 
     */
    public boolean isAnnotation()
    {
        return isAnnotation;
    }

    public Type getSuperClass() {
        if(OBJECT == null) {
            if(source.getJavaClassLibrary() != null) {
                OBJECT = source.getJavaClassLibrary().getJavaClass( "java.lang.Object" ).asType();
                ENUM = source.getJavaClassLibrary().getJavaClass( "java.lang.Enum" ).asType();
            }
        }
        
        boolean iAmJavaLangObject = OBJECT.equals(asType());

        if (isEnum) {
            return ENUM;
        } else if (!interfce && !isAnnotation && (superClass == null) && !iAmJavaLangObject) {
            return OBJECT;
        }

        return superClass;
    }
    
    /**
     * Shorthand for getSuperClass().getJavaClass() with null checking.
     */
    public JavaClass getSuperJavaClass() {
        if (getSuperClass() != null) {
            return getSuperClass().getJavaClass();
        } else {
            return null;
        }
    }

    public List<Type> getImplements() {
        return implementz;
    }

    /**
     * @since 1.3
     */
    public List<JavaClass> getImplementedInterfaces() {
        List<JavaClass> result = new LinkedList<JavaClass>();

        for (Type type : getImplements()) {
            result.add(type.getJavaClass());
        }

        return result;
    }

    public String getCodeBlock()
    {
        return getSource().getModelWriter().writeClass( this ).toString();
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

    public void addMethod(JavaMethod meth) {
        methods.add(meth);
    }

    public void setSuperClass(Type type) {
        if (isEnum) throw new IllegalArgumentException("enums cannot extend other classes");
        superClass = type;
    }

    public void setImplementz(List<Type> implementz) {
        this.implementz = implementz;
    }
    
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

    public void setSource( JavaSource source )
    {
        this.source = source;
    }
    
    public JavaSource getParentSource() {
        return (getParentClass() != null ? getParentClass().getParentSource() : source);
    }
    
    public JavaSource getSource()
    {
        return getParentSource();
    }

    public JavaPackage getPackage() {
        return getParentSource() != null ? getParentSource().getPackage() : javaPackage;
    }
    
    public JavaClassParent getParent()
    {
        JavaClassParent result = getParentClass();
        if (result == null) {
            result = getParentSource();
        }
        return result;
    }

    /**
     * If this class has a package, the packagename will be returned.
     * Otherwise an empty String.
     * 
     * @return
     */
    public String getPackageName() {
        JavaPackage javaPackage = getPackage();
        return (javaPackage != null && javaPackage.getName() != null) ? javaPackage.getName() : "";
    }

    public String getFullyQualifiedName() {
        return (getParentClass() != null ? (getParentClass().getClassNamePrefix()) : getPackage() != null ? (getPackage().getName()+".") : "") + getName();
    }

    /**
     * @since 1.3
     */
    public boolean isInner() {
        return getParentClass() != null;
    }

    public String resolveType(String typeName) {
        // Maybe it's an inner class?
        List<JavaClass> innerClasses = getNestedClasses();
        for (JavaClass innerClass : innerClasses) {
            if (innerClass.getName().equals(typeName)) {
                return innerClass.getFullyQualifiedName();
            }
        }

        return getParent().resolveType(typeName);
    }

    public String getClassNamePrefix() {
        return getFullyQualifiedName() + "$";
    }

    public Type asType() {
        if (type == null) {
            type = new Type(getFullyQualifiedName(), 0, this);
        }

        return type;
    }

    public List<JavaMethod> getMethods() {
        return methods;
    }

    /**
     * @since 1.3
     */
    public List<JavaMethod> getMethods(boolean superclasses) {
        if (superclasses) {
            return new LinkedList<JavaMethod>(getMethodsFromSuperclassAndInterfaces(this).values());
        } else {
            return getMethods();
        }
    }

    private Map<String, JavaMethod> getMethodsFromSuperclassAndInterfaces(JavaClass callingClazz) {

        Map<String, JavaMethod> result = new LinkedHashMap<String, JavaMethod>();
        
        for (JavaMethod method : callingClazz.getMethods()) {
            if (!method.isPrivate()) {
                String signature = method.getDeclarationSignature(false);
                result.put( signature, new JavaMethodDelegate( this, method ) );
            }
        }

        JavaClass superclass = callingClazz.getSuperJavaClass();

        // TODO workaround for a bug in getSuperJavaClass
        if ((superclass != null) && (superclass != callingClazz)) {
            Map<String, JavaMethod> superClassMethods = callingClazz.getMethodsFromSuperclassAndInterfaces(superclass);
            for(Map.Entry<String, JavaMethod> methodEntry : superClassMethods.entrySet()) {
                if (!result.containsKey(methodEntry.getKey())) {
                    result.put( methodEntry.getKey(), new JavaMethodDelegate( superclass, methodEntry.getValue() ) );
                }
            }

        }

        for (JavaClass clazz : callingClazz.getImplementedInterfaces()) {
            Map<String, JavaMethod> interfaceMethods = callingClazz.getMethodsFromSuperclassAndInterfaces(clazz);
            for(Map.Entry<String, JavaMethod> methodEntry : interfaceMethods.entrySet()) {
                if (!result.containsKey(methodEntry.getKey())) {
                    result.put( methodEntry.getKey(), new JavaMethodDelegate( clazz, methodEntry.getValue() ) );
                }
            }
            
        }
        return result;
    }

    /**
     * 
     * @param name           method name
     * @param parameterTypes parameter types or null if there are no parameters.
     * @return the matching method or null if no match is found.
     */
    public JavaMethod getMethodBySignature(String name, List<Type> parameterTypes) {
        return getMethod( name, parameterTypes, false );
    }

    /**
     * This should be the signature for getMethodBySignature
     * 
     * @param name
     * @param parameterTypes
     * @param varArgs
     * @return
     */
    public JavaMethod getMethod(String name, List<Type> parameterTypes, boolean varArgs) {
        for (JavaMethod method : getMethods()) {
            if (method.signatureMatches(name, parameterTypes, varArgs)) {
                return method;
            }
        }

        return null;
    }
    
    /**
     * 
     * @param name
     * @param parameterTypes
     * @param superclasses
     * @return
     */
    public JavaMethod getMethodBySignature(String name, List<Type> parameterTypes,
                                           boolean superclasses) {
        return getMethodBySignature( name, parameterTypes, superclasses, false );
    }
    
    /**
     * 
     * @param name
     * @param parameterTypes
     * @param superclasses
     * @param varArg
     * @return
     */
    public JavaMethod getMethodBySignature(String name, List<Type> parameterTypes,
                                           boolean superclasses, boolean varArg) {
        
        List<JavaMethod> result = getMethodsBySignature(name, parameterTypes,
                superclasses, varArg);

        return (result.size() > 0) ? result.get(0) : null;
    }
    
    /**
     * 
     * @param name
     * @param parameterTypes
     * @param superclasses
     * @return
     */
    public List<JavaMethod> getMethodsBySignature(String name,
                                              List<Type> parameterTypes, boolean superclasses) {
        return getMethodsBySignature( name, parameterTypes, superclasses, false );
    }

    /**
     * 
     * @param name
     * @param parameterTypes
     * @param superclasses
     * @param varArg
     * @return
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

    public List<JavaField> getFields() {
        return fields;
    }

    public JavaField getFieldByName(String name) {
        for ( JavaField field : getFields()) {
            if (field.getName().equals(name)) {
                return field;
            }
        }
        return null;
    }

    public void addClass(JavaClass cls) {
        cls.setParentClass( this );
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

    /**
     * @since 1.3
     */
    public boolean isA(String fullClassName) {
        Type type = new Type(fullClassName, 0, this);
        return asType().isA(type);
    }

    /**
     * @since 1.3
     */
    public boolean isA(JavaClass javaClass) {
        return asType().isA(javaClass.asType());
    }

    /**
     * Gets bean properties without looking in superclasses or interfaces.
     *
     * @since 1.3
     */
    public List<BeanProperty> getBeanProperties() {
        return getBeanProperties(false);
    }

    /**
     * @since 1.3
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

    /**
     * Gets bean property without looking in superclasses or interfaces.
     *
     * @since 1.3
     */
    public BeanProperty getBeanProperty(String propertyName) {
        return getBeanProperty(propertyName, false);
    }

    /**
     * @since 1.3
     */
    public BeanProperty getBeanProperty(String propertyName,
                                        boolean superclasses) {
        return getBeanPropertyMap(superclasses).get(propertyName);
    }

    /**
     * Gets the known derived classes. That is, subclasses or implementing classes.
     */
    public List<JavaClass> getDerivedClasses() {
        List<JavaClass> result;
        if( source.getJavaClassLibrary() != null ) {
            result = new LinkedList<JavaClass>();
            List<JavaClass> classes = source.getJavaClassLibrary().getJavaClasses();
            for (JavaClass clazz : classes) {
                if (clazz.isA(this) && !(clazz == this)) {
                    result.add(clazz);
                }
            }
        }
        else {
            result = Collections.emptyList();
        }
        return result;
    }

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

    public int compareTo(Object o) {
        return getFullyQualifiedName().compareTo(((JavaClass) o).getFullyQualifiedName());
    }

    /**
     * @see http://java.sun.com/j2se/1.5.0/docs/api/java/lang/Class.html#toString()
     */
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	if(asType().isPrimitive() || (Type.VOID.equals(asType()))) {
    		sb.append(asType().getValue());
    	}
    	else {
        	sb.append(isInterface() ? "interface" : "class");
        	sb.append(" ");
        	sb.append(getFullyQualifiedName());
    	}
    	return sb.toString();
    }

    public ClassLibrary getJavaClassLibrary()
    {
        //JavaClass should always have a source...
        if(source != null) {
            return source.getJavaClassLibrary();
        }
        else {
            return null;
        }
    }
}
