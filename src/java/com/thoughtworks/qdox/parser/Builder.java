package com.thoughtworks.qdox.parser;

import com.thoughtworks.qdox.parser.structs.FieldDef;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;

/**
 * @mock:generate
 */
public interface Builder {

	void addPackage(String packageName);
	void addImport(String importName);

	void addJavaDoc(String text);
	void addJavaDocTag(String tag, String text);

	void addClass(ClassDef def);
	void addMethod(MethodDef def);
	void addField(FieldDef def);

}
