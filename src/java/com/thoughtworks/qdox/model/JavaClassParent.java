package com.thoughtworks.qdox.model;

public interface JavaClassParent {

	/**
	 * Get the class "namespace" that this JavaClassParent represents.
	 */
	public abstract String asClassNamespace();

	/**
	 * Get the containing JavaSource.
	 */
	public abstract JavaSource getParentSource();

}
