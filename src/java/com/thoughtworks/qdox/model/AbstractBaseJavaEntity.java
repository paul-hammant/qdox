package com.thoughtworks.qdox.model;

import java.io.Serializable;

public class AbstractBaseJavaEntity implements Serializable {

	protected String name;
	private Annotation[] annotations = new Annotation[0];
	private int lineNumber = -1;
	protected JavaClassParent parent;

	public AbstractBaseJavaEntity() {
		super();
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getName() {
	    return name;
	}

	public Annotation[] getAnnotations() {
	    return annotations;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public void setAnnotations(Annotation[] annotations) {
	    this.annotations = annotations;
	}

	public void setLineNumber(int lineNumber) {
	    this.lineNumber = lineNumber;
	}

	public JavaClassParent getParent() { 
	    return parent; 
	}

	public void setParent(JavaClassParent parent) { 
	    this.parent = parent;
	}

}