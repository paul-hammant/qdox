package com.thoughtworks.qdox.model;

public class JavaField extends AbstractJavaEntity {

	private Type type;

	public Type getType() {
		return type;
	}

	protected void writeBody(IndentBuffer result) {
		writeAllModifiers(result);
		result.write(type.getValue());
		for (int i = 0; i < type.getDimensions(); i++) {
			result.write("[]");
		}
		result.write(' ');
		result.write(name);
		result.write(';');
		result.newline();
	}

	public void setType(Type type) {
		this.type = type;
	}

}
