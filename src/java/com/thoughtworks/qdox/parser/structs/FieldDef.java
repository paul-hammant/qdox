package com.thoughtworks.qdox.parser.structs;

import java.util.HashSet;
import java.util.Set;

public class FieldDef {
    public String name = "";
    public String type = "";
    public Set modifiers = new HashSet();
    public int dimensions;

    public boolean equals(Object obj) {
        FieldDef paramDef = (FieldDef) obj;
        return paramDef.name.equals(name)
                && paramDef.type.equals(type)
                && paramDef.dimensions == dimensions
                && paramDef.modifiers.equals(modifiers);
    }

    public int hashCode() {
        return name.hashCode() + type.hashCode() +
                dimensions + modifiers.hashCode();
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(modifiers);
        result.append(' ');
        result.append(type);
        for (int i = 0; i < dimensions; i++) result.append("[]");
        result.append(' ');
        result.append(name);
        return result.toString();
    }
}
