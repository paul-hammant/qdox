package com.thoughtworks.qdox.parser.structs;

import java.util.LinkedHashSet;
import java.util.Set;

public class CompactConstructorDef extends LocatedDef {

    private Set<String> modifiers = new LinkedHashSet<String>();
    private String body;

    public void setModifiers(Set<String> modifiers) {
        this.modifiers = modifiers;
    }

    public Set<String> getModifiers() {
        return modifiers;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

}
