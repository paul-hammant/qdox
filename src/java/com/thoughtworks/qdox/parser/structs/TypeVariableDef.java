package com.thoughtworks.qdox.parser.structs;

import java.util.List;

public class TypeVariableDef {

	public String name;
	public List<TypeDef> bounds;

	public TypeVariableDef(String name) {
		this.name = name;
	}

	public TypeVariableDef(String name, List<TypeDef> bounds) {
		super();
		this.name = name;
		this.bounds = bounds;
	}
	
	
}