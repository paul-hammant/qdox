package com.thoughtworks.qdox.model;

import com.thoughtworks.qdox.JavaDocBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:joew@thoughtworks.com">Joe Walnes</a>
 * @author Aslak Helles&oslash;y
 */
public class JavaClass extends AbstractInheritableJavaEntity
        implements JavaClassParent {
    private static Type OBJECT = new Type("java.lang.Object");
    private List methods = new LinkedList();
    private JavaMethod[] methodsArray;
    private List fields = new LinkedList();
    private JavaField[] fieldsArray;
    private List classes = new LinkedList();
    private JavaClass[] classesArray;
    private boolean interfce;

    // Don't access this directly. Use asType() to get my Type
    private Type type;
    private Type superClass;
    private Type[] implementz = new Type[0];
    private final JavaClassParent parent;
    private JavaClassCache javaClassCache;

    public JavaClass(JavaClassParent parent) {
        this.parent = parent;
    }

    public void setJavaClassCache(JavaClassCache javaClassCache) {
        this.javaClassCache = javaClassCache;

        // reassign OBJECT. This will make it have a "source" too,
        // causing Type.getJavaClass() to return a JavaClass, instead
        // of null.
        OBJECT = javaClassCache.getClassByName("java.lang.Object").asType();
    }

    /**
     * Interface or class?
     */
    public boolean isInterface() {
        return interfce;
    }

    public Type getSuperClass() {
        boolean iAmJavaLangObject = OBJECT.equals(asType());

        if (!interfce && (superClass == null) && !iAmJavaLangObject) {
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

    protected void writeBody(IndentBuffer result) {
        writeAccessibilityModifier(result);
        writeNonAccessibilityModifiers(result);

        result.write(interfce ? "interface " : "class ");
        result.write(name);

        // subclass
        if (superClass != null) {
            result.write(" extends ");
            result.write(superClass.getValue());
        }

        // implements
        if (implementz.length > 0) {
            result.write(interfce ? " extends " : " implements ");

            for (int i = 0; i < implementz.length; i++) {
                if (i > 0) {
                    result.write(", ");
                }

                result.write(implementz[i].getValue());
            }
        }

        result.write(" {");
        result.newline();
        result.indent();

        // fields
        for (Iterator iterator = fields.iterator(); iterator.hasNext();) {
            JavaField javaField = (JavaField) iterator.next();

            result.newline();
            javaField.write(result);
        }

        // methods
        for (Iterator iterator = methods.iterator(); iterator.hasNext();) {
            JavaMethod javaMethod = (JavaMethod) iterator.next();

            result.newline();
            javaMethod.write(result);
        }

        // inner-classes
        for (Iterator iterator = classes.iterator(); iterator.hasNext();) {
            JavaClass javaClass = (JavaClass) iterator.next();

            result.newline();
            javaClass.write(result);
        }

        result.deindent();
        result.newline();
        result.write('}');
        result.newline();
    }

    public void setInterface(boolean interfce) {
        this.interfce = interfce;
    }

    public void addMethod(JavaMethod meth) {
        methods.add(meth);
        meth.setParentClass(this);
        methodsArray = null;
    }

    public void setSuperClass(Type type) {
        superClass = type;
    }

    public void setImplementz(Type[] implementz) {
        this.implementz = implementz;
    }

    public void addField(JavaField javaField) {
        fields.add(javaField);
        javaField.setParentClass(this);
        fieldsArray = null;
    }

    public JavaClassParent getParent() {
        return parent;
    }

    public JavaSource getParentSource() {
        JavaClassParent parent = getParent();

        return ((parent == null) ? null : parent.getParentSource());
    }

    public String getPackage() {
        return getParentSource().getPackage();
    }

    public String getFullyQualifiedName() {
        return getParent().getClassNamePrefix() + getName();
    }

    /**
     * @since 1.3
     */
    public boolean isInner() {
        return getParent() instanceof JavaClass;
    }

    public String resolveType(String typeName) {
        // Maybe it's an inner class?
        JavaClass[] innerClasses = getInnerClasses();
        for (int i = 0; i < innerClasses.length; i++) {
            if (innerClasses[i].getName().equals(typeName)) {
                return innerClasses[i].getFullyQualifiedName();
            }
        }

        return parent.resolveType(typeName);
    }

    public ClassLibrary getClassLibrary() {
        return parent.getClassLibrary();
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
        if (methodsArray == null) {
            methodsArray = new JavaMethod[methods.size()];
            methods.toArray(methodsArray);
        }

        return methodsArray;
    }

    /**
     * since 1.3
     */
    public JavaMethod[] getMethods(boolean superclasses) {
        if (superclasses) {
            Set signatures = new HashSet();
            List methods = new ArrayList();

            addMethodsFromSuperclassAndInterfaces(signatures, methods, this);

            return (JavaMethod[]) methods.toArray(new JavaMethod[methods.size()]);
        } else {
            return getMethods();
        }
    }

    private void addMethodsFromSuperclassAndInterfaces(Set signatures,
                                                       List methodList, JavaClass clazz) {
        JavaMethod[] methods = clazz.getMethods();

        addNewMethods(signatures, methodList, methods);

        JavaClass superclass = clazz.getSuperJavaClass();

        // TODO workaround for a bug in getSuperJavaClass
        if ((superclass != null) && (superclass != clazz)) {
            addMethodsFromSuperclassAndInterfaces(signatures, methodList,
                    superclass);
        }

        JavaClass[] implementz = clazz.getImplementedInterfaces();

        for (int i = 0; i < implementz.length; i++) {
            if (implementz[i] != null) {
                addMethodsFromSuperclassAndInterfaces(signatures, methodList,
                        implementz[i]);
            }
        }
    }

    private void addNewMethods(Set signatures, List methodList,
                               JavaMethod[] methods) {
        for (int i = 0; i < methods.length; i++) {
            JavaMethod method = methods[i];

            if (!method.isPrivate()) {
                String signature = method.getDeclarationSignature(false);

                if (!signatures.contains(signature)) {
                    methodList.add(method);
                    signatures.add(signature);
                }
            }
        }
    }

    /**
     * @param name method name
     * @param parameterTypes parameter types or null if there are no parameters.
     * @return the matching method or null if no match is found.
     */
    public JavaMethod getMethodBySignature(String name, Type[] parameterTypes) {
        JavaMethod[] methods = getMethods();

        for (int i = 0; i < methods.length; i++) {
            if (methods[i].signatureMatches(name, parameterTypes)) {
                return methods[i];
            }
        }

        return null;
    }

    public JavaMethod getMethodBySignature(String name, Type[] parameterTypes,
                                           boolean superclasses) {
        JavaMethod[] result = getMethodsBySignature(name, parameterTypes,
                superclasses);

        return (result.length > 0) ? result[0] : null;
    }

    public JavaMethod[] getMethodsBySignature(String name,
                                              Type[] parameterTypes, boolean superclasses) {
        List result = new ArrayList();

        JavaMethod methodInThisClass = getMethodBySignature(name, parameterTypes);

        if (methodInThisClass != null) {
            result.add(methodInThisClass);
        }

        if (superclasses) {
            JavaClass superclass = getSuperJavaClass();

            if (superclass != null) {
                JavaMethod method = superclass.getMethodBySignature(name,
                        parameterTypes, true);

                // todo: ideally we should check on package privacy too. oh well.
                if ((method != null) && !method.isPrivate()) {
                    result.add(method);
                }
            }

            JavaClass[] implementz = getImplementedInterfaces();

            for (int i = 0; i < implementz.length; i++) {
                JavaMethod method = implementz[i].getMethodBySignature(name,
                        parameterTypes, true);

                if (method != null) {
                    result.add(method);
                }
            }
        }

        return (JavaMethod[]) result.toArray(new JavaMethod[result.size()]);
    }

    public JavaField[] getFields() {
        if (fieldsArray == null) {
            fieldsArray = new JavaField[fields.size()];
            fields.toArray(fieldsArray);
        }

        return fieldsArray;
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
        classes.add(cls);
        classesArray = null;
    }

    /**
     * @deprecated Use {@link #getInnerClasses()} instead.
     */
    public JavaClass[] getClasses() {
        return getInnerClasses();
    }

    /**
     * @since 1.3
     */
    public JavaClass[] getInnerClasses() {
        if (classesArray == null) {
            classesArray = new JavaClass[classes.size()];
            classes.toArray(classesArray);
        }

        return classesArray;
    }

    public JavaClass getInnerClassByName(String name) {
        JavaClass[] classes = getInnerClasses();

        for (int i = 0; i < classes.length; i++) {
            if (classes[i].getName().equals(name)) {
                return classes[i];
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
     * @since 1.3
     */
    public BeanProperty[] getBeanProperties() {
        return getBeanProperties(false);
    }

    /**
     * @since 1.3
     */
    public BeanProperty[] getBeanProperties(boolean superclasses) {
        Map beanPropertyMap = getBeanPropertyMap(superclasses);
        Collection beanPropertyCollection = beanPropertyMap.values();

        return (BeanProperty[]) beanPropertyCollection.toArray(new BeanProperty[beanPropertyCollection
                .size()]);
    }

    private Map getBeanPropertyMap(boolean superclasses) {
        JavaMethod[] methods = getMethods(superclasses);
        Map beanPropertyMap = new HashMap();

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

    private BeanProperty getOrCreateProperty(Map beanPropertyMap,
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
        return (BeanProperty) getBeanPropertyMap(superclasses).get(propertyName);
    }

    /**
     * Gets the known derived classes. That is, subclasses or implementing classes.
     * @return
     */
    public JavaClass[] getDerivedClasses() {
        List result = new ArrayList();
        JavaDocBuilder builder = (JavaDocBuilder) javaClassCache;
        JavaClass[] classes = builder.getClasses();

        for (int i = 0; i < classes.length; i++) {
            JavaClass clazz = classes[i];

            if (clazz.isA(this) && !(clazz == this)) {
                result.add(clazz);
            }
        }

        return (JavaClass[]) result.toArray(new JavaClass[result.size()]);
    }

    public DocletTag[] getTagsByName(String name, boolean superclasses) {
        List result = new ArrayList();

        addTagsRecursive(result, this, name, superclasses);

        return (DocletTag[]) result.toArray(new DocletTag[result.size()]);
    }

    private void addTagsRecursive(List result, JavaClass javaClass,
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

    private void addNewTags(List list, DocletTag[] tags) {
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
}
