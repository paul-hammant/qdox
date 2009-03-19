package com.thoughtworks.qdox.model;

import com.thoughtworks.qdox.parser.structs.TypeDef;
import com.thoughtworks.qdox.parser.structs.TypeVariableDef;
/**
 * 
 * 
 * @author Robert Scholte
 * @since 1.10
 */
public class TypeVariable extends Type {

	public static final TypeVariable[] EMPTY_ARRAY = new TypeVariable[0];
	
	private Type[] bounds;

	public TypeVariable(String fullName, TypeVariableDef def, JavaClassParent context) {
		super(fullName, def.name, 0, context);
		if(def.bounds != null && !def.bounds.isEmpty()) {
			bounds = new Type[def.bounds.size()];
        	for(int index = 0; index < def.bounds.size(); index++) {
        		bounds[index] = createUnresolved((TypeDef) def.bounds.get(index), context);
        	}
        }
	}

	public static TypeVariable createUnresolved(TypeVariableDef def, JavaClassParent context) {
		return new TypeVariable(null, def, context);
	}
	
	
	public String getValue() {
		return bounds[0].getValue();
	}
	
	public String getGenericValue() {
		StringBuffer result = new StringBuffer("<");
		result.append(super.getValue());
		if(bounds != null && bounds.length > 0) {
			result.append(" extends ");
			for(int index = 0; index < bounds.length; index++) {
				if(index > 0) {
					result.append(",");
				}
				result.append(bounds[index].getGenericValue());
			}
		}
		result.append(">");
		return result.toString();
	}
	
	public String getName() {
		return super.getValue();
	}

}
