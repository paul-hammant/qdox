package com.thoughtworks.qdox.parser.structs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassDef extends LocatedDef {
    
    public static final String CLASS = "class";
    public static final String INTERFACE = "interface";
    public static final String ENUM = "enum";
    public static final String ANNOTATION_TYPE = "@interface";
    
    public String name = "";
    public Set modifiers = new HashSet();
    public List typeParams = new ArrayList(); //<TypeVariableDef>
    public Set extendz = new HashSet();
    public Set implementz = new HashSet();
    public String type = CLASS;

    public boolean equals(Object obj) {
        ClassDef classDef = (ClassDef) obj;
        return classDef.name.equals(name)
                && classDef.type == type
                && classDef.typeParams.equals( typeParams )
                && classDef.modifiers.equals(modifiers)
                && classDef.extendz.equals(extendz)
                && classDef.implementz.equals(implementz);
    }

    public int hashCode() {
        return name.hashCode() + type.hashCode() + typeParams.hashCode()+
                modifiers.hashCode() + extendz.hashCode() +
                implementz.hashCode();
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(modifiers);
        result.append(" ");
        result.append(type);
        result.append(" ");
        result.append(name);
        //typeParams
        result.append(" extends ");
        result.append(extendz);
        result.append(" implements ");
        result.append(implementz);
        return result.toString();
    }
}
