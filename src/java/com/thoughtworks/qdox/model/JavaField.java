package com.thoughtworks.qdox.model;

public class JavaField extends AbstractJavaEntity implements Member {

    private Type type;

    public JavaField() {
    }

    public JavaField(String name) {
        setName(name);
    }

    public JavaField(Type type, String name) {
        setType(type);
        setName(name);
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

    public String getDeclarationSignature(boolean withModifiers) {
        IndentBuffer result = new IndentBuffer();
        if (withModifiers) {
            writeAllModifiers(result);
        }
        result.write(type.toString());
        result.write(' ');
        result.write(name);
        return result.toString();
    }

    public String getCallSignature() {
        return getName();
    }

}
