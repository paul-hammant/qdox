package com.thoughtworks.qdox.parser;

import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.FieldDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;

/**
 * @mock
 */
public interface Builder {

	void addPackage(String packageName);
	void addImport(String importName);

	void addJavaDoc(String text);
	void addJavaDocTag(String tag, String text);

	void beginClass(ClassDef def);
	void endClass();

	void addMethod(MethodDef def);
	void addField(FieldDef def);

}
