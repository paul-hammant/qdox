package com.thoughtworks.qdox.model;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JavaSource implements Serializable, JavaClassParent {

    private static final Set PRIMITIVE_TYPES = new HashSet();

    static {
        PRIMITIVE_TYPES.add("boolean");
        PRIMITIVE_TYPES.add("byte");
        PRIMITIVE_TYPES.add("char");
        PRIMITIVE_TYPES.add("double");
        PRIMITIVE_TYPES.add("float");
        PRIMITIVE_TYPES.add("int");
        PRIMITIVE_TYPES.add("long");
        PRIMITIVE_TYPES.add("short");
        PRIMITIVE_TYPES.add("void");
    }

    private File file;
    private String packge;
    private List imports = new LinkedList();
    private String[] importsArray;
    private List classes = new LinkedList();
    private JavaClass[] classesArray;
    private ClassLibrary classLibrary;
    private Map resolvedTypeCache = new HashMap();

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public String getPackage() {
        return packge;
    }

    public void setPackage(String packge) {
        this.packge = packge;
    }

    public void addImport(String imp) {
        imports.add(imp);
        importsArray = null;
    }

    public String[] getImports() {
        if (importsArray == null) {
            importsArray = new String[imports.size()];
            imports.toArray(importsArray);
        }
        return importsArray;
    }

    public void addClass(JavaClass imp) {
        classes.add(imp);
        classesArray = null;
    }

    public JavaClass[] getClasses() {
        if (classesArray == null) {
            classesArray = new JavaClass[classes.size()];
            classes.toArray(classesArray);
        }
        return classesArray;
    }

    public ClassLibrary getClassLibrary() {
        return classLibrary;
    }

    public void setClassLibrary(ClassLibrary classLibrary) {
        this.classLibrary = classLibrary;
    }

    public String toString() {
        IndentBuffer result = new IndentBuffer();

        // package statement
        if (packge != null) {
            result.write("package ");
            result.write(packge);
            result.write(';');
            result.newline();
            result.newline();
        }

        // import statement
        String[] imports = getImports();
        for (int i = 0; imports != null && i < imports.length; i++) {
            result.write("import ");
            result.write(imports[i]);
            result.write(';');
            result.newline();
        }
        if (imports != null && imports.length > 0) {
            result.newline();
        }

        // classes
        JavaClass[] classes = getClasses();
        for (int i = 0; i < classes.length; i++) {
            if (i > 0) result.newline();
            classes[i].write(result);
        }

        return result.toString();
    }

    public String resolveType(String typeName) {
        if (resolvedTypeCache.containsKey(typeName)) {
            return (String) resolvedTypeCache.get(typeName);
        }
        String resolved = resolveTypeInternal(typeName);
        if (resolved != null) {
            resolvedTypeCache.put(typeName, resolved);
        }
        return resolved;
    }

    private String resolveTypeInternal(String typeName) {

        // primitive types
        if (PRIMITIVE_TYPES.contains(typeName)) return typeName;

        // check if a matching fully-qualified import
        String[] imports = getImports();
        for (int i = 0; i < imports.length; i++) {
            if (imports[i].equals(typeName)) return typeName;
            if (imports[i].endsWith("." + typeName)) return imports[i];
        }

        if (getClassLibrary() == null) return null;

        // check for fully-qualified class
        if (getClassLibrary().contains(typeName)) {
            return typeName;
        }
        
        // check for a class in the same package
        {
            String fqn = getClassNamePrefix() + typeName;
            if (getClassLibrary().contains(fqn)) {
                return fqn;
            }
        }

        // check for wildcard imports
        for (int i = 0; i < imports.length; i++) {
            if (imports[i].endsWith(".*")) {
                String fqn = 
                    imports[i].substring(0, imports[i].length() - 1)
                    + typeName;
                if (getClassLibrary().contains(fqn)) {
                    return fqn;
                }
            }
        }

        // try java.lang.*
        {
            String fqn = "java.lang." + typeName;
            if (getClassLibrary().contains(fqn)) {
                return fqn;
            }
        }

        // maybe it's an inner-class reference
        int indexOfLastDot = typeName.lastIndexOf('.');
        if (indexOfLastDot != -1) {
            String root = typeName.substring(0,indexOfLastDot);
            String leaf = typeName.substring(indexOfLastDot+1);
            return resolveType(root + "$" + leaf);
        }
        
        return null;
    }

    public String getClassNamePrefix() {
        if (getPackage() == null) return "";
        return getPackage() + ".";
    }

    public JavaSource getParentSource() {
        return this;
    }

}
