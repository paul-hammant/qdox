package com.thoughtworks.qdox.library;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.ModelBuilder;
import com.thoughtworks.qdox.parser.structs.ClassDef;

/**
 * This library always resolve a className by generating an empty JavaClass Model
 */
public class ClassNameLibrary
    extends AbstractClassLibrary
{

    public ClassNameLibrary()
    {
    }

    /**
     * {@inheritDoc}
     */
    protected JavaClass resolveJavaClass( String name )
    {
        ModelBuilder unknownBuilder = new ModelBuilder();
        ClassDef classDef = new ClassDef();
        classDef.name = name;
        unknownBuilder.beginClass( classDef );
        unknownBuilder.endClass();
        JavaSource unknownSource = unknownBuilder.getSource();
        JavaClass result = unknownSource.getClasses()[0];
        return result;
    }
    
    protected boolean containsClassByName( String name )
    {
        return false;
    }

}
