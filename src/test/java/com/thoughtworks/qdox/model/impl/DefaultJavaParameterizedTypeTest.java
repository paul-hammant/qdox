package com.thoughtworks.qdox.model.impl;

import com.thoughtworks.qdox.library.ClassLoaderLibrary;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.type.TypeResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultJavaParameterizedTypeTest
{
    // Fields to verify if reflection acts the same
    public List<String> listOfString;

    public List<String>[] listOfStringArray;

    public List<String>[][] listOfStringArrayArray;
    
    public Map.Entry<Integer, String> mapEntryIntegerForString;
    
    private TypeResolver typeResolver;
    
    @BeforeEach
    public void initMocks() {
        ClassLoaderLibrary classLibrary = new ClassLoaderLibrary( null );
        classLibrary.addDefaultLoader();
        typeResolver = TypeResolver.byPackageName( null, classLibrary , null );
    }

    @Test
    public void testListOfString() throws Exception
    {
        DefaultJavaParameterizedType type = new DefaultJavaParameterizedType( "java.util.List", "List", 0, typeResolver );
        DefaultJavaType genType = new DefaultJavaType( "java.lang.String", "String", 0, typeResolver );
        type.setActualArgumentTypes( Collections.<JavaType>singletonList( genType ) );

        Field field = DefaultJavaParameterizedTypeTest.class.getField( "listOfString" );

        Assertions.assertEquals("List", type.getValue());
        Assertions.assertEquals("List<String>", type.getGenericValue());

        Assertions.assertEquals("java.util.List", type.getBinaryName());
        Assertions.assertEquals("java.util.List", type.getFullyQualifiedName());

        
        // Also compare with reflect implementations
        assertThat(type.getSimpleName()).isEqualTo("List").isEqualTo(field.getType().getSimpleName());
        assertThat(type.getCanonicalName()).isEqualTo("java.util.List").isEqualTo(field.getType().getCanonicalName());
        
        assertThat(type.getGenericCanonicalName()).isEqualTo("java.util.List<java.lang.String>")/*,
                                                           equalTo(field.getGenericType().getTypeName())*/;
    }

    @Test
    public void testListOfStringArray() throws Exception
    {
        DefaultJavaParameterizedType type = new DefaultJavaParameterizedType( "java.util.List", "List", 1, typeResolver );
        DefaultJavaType genType = new DefaultJavaType( "java.lang.String", "String", 0, typeResolver );
        type.setActualArgumentTypes( Collections.<JavaType>singletonList( genType ) );
        
        Field field = DefaultJavaParameterizedTypeTest.class.getField( "listOfStringArray" );

        Assertions.assertEquals("List[]", type.getValue());
        Assertions.assertEquals("List<String>[]", type.getGenericValue());

        Assertions.assertEquals("java.util.List", type.getBinaryName());
        Assertions.assertEquals("java.util.List[]", type.getFullyQualifiedName());

        // Also compare with reflect implementations
        assertThat(type.getSimpleName()).isEqualTo("List[]").isEqualTo(field.getType().getSimpleName());
        assertThat(type.getCanonicalName()).isEqualTo("java.util.List[]").isEqualTo(field.getType().getCanonicalName());
        
        assertThat(type.getGenericCanonicalName()).isEqualTo("java.util.List<java.lang.String>[]")/*,
                                                           equalTo(field.getGenericType().getTypeName())*/;
        
        assertThat(type.getComponentType().getSimpleName()).isEqualTo("List").isEqualTo(field.getType().getComponentType().getSimpleName());
        assertThat(type.getComponentType().getCanonicalName()).isEqualTo("java.util.List").isEqualTo(field.getType().getComponentType().getCanonicalName());
    }
    
    @Test
    public void testListOfStringArrayArray() throws Exception
    {
        DefaultJavaParameterizedType type = new DefaultJavaParameterizedType( "java.util.List", "List", 2, typeResolver );
        DefaultJavaType genType = new DefaultJavaType( "java.lang.String", "String", 0, typeResolver );
        type.setActualArgumentTypes( Collections.<JavaType>singletonList( genType ) );
        
        Field field = DefaultJavaParameterizedTypeTest.class.getField( "listOfStringArrayArray" );

        Assertions.assertEquals("List[][]", type.getValue());
        Assertions.assertEquals("List<String>[][]", type.getGenericValue());

        Assertions.assertEquals("java.util.List", type.getBinaryName());
        Assertions.assertEquals("java.util.List[][]", type.getFullyQualifiedName());

        // Also compare with reflect implementations
        assertThat(type.getSimpleName()).isEqualTo("List[][]").isEqualTo(field.getType().getSimpleName());
        assertThat(type.getCanonicalName()).isEqualTo("java.util.List[][]").isEqualTo(field.getType().getCanonicalName());
        
        
//        assertThat(type.getComponentType().getSimpleName()).isEqualTo("List").isEqualTo(field.getType().getComponentType().getSimpleName());
    }
    
    @Test
    public void testMapEntryIntegerForString() throws Exception
    {
        DefaultJavaParameterizedType type = new DefaultJavaParameterizedType( "java.util.Map$Entry", "Map.Entry", 0, typeResolver );
        DefaultJavaType keyType = new DefaultJavaType( "java.lang.Integer", "Integer", 0, typeResolver );
        DefaultJavaType valueType = new DefaultJavaType( "java.lang.String", "String", 0, typeResolver );
        type.setActualArgumentTypes( Arrays.<JavaType>asList( keyType, valueType ) );
        
        Field field = DefaultJavaParameterizedTypeTest.class.getField( "mapEntryIntegerForString" );

        Assertions.assertEquals("Map.Entry", type.getValue());
        Assertions.assertEquals("Map.Entry<Integer,String>", type.getGenericValue());

        Assertions.assertEquals("java.util.Map$Entry", type.getBinaryName());
        Assertions.assertEquals("java.util.Map.Entry", type.getFullyQualifiedName());

        // Also compare with reflect implementations
        assertThat(type.getSimpleName()).isEqualTo( "Entry" ).isEqualTo(field.getType().getSimpleName());
        assertThat(type.getCanonicalName()).isEqualTo("java.util.Map.Entry").isEqualTo(field.getType().getCanonicalName());
        // this is buggy for multiple JDKs 
        assertThat(type.getGenericCanonicalName()).isEqualTo("java.util.Map.Entry<java.lang.Integer,java.lang.String>") /*,
                                                           .isEqualTo(field.getGenericType().getTypeName())*/ ;
    }

}
