package com.thoughtworks.qdox;

import com.thoughtworks.qdox.model.JavaClass;

public interface Searcher {

    boolean eval(JavaClass cls);

}
