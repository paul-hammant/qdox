package com.thoughtworks.qdox;

import java.util.Hashtable;
import java.util.Map;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaClassCache;

public class DefaultJavaClassCache implements JavaClassCache {

	private Map classes = new Hashtable();
	
	public JavaClass getClassByName(String name) {
		return (JavaClass) classes.get(name);
	}

	public JavaClass[] getClasses() {
		return (JavaClass[]) classes.values().toArray(new JavaClass[0]);
	}

	public void putClassByName(String name, JavaClass javaClass) {
		classes.put(name, javaClass);
	}

}
