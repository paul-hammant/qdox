package com.thoughtworks.qdox.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class DefaultDocletTag implements DocletTag {

    private String name;
    private String value;
    private String[] parameters;
    private Map namedParameters;
    private String[] quotes = new String[]{"\"", "'"};

    public DefaultDocletTag(String name, String value) {
        this.name = name;
        this.value = value;
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

    public String getNamedParameter(String key) {
        if (namedParameters == null) {
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
        }
        return (String) namedParameters.get(key);
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


