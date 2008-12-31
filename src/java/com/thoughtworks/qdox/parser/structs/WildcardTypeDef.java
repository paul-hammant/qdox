package com.thoughtworks.qdox.parser.structs;

/**
 * WildcardTypeDef must be a subclass of TypeDef, so it can be used in other classes.
 * But here are only 2 fields interesting: typeDef and wildcardExpressionType
 * typeDef itself can be generic, so it must be resolvable
 * wildcardExpressionType is super or extends
 * 
 * 
 * @author Robert Scholte
 *
 */
public class WildcardTypeDef extends TypeDef {

	private TypeDef typeDef;
	private String wildcardExpressionType; //super or extends
	
	public WildcardTypeDef() {
		super("?");
	}
	
	public WildcardTypeDef(TypeDef typeDef, String wildcardExpressionType) {
		super(typeDef.name, typeDef.dimensions);
		this.typeDef = typeDef;
		this.wildcardExpressionType = wildcardExpressionType;
	}
	
	public TypeDef getTypeDef() {
		return typeDef;
	}
	
	public String getWildcardExpressionType() {
		return wildcardExpressionType;
	}
}
