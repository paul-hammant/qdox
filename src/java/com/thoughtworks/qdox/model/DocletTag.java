package com.thoughtworks.qdox.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class DocletTag implements Serializable {

	private String name;
	private String value;
	private String[] parameters;
	private Map namedParameters;

	public DocletTag(String name, String value) {
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
			StringTokenizer tokens = new StringTokenizer(value, " ");
			while(tokens.hasMoreTokens()) {
				paramsList.add(tokens.nextToken());
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
					if (k.length() > 0) {
						namedParameters.put(k, v);
					}
				}
			}
		}
		return (String)namedParameters.get(key);
	}

}
