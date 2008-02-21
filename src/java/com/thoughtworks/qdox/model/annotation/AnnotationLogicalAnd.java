package com.thoughtworks.qdox.model.annotation;

public class AnnotationLogicalAnd extends AnnotationBinaryOperator {

    public AnnotationLogicalAnd( AnnotationValue left, AnnotationValue right ) {
        super( left, right );
    }

    public String toString() {
        return getLeft().toString() + " && " + getRight().toString();
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationLogicalAnd( this );
    }

    public Object getParameterValue() {
        return getLeft().getParameterValue() + " && " + getRight().getParameterValue();
    }

}
