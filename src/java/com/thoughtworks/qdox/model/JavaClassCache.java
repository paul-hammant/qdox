package com.thoughtworks.qdox.model;

import com.thoughtworks.qdox.model.JavaClass;

public interface JavaClassCache {
	public JavaClass getClassByName(String name);
}
