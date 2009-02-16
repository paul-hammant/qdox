package com.thoughtworks.qdox.parser.structs;

public class PackageDef extends LocatedDef {

	public String name = "";
	
	public PackageDef(String name) {
		this.name = name;
	}
	
	public PackageDef(String name, int lineNumber) {
		this.name = name;
		this.lineNumber = lineNumber;
	}
	
	public boolean equals(Object obj) {
		PackageDef packageDef = (PackageDef) obj;
		return packageDef.name.equals(name);
	}
}
