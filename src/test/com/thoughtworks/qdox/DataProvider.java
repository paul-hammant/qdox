package com.thoughtworks.qdox;

import com.thoughtworks.qdox.model.Type;

public class DataProvider {

	public static Type createType(String typeName, int dimensions){
		return new Type(typeName, dimensions);
	}
}
