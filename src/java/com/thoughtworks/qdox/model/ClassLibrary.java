package com.thoughtworks.qdox.model;

import java.util.*;

public class ClassLibrary {

	private Set classes = new TreeSet();
	private List classLoaders = new ArrayList();

	public void add(String fullClassName) {
		classes.add(fullClassName);
	}

	public boolean contains(String fullClassName) {
		if (classes.contains(fullClassName)) {
			return true;
		}
		for (Iterator iterator = classLoaders.iterator(); iterator.hasNext();) {
			ClassLoader classLoader = (ClassLoader)iterator.next();
			try {
				if (classLoader.loadClass(fullClassName) != null) {
					return true;
				}
			}
			catch (ClassNotFoundException e) {
				// continue
			}
		}
		return false;
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

	public void addClassLoader(ClassLoader classLoader) {
		classLoaders.add(classLoader);
	}

}
