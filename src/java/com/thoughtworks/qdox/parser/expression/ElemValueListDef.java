package com.thoughtworks.qdox.parser.expression;

import java.util.List;

import com.thoughtworks.qdox.builder.AnnotationTransformer;

public class ElemValueListDef implements ElemValueDef{

	public List<ElemValueDef> valueList;
	
	public ElemValueListDef(List<ElemValueDef> annoValueList) {
		this.valueList = annoValueList;
	}

	public <U> U transform(AnnotationTransformer<U> transformer) {
		return transformer.transform(this);
	}

	public Object accept(AnnotationVisitor visitor) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getParameterValue() {
		// TODO Auto-generated method stub
		return null;
	}
}
