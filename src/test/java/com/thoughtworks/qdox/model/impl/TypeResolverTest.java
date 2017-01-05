package com.thoughtworks.qdox.model.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.thoughtworks.qdox.library.ClassLibrary;

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
    public void testResolveTypeInnerClass() throws Exception {
        when( classLibrary.hasClassReference( "p.X$DogFood" ) ).thenReturn( true );
        
        typeResolver = TypeResolver.byClassName( "p.X", classLibrary, Collections.<String>emptyList() );
        
        assertEquals("p.X$DogFood", typeResolver.resolveType("DogFood"));
        assertEquals(null, typeResolver.resolveType("Food"));
    }

}
