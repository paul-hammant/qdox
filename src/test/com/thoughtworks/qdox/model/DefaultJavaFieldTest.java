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
        JavaSource source = new DefaultJavaSource( null );
        JavaClass javaClass = new JavaClass( source );
        DefaultJavaField result = new DefaultJavaField();
        result.setParentClass( javaClass );
        return result;
    }

    public JavaField newJavaField( Type type, String name )
    {
        return new DefaultJavaField(type, name);
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
        ((DefaultJavaField) fld).setComment( comment );
    }

    public void setInitializationExpression( JavaField fld, String expression )
    {
        ((DefaultJavaField) fld).setInitializationExpression( expression );
    }

    public void setModifiers( JavaField fld, String[] modifiers )
    {
        ((DefaultJavaField) fld).setModifiers( modifiers );
    }

    public void setName( JavaField fld, String name )
    {
        ((DefaultJavaField) fld).setName( name );
    }

    public void setType( JavaField fld, Type type )
    {
        ((DefaultJavaField) fld).setType( type );
    }


    public void addField( JavaClass clazz, JavaField fld )
    {
        clazz.addField( fld );
        ((DefaultJavaField) fld).setParentClass( clazz );
    }

    public void addClass( JavaPackage pckg, JavaClass clazz )
    {
        pckg.addClass( clazz );
        clazz.setJavaPackage( pckg );
    }

}
