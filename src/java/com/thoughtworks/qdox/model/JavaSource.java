package com.thoughtworks.qdox.model;

import java.io.Serializable;

public class JavaSource implements Serializable {

	private JavaClass[] classes;
	private String packge;
	private String[] imports;

	public String getPackage() {
		return packge;
	}

	public String[] getImports() {
		return imports;
	}

	public JavaClass[] getClasses() {
		return classes;
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

	public void setPackge(String packge) {
		this.packge = packge;
	}

	public void setImports(String[] imports) {
		this.imports = imports;
	}

	public void setClasses(JavaClass[] classes) {
		this.classes = classes;
		
		for (int classIndex = 0; classIndex < classes.length; classIndex++) {
			JavaClass javaClass = classes[classIndex];
			javaClass.setSource(this);
		}
	}

}
