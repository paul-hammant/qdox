package com.thoughtworks.qdox.parser.structs;

public class TypeDef {

    public String name;
    public int dimensions;
    public boolean isVarArgs;

    public TypeDef(String name, int dimensions) {
        this.name = name;
        this.dimensions = dimensions;
        //this.isVarArgs = isVarArgs;
    }

}
