import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import org.junit.Test;

import java.io.File;
import java.io.StringReader;

import static org.junit.Assert.assertNotNull;

public class Reproduce263Test {

    @Test
    public void test() {
        JavaProjectBuilder builder = new JavaProjectBuilder();
        String source = "@SuppressWarnings(\"\"\"\n" +
                " Lorem ipsum dolor \"sit\" amet\n" +
                " \"\"\")\n" +
                "public class Thingy {\n" +
                "}";
        builder.addSource( new StringReader( source ) );
        JavaClass thingy = builder.getClassByName("Thingy");
        assertNotNull(thingy);
    }
}
