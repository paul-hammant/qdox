package com.thoughtworks.qdox.model;

import com.thoughtworks.qdox.JavaDocBuilder;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;

/**
 * @author <a href="mailto:joew@thoughtworks.com">Joe Walnes</a>
 * @author Aslak Helles&oslash;y
 */
public class JavaClass extends AbstractJavaEntity implements JavaClassParent {

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
    private JavaClassParent parent;

    private BeanProperty[] beanProperties;
    private Map beanPropertyMap;
    private JavaClassCache javaClassCache;

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
        if (!interfce && superClass == null && !iAmJavaLangObject) {
            return OBJECT;
        }
        return superClass;
    }

    /**
     * Shorthand for getSuperClass().getJavaClass()
     * @return
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
                if (i > 0) result.write(", ");
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
        beanProperties = null;
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

    public void setParent(JavaClassParent parent) {
        this.parent = parent;
    }

    public JavaClassParent getParent() {
        return parent;
    }

    public JavaSource getParentSource() {
        JavaClassParent parent = getParent();
        return (parent == null ? null : parent.getParentSource());
    }

    public String getPackage() {
        return getParentSource().getPackage();
    }

    public String getFullyQualifiedName() {
        if (getParent() != null) {
            JavaClassParent parent = getParent();
            String pakkage = parent.asClassNamespace();
            char separator = isInner() ? '$' : '.';
            return pakkage == null ? getName() : pakkage + separator + getName();
        } else {
            return null;
        }
    }

    /**
     * @since 1.3
     */
    public boolean isInner() {
        return getParent() instanceof JavaClass;
    }

    public String asClassNamespace() {
        return getFullyQualifiedName();
    }

    public Type asType() {
        if (type == null) {
            type = new Type(getFullyQualifiedName(), 0, getParentSource());
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
     * @param name method name
     * @param parameterTypes parameter types or null if there are no parameters.
     * @return the matching method or null if no match is found.
     */
    public JavaMethod getMethodBySignature(String name,
                                           Type[] parameterTypes) {
        JavaMethod[] methods = getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].signatureMatches(name, parameterTypes)) {
                return methods[i];
            }
        }
        return null;
    }

    public JavaMethod getMethodBySignature(String name,
                                           Type[] parameterTypes,
                                           boolean superclasses) {
        JavaMethod methodInThisClass = getMethodBySignature(name,parameterTypes);
        if(methodInThisClass == null && superclasses) {
            // only look up if we found nothing and we're allowed to look up.
            // start with superclass, then interfaces.
            // That way we always look upwards in the order of declaration

            Type supertype = getSuperClass();
            if (supertype != null) {
                JavaClass superclass = supertype.getJavaClass();
                if (superclass != null) {
                    JavaMethod method = superclass.getMethodBySignature(name,parameterTypes,true);
                    // todo: ideally we should check on package privacy too. oh well.
                    if(method != null && !method.isPrivate()) {
                        return method;
                    }
                }
            }

            Type[] implementz = getImplements();
            for (int i = 0; i < implementz.length; i++) {
                JavaClass interfaze = implementz[i].getJavaClass();
                if(interfaze != null) {
                    JavaMethod method = interfaze.getMethodBySignature(name,parameterTypes,true);
                    if(method != null) {
                        return method;
                    }
                }
            }
            return null;
        } else {
            return methodInThisClass;
        }
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
        cls.setParent(this);
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
        Type type = new Type(fullClassName, 0, getParentSource());
        return asType().isA(type);
    }

    /**
     * @since 1.3
     */
    public boolean isA(JavaClass javaClass) {
        return asType().isA(javaClass.asType());
    }

    /**
     * @since 1.3
     */
    public BeanProperty[] getBeanProperties() {
        if (beanProperties == null) {
            initialiseBeanProperties();
        }
        return beanProperties;
    }

    /**
     * @since 1.3
     */
    public BeanProperty getProperty(String propertyName) {
        if (beanProperties == null) {
            initialiseBeanProperties();
        }
        return (BeanProperty) beanPropertyMap.get(propertyName);
    }

    private void initialiseBeanProperties() {
        beanPropertyMap = new HashMap();
        // loop over the methods.
        JavaMethod[] methods = getMethods();
        for (int i = 0; i < methods.length; i++) {
            JavaMethod method = methods[i];
            if (method.isPublic() && !method.isStatic()) {
                if (method.isPropertyAccessor()) {
                    String propertyName = method.getPropertyName();
                    BeanProperty beanProperty = getOrCreateProperty(propertyName);
                    beanProperty.setAccessor(method);
                    beanProperty.setType(method.getPropertyType());
                } else if (method.isPropertyMutator()) {
                    String propertyName = method.getPropertyName();
                    BeanProperty beanProperty = getOrCreateProperty(propertyName);
                    beanProperty.setMutator(method);
					beanProperty.setType(method.getPropertyType());
                }
            }
        }
        Collection beanPropertyCollection = beanPropertyMap.values();
        beanProperties = (BeanProperty[]) beanPropertyCollection.toArray(new BeanProperty[beanPropertyCollection.size()]);
    }

    private BeanProperty getOrCreateProperty(String propertyName) {
        BeanProperty result = (BeanProperty) beanPropertyMap.get(propertyName);
        if (result == null) {
            result = new BeanProperty(propertyName);
            beanPropertyMap.put(propertyName, result);
        }
        return result;
    }

    // This method will fail if the method isn't an accessor or mutator, but
    // it will only be called with methods that are, so we're safe.

    public JavaClass[] getDerivedClasses() {
        List result = new ArrayList();
        JavaDocBuilder builder = (JavaDocBuilder) javaClassCache;
        JavaClass[] classes = builder.getClasses();
        for (int i = 0; i < classes.length; i++) {
            JavaClass clazz = classes[i];
            if( clazz.isA(this) && !(clazz == this) ) {
                result.add(clazz);
            }
        }
        return (JavaClass[]) result.toArray(new JavaClass[result.size()]);
    }
}
