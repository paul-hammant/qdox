package com.thoughtworks.qdox.model;

import java.util.List;
import java.util.Map;

import com.thoughtworks.qdox.model.util.TagParser;

public class DefaultDocletTag implements DocletTag {

    private final String name;
    private final String value;
    private final int lineNumber;

    private List<String> parameters;
    private Map<String, String> namedParameters;
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

    public List<String> getParameters() {
        if (parameters == null) {
            parameters = TagParser.parseParameters(value);
        }
        return parameters;
    }

    public Map<String, String> getNamedParameterMap() {
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


