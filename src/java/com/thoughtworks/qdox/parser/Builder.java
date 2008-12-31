package com.thoughtworks.qdox.parser;

import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.Type;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.FieldDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;
import com.thoughtworks.qdox.parser.structs.TagDef;
import com.thoughtworks.qdox.parser.structs.TypeDef;

public interface Builder {

    void addPackage(String packageName);

    void addImport(String importName);

    void addJavaDoc(String text);

    void addJavaDocTag(TagDef def);

    void beginClass(ClassDef def);

    void endClass();

    void addMethod(MethodDef def);

    void addField(FieldDef def);

    void addAnnotation(Annotation annotation);
    
    /**
     * @deprecated
     */
    Type createType(String name, int dimensions);
    
    Type createType(TypeDef name);
}
