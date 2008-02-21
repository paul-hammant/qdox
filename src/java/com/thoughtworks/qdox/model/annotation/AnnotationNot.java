package com.thoughtworks.qdox.model.annotation;

public class AnnotationNot extends AnnotationUnaryOperator {

    public AnnotationNot( AnnotationValue value ) {
        super( value );
    }

    public String toString() {
        return "~" + getValue().toString();
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationNot( this );
    }

    public Object getParameterValue() {
        return "~" + getValue().toString();
    }
}
