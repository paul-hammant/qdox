package com.thoughtworks.qdox;

import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

/**
 * Examples from <a href="https://docs.oracle.com/en/java/javase/16/text-blocks/index.html">https://docs.oracle.com/en/java/javase/16/text-blocks/index.html</a>
 * @author Robert Scholte
 *
 */
public class TextBlocksTest
{
    private JavaProjectBuilder builder = new JavaProjectBuilder();
    
    @Test
    public void test()
    {
        String source = "interface Something { "
            + "// Using a text block\r\n"
            + "String tbName = \"\"\"\r\n"
            + "                Pat Q. Smith\"\"\"; }";
        JavaSource javaSource = builder.addSource( new StringReader( source ) );
        JavaField javaField = javaSource.getClasses().get( 0 ).getFieldByName( "tbName" );
        Assertions.assertEquals("\"\"\"\r\n"
                        + "                Pat Q. Smith\"\"\"", javaField.getInitializationExpression());
    }
    
}
