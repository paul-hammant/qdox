package com.thoughtworks.qdox.model;

import java.util.Iterator;

public class JavaField extends AbstractJavaEntity {

	private Type type;
	private int dimensions;

	public Type getType() {
		return type;
	}

	protected void writeBody(IndentBuffer result) {
		writeAllModifiers(result);
		result.write(type.getValue());
		for (int i = 0; i < dimensions; i++) {
			result.write("[]");
		}
		result.write(' ');
		result.write(name);
		result.write(';');
		result.newline();
	}

	void setType(Type type) {
		this.type = type;
	}

	void setDimensions(int i) {
		this.dimensions = i;
	}

	public int getDimensions() {
		return dimensions;
	}

}
