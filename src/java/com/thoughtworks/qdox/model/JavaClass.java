package com.thoughtworks.qdox.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
    private Type[] implementz = new Type[0];
    private TypeVariable[] typeParameters = TypeVariable.EMPTY_ARRAY; 
    
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

    public Type[] getImplements() {
        return implementz;
    }

    /**
     * @since 1.3
     */
    public JavaClass[] getImplementedInterfaces() {
        Type[] type = getImplements();
        JavaClass[] result = new JavaClass[type.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = type[i].getJavaClass();
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

    public void setImplementz(Type[] implementz) {
        this.implementz = implementz;
    }
    
    public TypeVariable[] getTypeParameters()
    {
        return typeParameters;
    }
    
    public void setTypeParameters( TypeVariable[] typeParameters )
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
        JavaClass[] innerClasses = getNestedClasses();
        for (int i = 0; i < innerClasses.length; i++) {
            if (innerClasses[i].getName().equals(typeName)) {
                return innerClasses[i].getFullyQualifiedName();
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

    public JavaMethod[] getMethods() {
        return methods.toArray( new JavaMethod[0] );
    }

    /**
     * @since 1.3
     */
    public JavaMethod[] getMethods(boolean superclasses) {
        if (superclasses) {
            Set<String> signatures = new HashSet<String>();
            List<JavaMethod> methods = new ArrayList<JavaMethod>();

            addMethodsFromSuperclassAndInterfaces(signatures, methods, this);

            return (JavaMethod[]) methods.toArray(new JavaMethod[methods.size()]);
        } else {
            return getMethods();
        }
    }

    private void addMethodsFromSuperclassAndInterfaces(Set<String> signatures,
                                                       List<JavaMethod> methodList, JavaClass callingClazz) {
        JavaMethod[] methods = callingClazz.getMethods();

        addNewMethods(signatures, methodList, methods);

        JavaClass superclass = callingClazz.getSuperJavaClass();

        // TODO workaround for a bug in getSuperJavaClass
        if ((superclass != null) && (superclass != callingClazz)) {
            callingClazz.addMethodsFromSuperclassAndInterfaces(signatures, methodList,
                    superclass);
        }

        JavaClass[] implementz = callingClazz.getImplementedInterfaces();

        for (int i = 0; i < implementz.length; i++) {
            if (implementz[i] != null) {
                callingClazz.addMethodsFromSuperclassAndInterfaces(signatures, methodList,
                        implementz[i]);
            }
        }
    }

    private void addNewMethods(Set<String> signatures, List<JavaMethod> methodList,
                               JavaMethod[] methods) {
        for (int i = 0; i < methods.length; i++) {
            JavaMethod method = methods[i];

            if (!method.isPrivate()) {
                String signature = method.getDeclarationSignature(false);

                if (!signatures.contains(signature)) {
                    methodList.add( new JavaMethodDelegate( this, method ) );
                    signatures.add(signature);
                }
            }
        }
    }

    /**
     * 
     * @param name           method name
     * @param parameterTypes parameter types or null if there are no parameters.
     * @return the matching method or null if no match is found.
     */
    public JavaMethod getMethodBySignature(String name, Type[] parameterTypes) {
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
    public JavaMethod getMethod(String name, Type[] parameterTypes, boolean varArgs) {
        JavaMethod[] methods = getMethods();

        for (int i = 0; i < methods.length; i++) {
            if (methods[i].signatureMatches(name, parameterTypes, varArgs)) {
                return methods[i];
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
    public JavaMethod getMethodBySignature(String name, Type[] parameterTypes,
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
    public JavaMethod getMethodBySignature(String name, Type[] parameterTypes,
                                           boolean superclasses, boolean varArg) {
        
        JavaMethod[] result = getMethodsBySignature(name, parameterTypes,
                superclasses, varArg);

        return (result.length > 0) ? result[0] : null;
    }
    
    /**
     * 
     * @param name
     * @param parameterTypes
     * @param superclasses
     * @return
     */
    public JavaMethod[] getMethodsBySignature(String name,
                                              Type[] parameterTypes, boolean superclasses) {
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
    public JavaMethod[] getMethodsBySignature(String name,
                                              Type[] parameterTypes, boolean superclasses, boolean varArg) {
        List<JavaMethod> result = new ArrayList<JavaMethod>();

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

            JavaClass[] implementz = getImplementedInterfaces();

            for (int i = 0; i < implementz.length; i++) {
                JavaMethod method = implementz[i].getMethodBySignature(name,
                        parameterTypes, true, varArg );

                if (method != null) {
                    result.add( new JavaMethodDelegate( this, method ) );
                }
            }
        }

        return result.toArray(new JavaMethod[result.size()]);
    }

    public JavaField[] getFields() {
        return fields.toArray( new JavaField[0] );
    }

    public JavaField getFieldByName(String name) {
        JavaField[] fields = getFields();

        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName().equals(name)) {
                return fields[i];
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
    public JavaClass[] getClasses() {
        return getNestedClasses();
    }

    /**
     * @since 1.3
     */
    public JavaClass[] getNestedClasses() {
        return classes.toArray(new JavaClass[0]);
    }

    public JavaClass getNestedClassByName(String name) {
        JavaClass[] classes = getNestedClasses();
        
        int separatorIndex = name.indexOf('.');
        String directInnerClassName = (separatorIndex > 0 ? name.substring(0, separatorIndex) : name); 
        for (int i = 0; i < classes.length; i++) {
        	JavaClass jClass = classes[i];
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
     * @deprecated old name for {@link #getNestedClasses()} 
     */
    public JavaClass[] getInnerClasses() {
        return getNestedClasses();
    }

    /**
     * @deprecated old name for {@link #getNestedClassByName(String)} 
     */
    public JavaClass getInnerClassByName(String name) {
       return getNestedClassByName(name);
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
    public BeanProperty[] getBeanProperties() {
        return getBeanProperties(false);
    }

    /**
     * @since 1.3
     */
    public BeanProperty[] getBeanProperties(boolean superclasses) {
        Map<String, BeanProperty> beanPropertyMap = getBeanPropertyMap(superclasses);
        Collection<BeanProperty> beanPropertyCollection = beanPropertyMap.values();

        return beanPropertyCollection.toArray(new BeanProperty[beanPropertyCollection
                .size()]);
    }

    private Map<String, BeanProperty> getBeanPropertyMap(boolean superclasses) {
        JavaMethod[] methods = getMethods(superclasses);
        Map<String, BeanProperty> beanPropertyMap = new LinkedHashMap<String, BeanProperty>();

        // loop over the methods.
        for (int i = 0; i < methods.length; i++) {
            JavaMethod method = methods[i];

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
    public JavaClass[] getDerivedClasses() {
        List<JavaClass> result = new ArrayList<JavaClass>();
        List<JavaClass> classes;
        if( source.getJavaClassLibrary() != null ) {
            classes = source.getJavaClassLibrary().getJavaClasses();
        }
        else {
            classes = new ArrayList<JavaClass>();
        }
        

        for (int i = 0; i < classes.size(); i++) {
            JavaClass clazz = classes.get(i);

            if (clazz.isA(this) && !(clazz == this)) {
                result.add(clazz);
            }
        }

        return result.toArray(new JavaClass[result.size()]);
    }

    public DocletTag[] getTagsByName(String name, boolean superclasses) {
        List<DocletTag> result = new ArrayList<DocletTag>();

        addTagsRecursive(result, this, name, superclasses);

        return result.toArray(new DocletTag[result.size()]);
    }

    private void addTagsRecursive(List<DocletTag> result, JavaClass javaClass,
                                  String name, boolean superclasses) {
        DocletTag[] tags = javaClass.getTagsByName(name);

        addNewTags(result, tags);

        if (superclasses) {
            JavaClass superclass = javaClass.getSuperJavaClass();

            // THIS IS A HACK AROUND A BUG THAT MUST BE SOLVED!!!
            // SOMETIMES A CLASS RETURNS ITSELF AS SUPER ?!?!?!?!?!
            if ((superclass != null) && (superclass != javaClass)) {
                addTagsRecursive(result, superclass, name, superclasses);
            }

            JavaClass[] implementz = javaClass.getImplementedInterfaces();

            for (int h = 0; h < implementz.length; h++) {
                if (implementz[h] != null) {
                    addTagsRecursive(result, implementz[h], name, superclasses);
                }
            }
        }
    }

    private void addNewTags(List<DocletTag> list, DocletTag[] tags) {
        for (int i = 0; i < tags.length; i++) {
            DocletTag superTag = tags[i];

            if (!list.contains(superTag)) {
                list.add(superTag);
            }
        }
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
