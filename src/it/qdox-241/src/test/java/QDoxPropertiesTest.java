import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaSource;

public class QDoxPropertiesTest
{

    @Test
    public void customStateStackSize() throws Exception
    {
        JavaProjectBuilder builder = new JavaProjectBuilder();
        JavaSource source = builder.addSource( new File("./src/test/resources/Test.java") );
        
        assertNotNull( source.getClassByName( "Test$A$B$C$D$E$F$G$H$I$J$K$L$M$N" ) );
    }
}
