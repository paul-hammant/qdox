package com.thoughtworks.qdox.model;

public class JavaField extends AbstractJavaEntity {

    private Type type;

    public JavaField(JavaClass parent) {
        super(parent);
    }

    public JavaField() {
        this(null);
    }

    public Type getType() {
        return type;
    }

    protected void writeBody(IndentBuffer result) {
        writeAllModifiers(result);
        result.write(type.toString());
        result.write(' ');
        result.write(name);
        result.write(';');
        result.newline();
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int compareTo(Object o) {
        return getName().compareTo(((JavaField)o).getName());
    }

}
