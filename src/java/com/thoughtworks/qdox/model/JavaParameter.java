package com.thoughtworks.qdox.model;

public class JavaParameter {

	private String name;
	private Type type;

	public JavaParameter(Type type, String name) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public boolean equals(Object obj) {
		JavaParameter p = (JavaParameter)obj;
		// name isn't used in equality check.
		return getType().equals(p.getType());
	}
}
