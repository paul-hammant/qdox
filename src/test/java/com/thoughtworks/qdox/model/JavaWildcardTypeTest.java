package com.thoughtworks.qdox.model;

import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.library.SortedClassLibraryBuilder;
import com.thoughtworks.qdox.model.impl.DefaultJavaWildcardType;
import com.thoughtworks.qdox.type.TypeResolver;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class JavaWildcardTypeTest<T extends JavaWildcardType>
{
    private TypeResolver typeResolver;

    @BeforeEach
    public void setUp() {
        ClassLibrary classLibrary = new SortedClassLibraryBuilder().appendDefaultClassLoaders().getClassLibrary();
        List<String> typeList = new ArrayList<String>();
        typeList.add("java.lang.*");
        typeList.add("java.util.*");
        typeResolver = TypeResolver.byClassName( "Object", classLibrary, typeList );
    }

    public abstract T newWildcardType();

    public abstract T newWildcardType( String fullname, TypeResolver typeResolver, DefaultJavaWildcardType.BoundType boundType );

    @Test
    public void testGetLowerBounds() throws NoSuchFieldException
    {
        T wildcardType = newWildcardType( "java.util.List", typeResolver, DefaultJavaWildcardType.BoundType.SUPER );
        assertEquals( "java.lang.Object", wildcardType.getUpperBounds()[0].getFullyQualifiedName() );
        assertEquals( "java.util.List", wildcardType.getLowerBounds()[0].getFullyQualifiedName() );
    }

    @Test
    public void testGetUpperBounds() throws NoSuchFieldException
    {
        T wildcardType = newWildcardType( "java.util.List", typeResolver, DefaultJavaWildcardType.BoundType.EXTENDS );
        assertEquals( "java.util.List", wildcardType.getUpperBounds()[0].getFullyQualifiedName() );
        assertEquals( 0, wildcardType.getLowerBounds().length );
    }

}
