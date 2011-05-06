package com.thoughtworks.qdox.parser.expression;


public interface ElemValueDef {

	public <U> U transform(AnnotationTransformer<U> transformer);
}
