package com.thoughtworks.qdox.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

public class JavaClass extends AbstractJavaEntity {

	private List methods = new LinkedList();
	private List fields = new LinkedList();
	private boolean interfce;
	private Type superClass;
	private Type[] implementz = new Type[0];
	private JavaSource source;
	
	/**
	 * Interface or class?
	 */
	public boolean isInterface() {
		return interfce;
	}

	public Type getSuperClass() {
		return superClass;
	}

	public Type[] getImplements() {
		return implementz;
	}

	public int getMethodCount() {
		return methods.size();
	}

	public JavaMethod getMethod(int i) {
		return (JavaMethod) methods.get(i);
	}

	public JavaField getField(int i) {
		return (JavaField) fields.get(i);
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
			JavaField javaField = (JavaField)iterator.next();
			result.newline();
			javaField.write(result);
		}

		// methods
		for (Iterator iterator = methods.iterator(); iterator.hasNext();) {
			JavaMethod javaMethod = (JavaMethod)iterator.next();
			result.newline();
			javaMethod.write(result);
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
		this.methods.add(meth);
	}

	public void setSuperClass(Type type) {
		superClass = type;
	}

	public void setImplementz(Type[] implementz) {
		this.implementz = implementz;
	}

	public void addField(JavaField javaField) {
		fields.add(javaField);
	}

	public boolean isContainingClass() {
		return isPublic();
	}

	public String getPackage() {
		return source.getPackage();
	}

	public String getFullyQualifiedName() {
		return source.getPackage() + "." + getName();
	}

	public void setSource(JavaSource javaSource) {
		source = javaSource;
	}

}
