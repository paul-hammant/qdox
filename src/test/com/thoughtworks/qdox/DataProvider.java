package com.thoughtworks.qdox;

import java.util.ArrayList;

import com.thoughtworks.qdox.model.Type;

public class DataProvider {

	public static Type createType(String typeName){
		return new Type(new ArrayList(), typeName, new ArrayList(), "");
	}
}
