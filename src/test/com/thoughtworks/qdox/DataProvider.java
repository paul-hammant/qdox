package com.thoughtworks.qdox;

import java.util.ArrayList;

import com.thoughtworks.qdox.model.Type;
import com.thoughtworks.qdox.model.ClassLibrary;

public class DataProvider {

	public static Type createType(String typeName, int dimensions){
		return new Type(typeName, dimensions);
	}
}
