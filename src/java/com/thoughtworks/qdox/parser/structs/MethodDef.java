package com.thoughtworks.qdox.parser.structs;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

public class MethodDef {
	public String name = "";
	public String returns = "";
	public Set modifiers = new HashSet();
	public List params = new ArrayList();
	public Set exceptions = new HashSet();
	public boolean constructor = false;
	public int dimensions;

	public boolean equals(Object obj) {
		MethodDef methodDef = (MethodDef)obj;
		return methodDef.name.equals(name)
			&& methodDef.returns.equals(returns)
			&& methodDef.modifiers.equals(modifiers)
			&& methodDef.params.equals(params)
			&& methodDef.exceptions.equals(exceptions)
			&& methodDef.constructor == constructor
			&& methodDef.dimensions == dimensions;
	}

	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append(modifiers);
		result.append(' ');
		result.append(returns);
		for(int i = 0; i < dimensions; i++) result.append("[]");
		result.append(' ');
		result.append(name);
		result.append('(');
		result.append(params);
		result.append(')');
		result.append(" throws ");
		result.append(exceptions);
		return result.toString();
	}
}
