package com.thoughtworks.qdox.parser.structs;

import java.util.HashSet;
import java.util.Set;

public class ClassDef extends LocatedDef {
    public String name = "";
    public Set modifiers = new HashSet();
    public Set extendz = new HashSet();
    public Set implementz = new HashSet();
    public boolean isInterface;

    public boolean equals(Object obj) {
        ClassDef classDef = (ClassDef) obj;
        return classDef.name.equals(name)
                && classDef.isInterface == isInterface
                && classDef.modifiers.equals(modifiers)
                && classDef.extendz.equals(extendz)
                && classDef.implementz.equals(implementz);
    }

    public int hashCode() {
        return name.hashCode() + (isInterface ? 1 : 0) +
                modifiers.hashCode() + extendz.hashCode() +
                implementz.hashCode();
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(modifiers);
        result.append(isInterface ? " interface " : " class ");
        result.append(name);
        result.append(" extends ");
        result.append(extendz);
        result.append(" implements ");
        result.append(implementz);
        return result.toString();
    }
}
