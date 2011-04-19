import static org.junit.Assert.*;

import java.io.File;
import java.util.Collections;

import org.junit.Test;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.*;

public class ClasspathSourcesTest
{

    @Test
    public void testCommonsLangSources()
        throws Exception
    {
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addClassLoader( ClassLoader.getSystemClassLoader() );
        JavaClass clazz = builder.getClassByName( "org.apache.commons.lang.BitField" );
        JavaMethod method = clazz.getMethods().get( 0 );
        assertEquals( "getValue", method.getName() );
        assertEquals( "holder", method.getParameters().get( 0 ).getName() );
        assertEquals( "see", method.getTags().get( 0 ).getName() );
        assertEquals( "#setValue(int,int)", method.getTags().get( 0 ).getValue() );
        assertEquals( "param", method.getTags().get( 1 ).getName() );
        assertEquals( "holder the int data containing the bits we're interested\r\n in", method.getTags().get( 1 ).getValue() );
        assertEquals( "return", method.getTags().get( 2 ).getName() );
        assertEquals( "the selected bits, shifted right appropriately", method.getTags().get( 2 ).getValue() );
    }

}
