package com.thoughtworks.qdox.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.thoughtworks.qdox.model.util.TagParser;

public class DefaultDocletTag implements DocletTag {

    private final String name;
    private final String value;
    private final int lineNumber;

    private String[] parameters;
    private Map namedParameters;
    private String[] quotes = new String[]{"\"", "'"};
    private AbstractJavaEntity context;

    public DefaultDocletTag(String name, String value, 
                            AbstractJavaEntity context, 
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
            parameters = TagParser.parseWords(value);
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

    public final AbstractJavaEntity getContext() {
        return context;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    private String trim(String value, String[] strings) {
        for (int i = 0; i < strings.length; i++) {
            String string = strings[i];
            while (value.startsWith(string)) {
                value = value.substring(string.length(), value.length());
            }
            while (value.endsWith(string)) {
                value = value.substring(0, value.length() - string.length());
            }
        }
        return value;
    }

}


