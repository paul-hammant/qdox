package com.thoughtworks.qdox;

import junit.framework.TestCase;

import com.thoughtworks.qdox.OuterClazz.InnerClazz;

public class ReflectTest
    extends TestCase
{

    public void testDeclarativeClass() throws Exception {
        assertEquals( InnerClazz.class.getDeclaringClass(), ReflectTest.class );        
    }
}
class OuterClazz {
    
    class InnerClazz {

        class MostInnerClass {
            
        }
    }
}


