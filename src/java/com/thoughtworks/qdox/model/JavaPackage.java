package com.thoughtworks.qdox.model;


public class JavaPackage extends AbstractBaseJavaEntity {

	private String name;
	private Annotation[] annotations = new Annotation[0];
	private int lineNumber = -1;

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
}
