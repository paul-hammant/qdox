package com.thoughtworks.qdox.model;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.qdox.JavaClassContext;

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

    private com.thoughtworks.qdox.library.ClassLibrary classLibrary;
    private com.thoughtworks.qdox.model.ClassLibrary oldClassLibrary;
    
    private JavaPackage packge;
    private List imports = new LinkedList();
    private String[] importsArray;
    private List classes = new LinkedList();
    private JavaClass[] classesArray;
    private JavaClassContext context;
    private Map resolvedTypeCache = new HashMap();
    private URL url;

    //@todo remove
    public JavaSource() {
    	this(new JavaClassContext());
    }
    
    //@todo remove
    public JavaSource(JavaClassContext context) {
    	this.context = context;
    }
    
    //@todo remove
    public JavaSource(com.thoughtworks.qdox.model.ClassLibrary classLibrary) {
        this.oldClassLibrary = classLibrary;
        this.context = classLibrary.getContext();
    }

    public JavaSource(com.thoughtworks.qdox.library.ClassLibrary classLibrary) {
        this.classLibrary = classLibrary;
    }

    /**
     * @since 1.4
     */
    public void setURL(URL url) {
        this.url = url;
    }

    /**
     * @since 1.4
     */
    public URL getURL() {
        return url;
    }

    /**
     * @deprecated use setURL
     */
    public void setFile(File file) {
        try {
            setURL(file.toURL());
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * @deprecated use getURL
     */
    public File getFile() {
        return new File(url.getFile());
    }

    public JavaPackage getPackage() {
        return packge;
    }

    public void setPackage(JavaPackage packge) {
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

    public void addClass(JavaClass cls) {
        classes.add(cls);
        classesArray = null;
    }

    public JavaClass[] getClasses() {
        if (classesArray == null) {
            classesArray = new JavaClass[classes.size()];
            classes.toArray(classesArray);
        }
        return classesArray;
    }

    public JavaClassContext getJavaClassContext() {
        return this.context;
    }

    public void setClassLibrary(ClassLibrary classLibrary) {
        this.oldClassLibrary = classLibrary;
        this.context.setClassLibrary(classLibrary); //should be removed
    }

    public String getCodeBlock() {
        IndentBuffer result = new IndentBuffer();

        // package statement
        if (packge != null) {
            result.write("package ");
            result.write(packge.getName());
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
    
    public String toString() {
    	return getCodeBlock();
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
    
    /**
     * Resolves a type name
     * <p>
     * Follows the <a href="http://java.sun.com/docs/books/jls/third_edition/html/packages.html#7.5.1">
     * Java Language Specification, Version 3.0</a>.
     * <p>
     * Current resolution order is:
     * <ol>
     * <li>Single-Type-Import Declaration</li>
     * <li>Type-Import-on-Demand Declaration</li>
     * <li>Automatic Imports</li>
     * </ol>
     * @todo Static imports are not handled yet
     * 
     * @param typeName
     * @return Resolved type name
     */
    private String resolveTypeInternal(String typeName) {
        String resolvedName = null;

        lookup : {
            // primitive types
            if(PRIMITIVE_TYPES.contains( typeName )) {
                resolvedName = typeName;
                break lookup;
            }

            String outerName = typeName;
            String nestedName = typeName.replace('.', '$');
            int dotpos = typeName.indexOf( '.' );

            if(dotpos >= 0) {
                outerName = typeName.substring( 0, dotpos );
            }
            
            // Check single-type-import with fully qualified name
            resolvedName = resolveImportedType( typeName, nestedName, true );
                    
            if(resolvedName != null) {
                break lookup;
            }
            
            // Check single-type-import with outer name
            resolvedName = resolveImportedType( outerName, nestedName, false );
            
            if(resolvedName != null) {
                break lookup;
            }

            // check for a class globally
            resolvedName = resolveFullyQualifiedType( typeName );
            
            if(resolvedName != null) {
                break lookup;
            }

            if(classLibrary != null || oldClassLibrary != null) {
                // check for a class in the same package
                resolvedName = resolveFromLibrary( getClassNamePrefix() + nestedName );
                
                if(resolvedName != null) {
                    break lookup;
                }
                
                // try java.lang.*
                resolvedName = resolveFromLibrary( "java.lang." + nestedName );

                if(resolvedName != null) {
                    break lookup;
                }
            }
            
            // Check type-import-on-demand
            resolvedName = resolveImportedType( "*", nestedName, false );

            if(resolvedName != null) {
                break lookup;
            }
        }
        
        return resolvedName;
    }
    
    private String resolveImportedType( String importSpec, String typeName, boolean fullMatch ) {
        String[] imports = getImports();
        String resolvedName = null;
        String dotSuffix = "." + importSpec;
            
        for (int i = 0; i < imports.length && resolvedName == null; i++) {
            if (imports[i].equals(importSpec) || (!fullMatch && imports[i].endsWith(dotSuffix))) {
                String candidateName = imports[i].substring( 0, imports[i].length() - importSpec.length()) + typeName;
                resolvedName = resolveFullyQualifiedType( candidateName );
                if(resolvedName == null && !"*".equals(importSpec)) {
                	resolvedName = candidateName;
                }
            } 
        }
        
        return resolvedName;
    }
    
    private String resolveFromLibrary(String typeName) {
        String result;
        if(classLibrary != null) {
            result = classLibrary.hasClassReference( typeName ) ? typeName : null;
        }
        else {
            result = oldClassLibrary.contains( typeName ) ? typeName : null;
        }
        return result;
    }
    
    private String resolveFullyQualifiedType(String typeName) {
        if (classLibrary != null || oldClassLibrary != null) {
            int indexOfLastDot = typeName.lastIndexOf('.');
            
            if (indexOfLastDot >= 0) {
                String root = typeName.substring(0,indexOfLastDot);
                String leaf = typeName.substring(indexOfLastDot+1);
                String resolvedTypeName = resolveFullyQualifiedType(root + "$" + leaf);
                
                if(resolvedTypeName != null) {
                    return resolvedTypeName;
                }
            }
    
            // check for fully-qualified class
            if ( classLibrary != null) {
                if( classLibrary.hasClassReference( typeName )) {
                    return typeName;
                }
            }
            else if (oldClassLibrary.contains(typeName)) {
                return typeName;
            }
        }

        return null;
    }

    public String getClassNamePrefix() {
        if (getPackage() == null) return "";
        return getPackage().getName() + ".";
    }

    public JavaSource getParentSource() {
        return this;
    }
    
    public JavaClass getNestedClassByName(String name) {
        JavaClass result = null;
        
        for (ListIterator i = classes.listIterator(); i.hasNext(); ) {
            JavaClass candidateClass = (JavaClass) i.next();
            
            if (candidateClass.getName().equals(name)) {
                result = candidateClass;
                break;
            }
        }

        return result;
    }

    /**
     * 
     * @return
     * @deprecated , use getJavaClassContext().getClassLibrary()
     */
	public ClassLibrary getClassLibrary() {
		return oldClassLibrary;
	}
	
	public com.thoughtworks.qdox.library.ClassLibrary getJavaClassLibrary()
	{
	    return classLibrary;
	}

    public String getPackageName()
    {
        return (packge == null ? "" : packge.getName());
    }

}
