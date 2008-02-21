package com.thoughtworks.qdox.model.annotation;

public class AnnotationSubtract extends AnnotationBinaryOperator {

    public AnnotationSubtract( AnnotationValue left, AnnotationValue right ) {
        super( left, right );
    }

    public String toString() {
        return getLeft().toString() + " - " + getRight().toString();
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationSubtract( this );
    }

    public Object getParameterValue() {
        return getLeft().getParameterValue() + " - " + getRight().getParameterValue();
    }

}
