import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.thoughtworks.qdox.JavaProjectBuilder;

public class CanProcessTypeParameterAnnotationsTest {

    @Test
    public void canProcessTypeAnnotations() {
        try {
            JavaProjectBuilder builder = new JavaProjectBuilder();
            builder.addSourceTree(new File("./src/main/java"));
        } catch (Exception ex) {
            fail(ex.toString());
        }
    }
}
