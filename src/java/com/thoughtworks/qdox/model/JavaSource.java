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
    private Map typeCache = new HashMap();

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
        String resolved = (String) typeCache.get(typeName);
        if (resolved != null) {
            return resolved;
        }
        resolved = resolveTypeInternal(typeName);
        if (resolved != null) {
            typeCache.put(typeName, resolved);
        }
        return resolved;
    }

    private String resolveTypeInternal(String typeName) {
        if (typeName.indexOf('.') != -1) return typeName;

        // primitive types
        if (PRIMITIVE_TYPES.contains(typeName)) return typeName;

        // check if a matching fully-qualified import
        String[] imports = getImports();
        for (int i = 0; i < imports.length; i++) {
            if (imports[i].endsWith("." + typeName)) {
                return imports[i];
            }
        }

        if (getClassLibrary() == null) return null;

        // check for a class in the same package
        String potentialName = packge + "." + typeName;
        if (getClassLibrary().contains(potentialName)) {
            return potentialName;
        }

        // check for wildcard imports
        for (int i = 0; i < imports.length; i++) {
            if (imports[i].endsWith(".*")) {
                potentialName =
                        imports[i].substring(0, imports[i].length() - 1) + typeName;
                if (getClassLibrary().contains(potentialName)) {
                    return potentialName;
                }
            }
        }

        // try java.lang.*
        potentialName = "java.lang." + typeName;
        if (getClassLibrary().contains(potentialName)) {
            return potentialName;
        }

        return null;
    }

    public String asClassNamespace() {
        return getPackage();
    }

    public JavaSource getParentSource() {
        return this;
    }

}
