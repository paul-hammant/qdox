package com.thoughtworks.qdox.testdata;

// used as test data for properties and binary support
public class PropertyClass extends Superclass {
    public static boolean aField;

    static{
        aField = true;
    }

    public PropertyClass() {}

    // not a bean property, sice it's static
    public static String getFoo() {
        return "foo";
    }

    public boolean isBar() {
        return aField;
    }

    // not a bean property
    public void set(int i) {}

    // not a bean property
    public final String get() {return null;}
}

class Superclass {
    public int shouldntBeInherited;
    public int getShouldntBeInherited() {
        return shouldntBeInherited;
    }
}