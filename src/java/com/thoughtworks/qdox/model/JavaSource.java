package com.thoughtworks.qdox.model;

import java.io.Serializable;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JavaSource implements Serializable, JavaClassParent {

	private static final Set PRIMITIVE_TYPES = new HashSet();
	static {
		PRIMITIVE_TYPES.add( "boolean" );
		PRIMITIVE_TYPES.add( "byte" );
		PRIMITIVE_TYPES.add( "char" );
		PRIMITIVE_TYPES.add( "double" );
		PRIMITIVE_TYPES.add( "float" );
		PRIMITIVE_TYPES.add( "int" );
		PRIMITIVE_TYPES.add( "long" );
		PRIMITIVE_TYPES.add( "short" );
		PRIMITIVE_TYPES.add( "void" );
	}

	private JavaClass[] classes = new JavaClass[0];
	private String packge;
	private String[] imports = new String[0];
	private ClassLibrary classLibrary;
	private Map typeCache = new HashMap();
	private File file;

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

	public String[] getImports() {
		return imports;
	}

	public void setImports(String[] imports) {
		this.imports = imports;
	}

	public JavaClass[] getClasses() {
		return classes;
	}

	public void setClasses(JavaClass[] classes) {
		this.classes = classes;
		for (int classIndex = 0; classIndex < classes.length; classIndex++) {
			JavaClass javaClass = classes[classIndex];
			javaClass.setParent(this);
		}
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
		for (int i = 0; i < classes.length; i++) {
			if (i > 0) result.newline();
			classes[i].write(result);
		}

		return result.toString();
	}

	/**
	 * Resolve a type-name within the context of this source-file.
	 * @param typeName name of a type
	 * @return the fully-qualified name of the type, or null if it cannot
	 *	 be resolved
	 */
	public String resolveType(String typeName) {
		String resolved = (String)typeCache.get(typeName);
		if (resolved != null) {
			return resolved;
		}
		resolved = resolveTypeInternal(typeName);
		if (resolved != null) {
			typeCache.put(typeName,resolved);
		}
		return resolved;
	}

	private String resolveTypeInternal(String typeName) {
		if (typeName.indexOf('.') != -1) return typeName;

		// primitive types
		if (PRIMITIVE_TYPES.contains(typeName)) return typeName;

		// check if a matching fully-qualified import
		for (int i = 0; i < imports.length; i++) {
			if (imports[i].endsWith("." + typeName)){
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
					imports[i].substring(0, imports[i].length()-1) + typeName;
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
