package com.thoughtworks.qdox.model;

public class DefaultBeanProperty implements BeanProperty
{

    private final String name;
    private JavaMethod accessor;
    private JavaMethod mutator;
    private JavaType type;

    public DefaultBeanProperty(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(JavaType type) {
        this.type = type;
    }

    public JavaType getType() {
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
