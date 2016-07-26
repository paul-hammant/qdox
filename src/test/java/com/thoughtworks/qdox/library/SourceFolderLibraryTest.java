package com.thoughtworks.qdox.library;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class SourceFolderLibraryTest
{
    private SourceFolderLibrary library = new SourceFolderLibrary( null );

    @Test
    public void testModuleInfo()
    {
        library.addSourceFolder( new File("src/test/resources/jigsaw") );
        assertEquals( "com.foo.bar", library.getJavaModule().getName() );
    }

}
