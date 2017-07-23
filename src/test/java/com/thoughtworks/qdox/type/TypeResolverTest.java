package com.thoughtworks.qdox.type;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.type.TypeResolver;

public class TypeResolverTest
{
    private TypeResolver typeResolver;
    
    @Mock
    private ClassLibrary classLibrary;

    @Before 
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void testResolveTypeInnerClass() {
        when( classLibrary.hasClassReference( "p.X$DogFood" ) ).thenReturn( true );
        
        typeResolver = TypeResolver.byClassName( "p.X", classLibrary, Collections.<String>emptyList() );
        
        assertEquals("p.X$DogFood", typeResolver.resolveType("DogFood"));
        assertEquals(null, typeResolver.resolveType("Food"));
    }
    
    @Test
    public void testResolving()
    {
        when( classLibrary.hasClassReference( "foo.Bar" ) ).thenReturn( true );
        
        typeResolver = TypeResolver.byPackageName( null, classLibrary, Collections.singletonList( "foo.*" )  );
        
        assertEquals( "foo.Bar", typeResolver.resolveType( "Bar" ) );
        assertEquals( "foo.Bar", typeResolver.resolveType( "foo.Bar" ) );
    }
    
    @Test
    public void testResolveJavaPrimitive() {
        List<String> imports = new ArrayList<String>();
        imports.add("bogus.int");
        imports.add("bogus.double");
        String[] primitives = new String[]{
            "boolean", "byte", "char", "double",
            "float", "int", "long", "short", "void"
        };
        typeResolver = TypeResolver.byPackageName( null, classLibrary, imports );
        for (int i = 0; i < primitives.length; i++) {
            assertEquals(primitives[i], typeResolver.resolveType(primitives[i]));
        }
    }

    @Test
    public void testDontResolveMissingClasses() {
        typeResolver = TypeResolver.byPackageName( null, classLibrary, Collections.<String>emptyList() );
        assertEquals(null, typeResolver.resolveType("not.Found"));
    }

    @Test
    public void testResolveFullyQualifiedName()
    {
        typeResolver = TypeResolver.byPackageName( null, classLibrary, Collections.<String>emptyList() );
        when( classLibrary.hasClassReference( "open.Bar" ) ).thenReturn( true );
        assertEquals( "open.Bar", typeResolver.resolveType( "open.Bar" ) );
    }
    
    @Test
    public void testResolveFullyQualifiedImport() {
        typeResolver = TypeResolver.byPackageName( null, classLibrary, Collections.singletonList( "foo.Bar" ) );
        when( classLibrary.hasClassReference( "foo.Bar" ) ).thenReturn( true );
        assertEquals( "foo.Bar", typeResolver.resolveType( "Bar" ) );
    }

    @Test
    public void testResolveChooseFirstMatchingImport() {
        List<String> imports = new ArrayList<String>();
        imports.add( "bogus.package.MyType" );
        imports.add( "com.thoughtworks.qdox.model.Type" );
        imports.add( "another.package.Type" );
        typeResolver = TypeResolver.byPackageName( null, classLibrary, imports );
        when( classLibrary.hasClassReference( "bogus.package.MyType" ) ).thenReturn( true );
        when( classLibrary.hasClassReference( "com.thoughtworks.qdox.model.Type" ) ).thenReturn( true );
        when( classLibrary.hasClassReference( "another.package.Type" ) ).thenReturn( true );
        assertEquals( "com.thoughtworks.qdox.model.Type", typeResolver.resolveType( "Type" ) );
    }

    @Test
    public void testResolveSamePackage() {
        typeResolver = TypeResolver.byPackageName( "foo", classLibrary, Collections.<String>emptyList() );
        when( classLibrary.hasClassReference( "foo.Bar" ) ).thenReturn( true );
        assertEquals("foo.Bar", typeResolver.resolveType("Bar"));
    }

    @Test
    public void testResolveFullyQualifiedTrumpsSamePackage() {
        typeResolver = TypeResolver.byPackageName( "foo", classLibrary, Collections.<String>emptyList() );
        when( classLibrary.hasClassReference( "foo.Bar" ) ).thenReturn( true );
        when( classLibrary.hasClassReference( "open.Bar" ) ).thenReturn( true );
        assertEquals("open.Bar", typeResolver.resolveType("open.Bar"));
    }

    @Test
    public void testResolveFullyQualifiedTrumpsWildCard() {
        List<String> imports = new ArrayList<String>();
        imports.add("foo.*");
        imports.add("bar.Bar");
        typeResolver = TypeResolver.byPackageName( null, classLibrary, imports );
        when( classLibrary.hasClassReference( "foo.Bar" ) ).thenReturn( true );
        when( classLibrary.hasClassReference( "bar.Bar" ) ).thenReturn( true );
        assertEquals("bar.Bar", typeResolver.resolveType("Bar"));
    }

    @Test
    public void testResolveWildcard() {
        typeResolver = TypeResolver.byPackageName( null, classLibrary, Collections.singletonList("foo.*") );
        when( classLibrary.hasClassReference( "foo.Bar" ) ).thenReturn( true );
        assertEquals("foo.Bar", typeResolver.resolveType("Bar"));
    }

    @Test
    public void testResolveJavaLangClass() {
        typeResolver = TypeResolver.byPackageName( null, classLibrary, Collections.<String>emptyList() );
        when( classLibrary.hasClassReference( "java.lang.System" ) ).thenReturn( true );
        assertEquals("java.lang.System", typeResolver.resolveType("System"));
    }

    @Test
    public void testResolveSamePackageTrumpsWildcard() {
        List<String> imports = new ArrayList<String>();
        imports.add("com.thoughtworks.qdox.model.Type");
        imports.add("foo.*");
        typeResolver = TypeResolver.byPackageName( "com.thoughtworks.qdox.model", classLibrary, imports );
        when( classLibrary.hasClassReference( "com.thoughtworks.qdox.model.Type" ) ).thenReturn( true );
        when( classLibrary.hasClassReference( "foo.Type" ) ).thenReturn( true );
        assertEquals("com.thoughtworks.qdox.model.Type", typeResolver.resolveType("Type"));
    }

    @Test
    public void testResolveFullyQualifiedInnerClass() {
        typeResolver = TypeResolver.byPackageName( "foo", classLibrary, Collections.<String>emptyList() );
        when( classLibrary.hasClassReference( "foo.Bar$Fnord" ) ).thenReturn( true );
        assertEquals("foo.Bar$Fnord", typeResolver.resolveType("foo.Bar.Fnord"));
    }

    @Test
    public void testResolvePartiallySpecifiedInnerClass() {
        typeResolver = TypeResolver.byPackageName( "foo", classLibrary, Collections.singletonList("java.util.*") );
        when( classLibrary.hasClassReference( "foo.Bar$Fnord" ) ).thenReturn( true );
        when( classLibrary.hasClassReference( "java.util.Map$Entry" ) ).thenReturn( true );
        assertEquals("foo.Bar$Fnord", typeResolver.resolveType("Bar.Fnord"));
        assertEquals("java.util.Map$Entry", typeResolver.resolveType("Map.Entry"));
    }
}
