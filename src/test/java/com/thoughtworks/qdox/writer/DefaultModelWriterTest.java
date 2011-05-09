package com.thoughtworks.qdox.writer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;

import com.thoughtworks.qdox.model.DefaultDocletTag;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotatedElement;
import com.thoughtworks.qdox.writer.DefaultModelWriter;

public class DefaultModelWriterTest {

	private DefaultModelWriter modelWriter;
	
	@Before
	public void onSetup(){
		modelWriter = new DefaultModelWriter();
	}
	
    public void testCommentToString() {
        // setup
    	JavaAnnotatedElement annotatedElement = mock(JavaAnnotatedElement.class);
    	when(annotatedElement.getComment()).thenReturn("Hello");

        // expectation
        String expected = ""
                + "/**\n"
                + " * Hello\n"
                + " */\n";

        // run
        modelWriter.commentHeader(annotatedElement);

        // verify
        assertEquals(expected, modelWriter.toString());
    }
    
    public void testMultilineCommentToString() {
    	JavaAnnotatedElement annotatedElement = mock(JavaAnnotatedElement.class);
    	when(annotatedElement.getComment()).thenReturn("Hello\nWorld");

        // expectation
        String expected = ""
                + "/**\n"
                + " * Hello\n"
                + " * World\n"
                + " */\n";

        // run
        modelWriter.commentHeader(annotatedElement);

        // verify
        assertEquals(expected, modelWriter.toString());
    	
    }

    public void testNoCommentToString() {
        // setup
    	JavaAnnotatedElement annotatedElement = mock(JavaAnnotatedElement.class);
    	when(annotatedElement.getComment()).thenReturn("");

        // expectation
        String expected = "";

        // run
        modelWriter.commentHeader(annotatedElement);

        // verify
        assertEquals(expected, modelWriter.toString());
    }

    public void testCommentWithTagToString() {
        // setup
    	JavaAnnotatedElement annotatedElement = mock(JavaAnnotatedElement.class);
    	when(annotatedElement.getComment()).thenReturn("Hello");
        List<DocletTag> tags = new LinkedList<DocletTag>();
        tags.add(new DefaultDocletTag("monkey", "is in the tree"));
    	when(annotatedElement.getTags()).thenReturn(tags);

        // expectation
        String expected = ""
                + "/**\n"
                + " * Hello\n"
                + " *\n"
                + " * @monkey is in the tree\n"
                + " */\n";

        // run
        modelWriter.commentHeader(annotatedElement);

        // verify
        assertEquals(expected, modelWriter.toString());
    }

    public void testCommentWithMultipleTagsToString() {
        // setup
    	JavaAnnotatedElement annotatedElement = mock(JavaAnnotatedElement.class);
    	when(annotatedElement.getComment()).thenReturn("Hello");
        List<DocletTag> tags = new LinkedList<DocletTag>();
        tags.add(new DefaultDocletTag("monkey", "is in the tree"));
        tags.add(new DefaultDocletTag("see", "the doctor"));
        when(annotatedElement.getTags()).thenReturn(tags);

        // expectation
        String expected = ""
                + "/**\n"
                + " * Hello\n"
                + " *\n"
                + " * @monkey is in the tree\n"
                + " * @see the doctor\n"
                + " */\n";

        // run
        modelWriter.commentHeader(annotatedElement);

        // verify
        assertEquals(expected, modelWriter.toString());
    }

    public void testTagButNoCommentToString() {
        // setup
    	JavaAnnotatedElement annotatedElement = mock(JavaAnnotatedElement.class);
        List<DocletTag> tags = new LinkedList<DocletTag>();
        tags.add(new DefaultDocletTag("monkey", "is in the tree"));
        when(annotatedElement.getTags()).thenReturn(tags);

        // expectation
        String expected = ""
                + "/**\n"
                + " * @monkey is in the tree\n"
                + " */\n";

        // run
        modelWriter.commentHeader(annotatedElement);

        // verify
        assertEquals(expected, modelWriter.toString());
    }

    public void testTagWithNoValueToString() {
        // setup
    	JavaAnnotatedElement annotatedElement = mock(JavaAnnotatedElement.class);
        List<DocletTag> tags = new LinkedList<DocletTag>();
        tags.add(new DefaultDocletTag("monkey", ""));
        when(annotatedElement.getTags()).thenReturn(tags);

        // expectation
        String expected = ""
                + "/**\n"
                + " * @monkey\n"
                + " */\n";

        // run
        modelWriter.commentHeader(annotatedElement);

        // verify
        assertEquals(expected, modelWriter.toString());
    }
}
