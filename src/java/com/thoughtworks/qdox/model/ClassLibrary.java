package com.thoughtworks.qdox.model;

import java.util.*;

public class ClassLibrary {

	private Set classes = new TreeSet();

	public void add(String fullClassName) {
		classes.add(fullClassName);
	}

	public boolean contains(String fullClassName) {
		return classes.contains(fullClassName);
	}

	public String findClass(Collection imports, String packageName, String className) {
		for (Iterator iterator = imports.iterator(); iterator.hasNext();) {
				String imprt = (String) iterator.next();
				if (imprt.endsWith("." + className)){
						return imprt;
				}
		}
		String nameAsIfInSamePackage = packageName + "." + className;
		if (classes.contains(nameAsIfInSamePackage)){
				return nameAsIfInSamePackage;
		}
		for (Iterator iterator = classes.iterator(); iterator.hasNext();) {
			String current = (String) iterator.next();
			if (current.endsWith("." + className) && imports.contains(current.substring(0, current.lastIndexOf(".")) + ".*")) {
				return current;
			}
		}
		return null;
	}

	public Collection all() {
		return Collections.unmodifiableCollection(classes);
	}

}
