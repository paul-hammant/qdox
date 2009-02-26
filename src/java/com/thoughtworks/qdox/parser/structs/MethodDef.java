package com.thoughtworks.qdox.parser.structs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MethodDef extends LocatedDef {
    public String name = "";
    public List typeParams; //<TypeVariableDef>
    public TypeDef returnType;
    public Set modifiers = new HashSet();
    public List params = new ArrayList();
    public Set exceptions = new HashSet();
    public boolean constructor = false;
    public int dimensions;
    public String body;

    public boolean equals(Object obj) {
        MethodDef methodDef = (MethodDef) obj;
        boolean result;
        result = methodDef.name.equals(name)
                && methodDef.modifiers.equals(modifiers)
                && methodDef.params.equals(params)
                && methodDef.exceptions.equals(exceptions)
                && methodDef.constructor == constructor;
        if(methodDef.returnType == null) {
        	result &= (returnType == null)
        		&& methodDef.dimensions == dimensions;
        	
        }
        else {
        	result &= (returnType != null)        		
        			&&(methodDef.returnType.name.equals(returnType.name))
        			&&(methodDef.returnType.actualArgumentTypes == null ? returnType.actualArgumentTypes == null: methodDef.returnType.actualArgumentTypes.equals(returnType.actualArgumentTypes))
        			&&(methodDef.returnType.dimensions + methodDef.dimensions == dimensions + returnType.dimensions);
        }
        return result;
    }

    public int hashCode() {
        return name.hashCode() + 
        		(returnType != null ? returnType.hashCode() : 0) +
                modifiers.hashCode() + params.hashCode() +
                params.hashCode() + exceptions.hashCode() +
                dimensions + (constructor ? 0 : 1);
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(modifiers);
        result.append(' ');
        result.append((returnType != null ? returnType.toString() : ""));
        for (int i = 0; i < dimensions; i++) result.append("[]");
        result.append(' ');
        result.append(name);
        result.append('(');
        result.append(params);
        result.append(')');
        result.append(" throws ");
        result.append(exceptions);
        result.append(body);
        return result.toString();
    }
}
