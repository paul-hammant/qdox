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
		// check if there's in an import ending with the class
		// (classname = ClassName and imports contains com.blah.ClassName)
		for (Iterator iterator = imports.iterator(); iterator.hasNext();) {
				String imprt = (String) iterator.next();
				if (imprt.endsWith("." + className)){
						return imprt;
				}
		}
		// check if a class exists in the same package with given name
		String nameAsIfInSamePackage = packageName + "." + className;
		if (contains(nameAsIfInSamePackage)){
				return nameAsIfInSamePackage;
		}
		// check java.lang.*
		if (contains("java.lang." + className)) {
			return "java.lang." + className;
		}
		// loop through import statements checking if class exists in .* import
		for (Iterator iterator = imports.iterator(); iterator.hasNext();) {
			String imprt = (String)iterator.next();
			if (imprt.endsWith(".*")) {
				imprt = imprt.substring(0, imprt.length() - 2);
				if (contains(imprt + '.' + className)) {
					return imprt + '.' + className;
				}
			}
		}
		// bummer...
		return null;
	}

	public Collection all() {
		return Collections.unmodifiableCollection(classes);
	}

	public void addClassLoader(ClassLoader classLoader) {
		classLoaders.add(classLoader);
	}

}
