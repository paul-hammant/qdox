package com.thoughtworks.qdox.model;

public class JavaMethod extends AbstractJavaEntity {

	protected Type returns;
	private JavaParameter[] parameters;
	private Type[] exceptions;
	private boolean constructor;
	private int dimensions;

	/**
	 * Constructor not public.
	 */
	JavaMethod() {
	}

	public Type getReturns() {
		return returns;
	}

	/**
	 * Return list of JavaParameters
	 */
	public JavaParameter[] getParameters() {
		return parameters;
	}

	public Type[] getExceptions() {
		return exceptions;
	}

	public boolean isConstructor() {
		return constructor;
	}

	protected void writeBody(IndentBuffer result) {
		writeAccessibilityModifier(result);
		writeNonAccessibilityModifiers(result);

		if (!constructor) {
			result.write(returns.getValue());
			for (int i = 0; i < dimensions; i++) {
				result.write("[]");
			}
			result.write(' ');
		}

		result.write(name);
		result.write('(');
		for (int i = 0; parameters != null && i < parameters.length; i++) {
			JavaParameter parameter = parameters[i];
			if (i > 0) result.write(", ");
			result.write(parameter.getType().getValue());
			for (int j = 0; j < parameter.getDimensions(); j++) {
				result.write("[]");
			}
			result.write(' ');
			result.write(parameter.getName());
		}
		result.write(')');
		if(exceptions != null && exceptions.length > 0) {
			result.write(" throws ");
			for (int i = 0; i < exceptions.length; i++) {
				if (i > 0) result.write(", ");
				result.write(exceptions[i].getValue());
			}
		}
		result.write(';');
		result.newline();
	}

	void setReturns(Type returns) {
		this.returns = returns;
	}

	void setParameters(JavaParameter[] parameters) {
		this.parameters = parameters;
	}

	void setExceptions(Type[] exceptions) {
		this.exceptions = exceptions;
	}

	void setConstructor(boolean constructor) {
		this.constructor = constructor;
	}

	void setDimensions(int dimensions) {
		this.dimensions = dimensions;
	}

	public int getDimensions() {
		return dimensions;
	}

}