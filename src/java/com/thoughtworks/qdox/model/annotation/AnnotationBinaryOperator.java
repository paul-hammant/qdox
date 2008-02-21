package com.thoughtworks.qdox.model.annotation;

public abstract class AnnotationBinaryOperator implements AnnotationValue {

    private AnnotationValue left;

    private AnnotationValue right;

    public AnnotationBinaryOperator( AnnotationValue left, AnnotationValue right ) {
        this.left = left;
        this.right = right;
    }

    public AnnotationValue getLeft() {
        return left;
    }

    public AnnotationValue getRight() {
        return right;
    }

}
