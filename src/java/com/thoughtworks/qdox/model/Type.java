package com.thoughtworks.qdox.model;

import java.util.List;

public class Type implements Comparable{
    private List imports;
    private String name;
    private ClassLibrary classLibrary;
    private String packge;
    private String value = null;

    public Type(List imports, String name, ClassLibrary classLibrary, String packge) {
        this.imports = imports;
        this.name = name;
        this.classLibrary = classLibrary;
        this.packge = packge;
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

}
