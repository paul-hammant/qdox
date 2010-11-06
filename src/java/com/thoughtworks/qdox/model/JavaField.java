package com.thoughtworks.qdox.model;

public class JavaField extends AbstractJavaEntity implements Member, JavaAnnotatedElement, JavaMember {

    private Type type;
    private String initializationExpression;
    	
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
    
    public String getCodeBlock()
    {
        return getSource().getModelWriter().writeField( this ).toString();
    }

    protected void writeBody(IndentBuffer result) {
        writeAllModifiers(result);
        result.write(type.toString());
        result.write(' ');
        result.write(name);
        if(initializationExpression != null && initializationExpression.length() > 0){
          result.write(" = ");
          result.write(initializationExpression);
        }
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

    /**
     * Get the original expression used to initialize the field.
     *
     * @return initialization as string.
     */
    public String getInitializationExpression(){
    	return initializationExpression;
    }
    
    public void setInitializationExpression(String initializationExpression){
    	this.initializationExpression = initializationExpression;
    }

    /**
     * @see http://java.sun.com/j2se/1.5.0/docs/api/java/lang/Field.html#toString()
     */
    public String toString() {
    	StringBuffer result = new StringBuffer();
    	if(isPrivate()) {
    		result.append("private ");
    	}
    	else if(isProtected()) {
    		result.append("protected ");
    	}
    	else if(isPublic()) {
    		result.append("public ");
    	}
    	if(isStatic()) {
    		result.append("static ");
    	}
    	if(isFinal()) {
    		result.append("final ");
    	}
    	if(isTransient()) {
    		result.append("transient ");
    	}
    	if(isVolatile()) {
    		result.append("volatile ");
    	}
    	result.append(getType().getValue() + " ");
    	result.append(getParentClass().getFullyQualifiedName() + "." +getName());
    	return result.toString();
    }
}
