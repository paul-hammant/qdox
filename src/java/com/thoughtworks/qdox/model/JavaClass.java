package com.thoughtworks.qdox.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class JavaClass extends AbstractJavaEntity {

	private List methods = new LinkedList();
	private JavaMethod[] methodsArray;
	private List fields = new LinkedList();
	private JavaField[] fieldsArray;
	private boolean interfce;
	private Type type;
	private Type superClass;
	private Type[] implementz = new Type[0];
	private JavaSource parentSource;

	public static final Type OBJECT = new Type("java.lang.Object", 0);
	private JavaClassCache javaClassCache;

	public void setJavaClassCache(JavaClassCache javaClassCache) {
	   this.javaClassCache = javaClassCache;
	}

	/**
	 * Interface or class?
	 */
	public boolean isInterface() {
		return interfce;
	}

	public Type getSuperClass() {
		if (!interfce && superClass == null) {
			return OBJECT;
		}
		return superClass;
	}

	public JavaClass getSuperJavaClass() {
		if (javaClassCache == null) {
			throw new java.lang.UnsupportedOperationException("JavaClassCache unavailable for this JavaClass");
		}
		return javaClassCache.getClassByName(getSuperClass().getValue());
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

	public String getPackage() {
		return parentSource.getPackage();
	}

	public String getFullyQualifiedName() {
		return parentSource.getPackage() + "." + getName();
	}

	public void setParentSource(JavaSource javaSource) {
		parentSource = javaSource;
	}

	public Type asType() {
		if (type == null) {
			type = new Type(parentSource.getPackage() + "." + getName(), 0);
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

	public JavaField[] getFields() {
		if (fieldsArray == null) {
			fieldsArray = new JavaField[fields.size()];
			fields.toArray(fieldsArray);
		}
		return fieldsArray;
	}

	public JavaSource getParentSource() {
		return parentSource;
	}

}
