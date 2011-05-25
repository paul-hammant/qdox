package com.thoughtworks.qdox.parser.expression;


public interface ElemValueDef {

	<U> U transform(AnnotationTransformer<U> transformer);
}
