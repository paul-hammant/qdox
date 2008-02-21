package com.thoughtworks.qdox.model.annotation;

public class AnnotationAnd extends AnnotationBinaryOperator {

    public AnnotationAnd( AnnotationValue left, AnnotationValue right ) {
        super( left, right );
    }

    public String toString() {
        return getLeft().toString() + " & " + getRight().toString();
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationAnd( this );
    }

    public Object getParameterValue() {
        return getLeft().getParameterValue() + " & " + getRight().getParameterValue();
    }

}
