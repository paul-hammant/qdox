package com.thoughtworks.qdox.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.qdox.parser.structs.TypeDef;
import com.thoughtworks.qdox.parser.structs.TypeVariableDef;
/**
 * 
 * 
 * @author Robert Scholte
 * @since 1.10
 */
public class TypeVariable extends Type {

	private List<Type> bounds;

	public TypeVariable(String fullName, TypeVariableDef def, JavaClassParent context) {
		super(fullName, def.name, 0, context);
		if(def.bounds != null && !def.bounds.isEmpty()) {
			bounds = new LinkedList<Type>();
        	for(TypeDef typeDef : def.bounds) {
        		bounds.add(createUnresolved(typeDef, context));
        	}
        }
	}

	public static TypeVariable createUnresolved(TypeVariableDef def, JavaClassParent context) {
		return new TypeVariable(null, def, context);
	}
	
	
	public String getValue() {
		return (bounds == null || bounds.isEmpty() ? ""  : bounds.get(0).getValue());
	}
	
	public String getGenericValue() {
		StringBuffer result = new StringBuffer("<");
		result.append(super.getValue());
		if(bounds != null && !bounds.isEmpty()) {
			result.append(" extends ");
			for(Iterator<Type> iter = bounds.iterator(); iter.hasNext();) {
				result.append(iter.next().getGenericValue());
                if(iter.hasNext()) {
                    result.append(",");
                }
			}
		}
		result.append(">");
		return result.toString();
	}
	
	public String getName() {
		return super.getValue();
	}

}
