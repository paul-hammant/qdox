package com.thoughtworks.qdox.model;

public class DefaultJavaFieldTest
    extends JavaFieldTest
{

    public DefaultJavaFieldTest( String s )
    {
        super( s );
    }

    public JavaField newJavaField()
    {
        return new JavaField();
    }

    public JavaField newJavaField( Type type, String name )
    {
        return new JavaField(type, name);
    }

    public JavaClass newJavaClass( String fullname )
    {
        return new JavaClass(fullname);
    }

    public JavaPackage newJavaPackage( String name )
    {
        return new JavaPackage(name);
    }

    public Type newType( String fullname )
    {
        return new Type( fullname );
    }

    public Type newType( String fullname, int dimensions )
    {
        return new Type( fullname, dimensions );
    }

    public void setComment( JavaField fld, String comment )
    {
        fld.setComment( comment );
    }

    public void setInitializationExpression( JavaField fld, String expression )
    {
        fld.setInitializationExpression( expression );
    }

    public void setModifiers( JavaField fld, String[] modifiers )
    {
        fld.setModifiers( modifiers );
    }

    public void setName( JavaField fld, String name )
    {
        fld.setName( name );
    }

    public void setType( JavaField fld, Type type )
    {
        fld.setType( type );
    }


    public void addField( JavaClass clazz, JavaField fld )
    {
        clazz.addField( fld );
    }

    public void addClass( JavaPackage pckg, JavaClass clazz )
    {
        pckg.addClass( clazz );
    }

}
