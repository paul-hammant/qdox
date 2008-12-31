package com.thoughtworks.qdox.model;

import com.thoughtworks.qdox.parser.structs.WildcardTypeDef;

/**
 * This class supports both the 'super' and 'extends' wildcards.
 * For &lt;?&gt; you must use the normal Type, because ? itself can't be generic 
 * 
 * @author Robert Scholte
 *
 */
public class WildcardType extends Type {

	private String wildcardExpressionType = null;
	
	public WildcardType() {
		super("?");
	}
	
	
	public WildcardType(WildcardTypeDef typeDef) {
		this(typeDef, null);
	}

	public WildcardType(WildcardTypeDef typeDef, JavaClassParent context) {
		super(null, typeDef, 0, context);
		this.wildcardExpressionType = typeDef.getWildcardExpressionType();
	}

	public String getGenericValue() {
		String result = "";
		if(wildcardExpressionType != null) {
			result += "? " + wildcardExpressionType+ " ";
		}
		result += super.getGenericValue();
		return result;
	}
}
