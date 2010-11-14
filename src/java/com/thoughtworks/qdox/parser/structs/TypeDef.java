package com.thoughtworks.qdox.parser.structs;

import java.util.List;

public class TypeDef {

    public String name;
    public int dimensions;
    public List<TypeDef> actualArgumentTypes; 

    public TypeDef(String name, int dimensions) {
        this.name = name;
        this.dimensions = dimensions;
    }

	public TypeDef(String name) {
		this(name, 0);
	}
	
	public boolean equals(Object obj) {
		TypeDef typeDef = (TypeDef) obj;
        return typeDef.name.equals(name)
                && typeDef.dimensions == dimensions
                && (typeDef.actualArgumentTypes != null ? typeDef.actualArgumentTypes.equals(actualArgumentTypes): actualArgumentTypes == null);
	}

	public int hashCode() {
        return name.hashCode() + 
                dimensions + (actualArgumentTypes == null ? 0 : actualArgumentTypes.hashCode());
    }
}
