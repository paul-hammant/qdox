package com.thoughtworks.qdox.model;

import java.util.Map;

import com.thoughtworks.qdox.parser.expression.AnnotationValue;

public interface JavaAnnotation {

	/**
	 * @return the annotation type
	 */
	public abstract Type getType();

	public abstract Map<String, AnnotationValue> getPropertyMap();

	public abstract AnnotationValue getProperty(String name);

}