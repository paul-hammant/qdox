package com.thoughtworks.qdox.model;

import java.util.List;

public class Type implements Comparable{
    private List imports;
    private String name;
    private ClassLibrary classLibrary;
    private String packge;
    private String value = null;
    private int dimensions;

    public Type(String fullName, int dimensions) {
        value = fullName;
        this.dimensions = dimensions;
    }

    public Type(List imports, String name, ClassLibrary classLibrary, String packge, int dimensions) {
        this.imports = imports;
        this.name = name;
        this.classLibrary = classLibrary;
        this.packge = packge;
        this.dimensions = dimensions;
    }

	private void resolve() {
        if (classLibrary == null || name.indexOf(".")!=-1){
            value = name;
            return;
        }

        value = classLibrary.findClass(imports, packge, name);
    }

    public String getValue() {
        return isResolved() ? value : name;
    }

    public boolean isResolved() {
        if (value==null)
            resolve();

        return value!=null;
    }


	/**
	 * @see java.lang.Comparable#compareTo(Object)
	 */
	public int compareTo(Object o) {
		if (!(o instanceof Type))
			return 0;

		return getValue().compareTo(((Type)o).getValue());
	}

	public boolean isArray() {
		return dimensions > 0;
	}

	public int getDimensions() {
		return dimensions;
	}

	public boolean equals(Object obj) {
		Type t = (Type)obj;
		return t.getValue().equals(getValue()) && t.getDimensions() == getDimensions();
	}

	public int hashCode() {
		return getValue().hashCode();
	}

}
