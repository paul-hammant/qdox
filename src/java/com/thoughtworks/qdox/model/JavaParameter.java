package com.thoughtworks.qdox.model;

public class JavaParameter {

	private String name;
	private Type type;
	private int dimensions;

	/**
	 * Constructor not public.
	 */
	JavaParameter(Type type, String name, int dimensions) {
		this.name = name;
		this.type = type;
		this.dimensions = dimensions;
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public int getDimensions() {
		return dimensions;
	}

}
