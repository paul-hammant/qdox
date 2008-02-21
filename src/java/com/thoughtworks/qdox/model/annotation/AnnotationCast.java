package com.thoughtworks.qdox.model.annotation;

import com.thoughtworks.qdox.model.Type;

public class AnnotationCast implements AnnotationValue {

    private final Type type;

    private final AnnotationValue value;

    public AnnotationCast( Type type, AnnotationValue value ) {
        this.type = type;
        this.value = value;
    }

    public Type getType() {
        return this.type;
    }

    public AnnotationValue getValue() {
        return this.value;
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationCast( this );
    }

    public Object getParameterValue() {
        return "(" + type.getValue() + ") " + value.getParameterValue();
    }

    public String toString() {
        return "(" + type.getValue() + ") " + value.toString();
    }

}
