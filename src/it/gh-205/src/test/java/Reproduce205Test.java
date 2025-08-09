import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaPackage;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Reproduce205Test {

    @Test
    public void test() {
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSourceTree(new File("src/main/java"));
        JavaPackage pckg = builder.getJavaPackage("com.stemlaur.anki.domain.catalog");
        assertNotNull(pckg);
        assertEquals("This domain allows users to create anki decks.", pckg.getComment());
        assertEquals(1, pckg.getAnnotations().size());
        assertEquals("@com.stemlaur.livingdocumentation.annotation.DomainLayer(name = \"Catalog\")", pckg.getAnnotations().get(0).toString());
    }
}
