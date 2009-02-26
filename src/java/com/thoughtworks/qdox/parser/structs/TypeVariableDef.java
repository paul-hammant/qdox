package com.thoughtworks.qdox.parser.structs;

import java.util.List;

public class TypeVariableDef {

	public String name;
	public List bounds;

	public TypeVariableDef(String name) {
		this.name = name;
	}

	public TypeVariableDef(String name, List bounds) {
		super();
		this.name = name;
		this.bounds = bounds;
	}
	
	
}