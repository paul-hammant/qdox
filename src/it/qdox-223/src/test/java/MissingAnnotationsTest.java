import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.thoughtworks.qdox.JavaProjectBuilder;

public class MissingAnnotationsTest {

    @Test
    public void packageInfoAnnotationsMissing() {

        //package y, package-info not first file, deprecated annotation found
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSourceTree(new File("./src/main/java/com/y"));
        assertEquals(1, builder.getPackages().iterator().next().getAnnotations().size());

         //package x, package-info not first file, test fails, deprecated annotation not found
        builder = new JavaProjectBuilder();
        builder.addSourceTree(new File("./src/main/java/com/x"));
        assertEquals(1, builder.getPackages().iterator().next().getAnnotations().size());
    }
}
