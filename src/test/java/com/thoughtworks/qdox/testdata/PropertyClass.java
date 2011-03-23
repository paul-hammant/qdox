package com.thoughtworks.qdox.testdata;

// used as test data for properties and binary support and other tests

/**
 * @foo bar="zap"
 * @aslak.foo bar="dodgeme"
 */
public class PropertyClass extends Superclass {
    public static boolean aField;

    static {
        aField = true;
    }

    protected int protectedField;

    private int privateField;

    public PropertyClass() {
    }

    protected PropertyClass(PropertyClass other) {
        privateField = other.privateField;
    }

    // not a bean property, sice it's static
    public static String getFoo() {
        return "foo";
    }

    public boolean isBar() {
        return aField;
    }

    // not a bean property
    public void set(int i) {
    }

    // not a bean property
    public final String get() {
        return null;
    }

    protected void protectedMethod() {
        privateField = 2;
        protectedField = privateMethod();
    }

    private int privateMethod() {
        return privateField;
    }
}

class Superclass {
    public int shouldntBeInherited;

    public int getShouldntBeInherited() {
        return shouldntBeInherited;
    }
}