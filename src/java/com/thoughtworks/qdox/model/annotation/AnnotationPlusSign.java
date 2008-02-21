package com.thoughtworks.qdox.model.annotation;

public class AnnotationPlusSign extends AnnotationUnaryOperator {

    public AnnotationPlusSign( AnnotationValue value ) {
        super( value );
    }

    public String toString() {
        return "+" + getValue().toString();
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationPlusSign( this );
    }

    public Object getParameterValue() {
        return "+" + getValue().toString();
    }
}
