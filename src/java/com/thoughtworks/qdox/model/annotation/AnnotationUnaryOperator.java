package com.thoughtworks.qdox.model.annotation;

public abstract class AnnotationUnaryOperator implements AnnotationValue {

    private AnnotationValue value;

    public AnnotationUnaryOperator( AnnotationValue value ) {
        this.value = value;
    }

    public AnnotationValue getValue() {
        return value;
    }

}
