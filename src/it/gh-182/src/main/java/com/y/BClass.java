package com.y;

import com.AnnotationA;
import java.util.ArrayList;
import java.util.List;

public class BClass {
    public List<java.util.@com.AnnotationA List<Long>> fooField;
    public List<@com.AnnotationA List<Long>> barField;
    public List<java.util.@AnnotationA List<Long>> bazField;
    public List<@AnnotationA List<Long>> borkField;

    public List<java.util.@com.AnnotationA List<Long>> foo() { return null; }
    public List<@com.AnnotationA List<Long>> bar() { return null; }
    public List<java.util.@AnnotationA List<Long>> baz() { return null; }
    public List<@AnnotationA List<Long>> bork() { return null; }

    public BClass.Container<com.y.BClass.@com.AnnotationA Container<Long>> eggs() { return null; }
    public BClass.Container<BClass.@AnnotationA Container<Long>> spam() { return null; }
    public BClass.@com.AnnotationA AnotherContainer<?> junk() { return null; }
    public BClass.@AnnotationA AnotherContainer<?> moreJunk() { return null; }

    static {
        new ArrayList<java.util.@com.AnnotationA List<Long>>();
        new ArrayList<@com.AnnotationA List<Long>>();
        new ArrayList<java.util.@AnnotationA List<Long>>();
        new ArrayList<@AnnotationA List<Long>>();
    }

    static class Container<@AnnotationA T> {}
    static class AnotherContainer<@com.AnnotationA T> {}
}
