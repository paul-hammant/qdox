package com.thoughtworks.qdox.model;

/**
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class BeanProperty {
    
    private final String name;
    private JavaMethod accessor;
    private JavaMethod mutator;
    private Type type;

    public BeanProperty(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public JavaMethod getAccessor() {
        return accessor;
    }

    public void setAccessor(JavaMethod accessor) {
        this.accessor = accessor;
    }

    public JavaMethod getMutator() {
        return mutator;
    }

    public void setMutator(JavaMethod mutator) {
        this.mutator = mutator;
    }
}
