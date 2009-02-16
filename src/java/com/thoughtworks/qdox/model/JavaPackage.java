package com.thoughtworks.qdox.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;


public class JavaPackage extends AbstractBaseJavaEntity {

	private String name;
    private Map allPackages;
    private Annotation[] annotations = new Annotation[0];
	private int lineNumber = -1;
	private List classes = new ArrayList();

    public JavaPackage() {
	}

	public JavaPackage(String name, Map allPackages) {
		this.name= name;
        this.allPackages = allPackages;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Annotation[] getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Annotation[] annotations) {
		this.annotations = annotations;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public void addClass(JavaClass clazz) {
		classes.add(clazz);
	}

    /**
     * Returns all the classes found for the package.
     *
     * @return all the classes found for the package
     * @since 1.9
     */
	public JavaClass[] getClasses() {
		return (JavaClass[]) classes.toArray(new JavaClass[classes.size()]);
	}

    public JavaPackage getParentPackage() {
        String parentName = name.substring(0,name.lastIndexOf("."));
        return (JavaPackage) allPackages.get(parentName);
    }

    public JavaPackage[] getSubPackages() {
        String expected = name + ".";
        Set packageKeys = allPackages.keySet();
        List retList = new ArrayList();
        for (Iterator iterator = packageKeys.iterator(); iterator.hasNext();) {
            String pName = (String) iterator.next();
            if (pName.startsWith(expected) && !pName.substring(expected.length()).contains(".")) {
                retList.add(allPackages.get(pName));
            }
        }
        return (JavaPackage[]) retList.toArray(new JavaPackage[retList.size()]);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JavaPackage that = (JavaPackage) o;

        if (!name.equals(that.name)) return false;

        return true;
    }

    public int hashCode() {
        return name.hashCode();
    }
}
