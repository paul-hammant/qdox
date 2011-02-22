package com.thoughtworks.qdox.parser.expression;

import com.thoughtworks.qdox.builder.AnnotationTransformer;

public interface ElemValueDef {

	public <U> U transform(AnnotationTransformer<U> transformer);
}
