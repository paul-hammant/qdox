package com.thoughtworks.qdox.model;

import com.thoughtworks.qdox.model.util.TagParser;

import java.util.Map;

public class DefaultDocletTag implements DocletTag {

    private final String name;
    private final String value;
    private final int lineNumber;

    private String[] parameters;
    private Map namedParameters;
    private AbstractBaseJavaEntity context;

    public DefaultDocletTag(String name, String value, 
                            AbstractBaseJavaEntity context, 
                            int lineNumber) 
    {
        this.name = name;
        this.value = value;
        this.context = context;
        this.lineNumber = lineNumber;
    }

    public DefaultDocletTag(String name, String value) {
        this(name, value, null, 0);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String[] getParameters() {
        if (parameters == null) {
            parameters = TagParser.parseParameters(value);
        }
        return parameters;
    }

    public Map getNamedParameterMap() {
        if (namedParameters == null) {
            namedParameters = TagParser.parseNamedParameters(value);
        }
        return namedParameters;
    }
    
    public String getNamedParameter(String key) {
        return (String) getNamedParameterMap().get(key);
    }

    public final AbstractBaseJavaEntity getContext() {
        return context;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}


