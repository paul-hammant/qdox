package com.thoughtworks.qdox.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Collections;
import java.util.Iterator;

public class DocletTag implements Serializable {

    private String name;
    private String value;
    private String[] parameters;
    private Map namedParameters;
    private String[] quotes = new String[]{"\"", "'"};

    public DocletTag(String name, String value) {
        this(name, value, Collections.EMPTY_MAP);
    }

    public DocletTag(String name, String value, Map properties) {
        this.name = name;
        this.value = convertProperties(value, properties);
    }

    private String convertProperties(String value, Map properties) {
        if (value == null) {
            return null;
        }

        List fragments = new ArrayList();
        List propertyRefs = new ArrayList();

        parsePropertyString(value, fragments, propertyRefs);

        StringBuffer sbuf = new StringBuffer();
        Iterator i = fragments.iterator();
        Iterator j = propertyRefs.iterator();

        while (i.hasNext()) {
            String fragment = (String) i.next();
            if (fragment == null) {
                String propertyName = (String) j.next();
                fragment = (properties.containsKey(propertyName)) ? (String) properties.get(propertyName)
                        : "${" + propertyName + '}';
            }
            sbuf.append(fragment);
        }

        return sbuf.toString();
    }

    private void parsePropertyString(String value, List fragments,
                                     List propertyRefs) {
        int prev = 0;
        int pos;
        //search for the next instance of $ from the 'prev' position
        while ((pos = value.indexOf("$", prev)) >= 0) {

            //if there was any text before this, add it as a fragment
            //TODO, this check could be modified to go if pos>prev;
            //seems like this current version could stick empty strings
            //into the list
            if (pos > 0) {
                fragments.add(value.substring(prev, pos));
            }
            //if we are at the end of the string, we tack on a $
            //then move past it
            if (pos == (value.length() - 1)) {
                fragments.add("$");
                prev = pos + 1;
            } else if (value.charAt(pos + 1) != '{') {
                //peek ahead to see if the next char is a property or not
                //not a property: insert the char as a literal
                /*
                fragments.addElement(value.substring(pos + 1, pos + 2));
                prev = pos + 2;
                */
                if (value.charAt(pos + 1) == '$') {
                    //backwards compatibility two $ map to one mode
                    fragments.add("$");
                    prev = pos + 2;
                } else {
                    //new behaviour: $X maps to $X for all values of X!='$'
                    fragments.add(value.substring(pos, pos + 2));
                    prev = pos + 2;
                }

            } else {
                //property found, extract its name or bail on a typo
                int endName = value.indexOf('}', pos);
                if (endName < 0) {
                    throw new RuntimeException("Syntax error in property: "
                            + value);
                }
                String propertyName = value.substring(pos + 2, endName);
                fragments.add(null);
                propertyRefs.add(propertyName);
                prev = endName + 1;
            }
        }
        //no more $ signs found
        //if there is any tail to the file, append it
        if (prev < value.length()) {
            fragments.add(value.substring(prev));
        }
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


