package com.thoughtworks.qdox.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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
            List paramsList = new ArrayList();
            StringTokenizer tokens = new StringTokenizer(value);
            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken();
                for (int i = 0; i < quotes.length; i++) {
                    String quote = quotes[i];
                    if (token.indexOf(quote) != -1) {
                        while (!token.endsWith(quote)) {
                            if (tokens.hasMoreTokens()) {
                                token += " " + tokens.nextToken();
                            } else {
                                break;
                            }
                        }
                        break;  // we only want to match against one type of quote
                    }
                }
                paramsList.add(token);
            }
            parameters = new String[paramsList.size()];
            paramsList.toArray(parameters);
        }
        return parameters;
    }

    public Map getNamedParameterMap() {
        if (namedParameters != null) return namedParameters;
        namedParameters = new HashMap();
        String[] params = getParameters();
        for (int i = 0; i < params.length; i++) {
            String param = params[i];
            int eq = param.indexOf('=');
            if (eq > -1) {
                String k = param.substring(0, eq);
                String v = param.substring(eq + 1);
                v = trim(v, quotes);
                if (k.length() > 0) {
                    namedParameters.put(k, v);
                }
            }
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


