package com.thoughtworks.qdox.model;

import java.util.List;
import java.util.Iterator;

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
        if (name.indexOf(".")!=-1){
            value = name;
            return;
        }

			value = classLibrary.findClass(imports, packge, name);
//        for (Iterator iterator = this.imports.iterator(); iterator.hasNext();) {
//            String imprt = (String) iterator.next();
//            if (imprt.endsWith("." + this.name)){
//                value = imprt;
//                return;
//            }
//        }

//        String nameAsIfInSamePackage = packge + "." + name;
//        if (classLibrary.contains(nameAsIfInSamePackage)){
//            value = nameAsIfInSamePackage;
//            return;
//        }

//        for (Iterator iterator = allClassNames.iterator(); iterator.hasNext();) {
//            String className = (String) iterator.next();
//            if (className.endsWith("." + name) && imports.contains(className.substring(0, className.lastIndexOf(".")) + ".*")){
//                value = className;
//                return;
//            }
//        }
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
