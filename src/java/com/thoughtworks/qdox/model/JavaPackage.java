package com.thoughtworks.qdox.model;

import java.util.List;
import java.util.ArrayList;


public class JavaPackage extends AbstractBaseJavaEntity {

	private String name;
	private Annotation[] annotations = new Annotation[0];
	private int lineNumber = -1;
	private List classes = new ArrayList();

	public JavaPackage() {
	}

	public JavaPackage(String name) {
		this.name= name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Annotation[] getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Annotation[] annotations) {
		this.annotations = annotations;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public void addClass(JavaClass clazz) {
		classes.add(clazz);
	}

    /**
     * Returns all the classes found for the package.
     *
     * @return all the classes found for the package
     * @since 1.9
     */
	public JavaClass[] getClasses() {
		return (JavaClass[]) classes.toArray(new JavaClass[classes.size()]);
	}
}
