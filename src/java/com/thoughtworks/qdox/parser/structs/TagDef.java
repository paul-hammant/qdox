package com.thoughtworks.qdox.parser.structs;

public class TagDef {

    public String name;
    public String text;
    public int lineNumber;
    
    public TagDef(String name, String text, int lineNumber) {
        this.name = name;
        this.text = text;
        this.lineNumber = lineNumber;
    }

    public TagDef(String name, String text) {
        this(name, text, -1);
    }
    
    public boolean equals(Object obj) {
        TagDef tagDef = (TagDef) obj;
        return tagDef.name.equals(name)
                && tagDef.text.equals(text)
                && tagDef.lineNumber == lineNumber;
    }

    public int hashCode() {
        return name.hashCode() + text.hashCode() + lineNumber;
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append('@');
        result.append(name);
        result.append(" => \"");
        result.append(text);
        result.append("\" @ line ");
        result.append(lineNumber);
        return result.toString();
    }

}
