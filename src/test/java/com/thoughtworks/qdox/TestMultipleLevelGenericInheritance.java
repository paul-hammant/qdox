package com.thoughtworks.qdox;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

public class TestMultipleLevelGenericInheritance
{
    private JavaProjectBuilder builder;

    @BeforeEach
    public void setUp()
    {
        builder = createBuilder();
    }

    @Test
    public void testMethodParametersOfIheritedMethodsAreCorrect()
    {
        final JavaClass clazz = builder.getClassByName( "HelloService" );
        final List<JavaMethod> methods = clazz.getMethods( true );

        JavaMethod method;

        method = methods.get( 0 );
        Assertions.assertEquals("get", method.getName());

        method = methods.get( 1 );
        Assertions.assertEquals("set", method.getName());
        assertFirstParameterIs( "Dto", method );

        method = methods.get( 2 );
        Assertions.assertEquals("validate", method.getName());
        assertFirstParameterIs( "Dto", method );

    }

    private void assertFirstParameterIs( final String type, final JavaMethod method )
    {
        List<JavaType> parameterTypes;
        parameterTypes = method.getParameterTypes( true );
        Assertions.assertEquals(1, parameterTypes.size());
        Assertions.assertEquals(type, parameterTypes.get( 0 ).getCanonicalName());
    }

    private JavaProjectBuilder createBuilder()
    {
        final JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSource( getDtoClass() );
        builder.addSource( getGetterClass() );
        builder.addSource( getSetterClass() );
        builder.addSource( getValidatorClass() );
        builder.addSource( getHelloService() );
        return builder;
    }

    private Reader getDtoClass()
    {
        return new StringReader( "public class Dto {}" );
    }

    private Reader getGetterClass()
    {
        return new StringReader( "public interface Getter<T extends Object> { T get(); }" );
    }

    private Reader getValidatorClass()
    {
        return new StringReader( "public interface Validator<T extends Object> { void validate(T obj); }" );
    }

    private Reader getSetterClass()
    {
        return new StringReader( "public interface Setter<T extends Object> extends Validator<T> { void set(T obj); }" );
    }

    private Reader getHelloService()
    {
        return new StringReader( "public interface HelloService extends Getter<Dto>, Setter<Dto> {}" );
    }

}
