package com.thoughtworks.qdox.directorywalker;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class DirectoryScannerTest
{

    private File newMockFile( String name )
    {
        return newMockFile( name, false );
    }

    private File newMockFile( String name, boolean isDirectory )
    {
        File result = mock( File.class );
        when( result.getName() ).thenReturn( name );
        when( result.isDirectory() ).thenReturn( isDirectory );
        return result;
    }

    @Test
    public void testSingleDirectory()
    {
        File rootDir = newMockFile( "root", true );
        {
            File blahTxt = newMockFile( "blah.txt" );
            File fooTxt = newMockFile( "foo.txt" );
            File pigJava = newMockFile( "pig.java" );

            when( rootDir.listFiles() ).thenReturn( new File[] { blahTxt, fooTxt, pigJava } );
        }

        DirectoryScanner scanner = new DirectoryScanner( rootDir );
        List<File> files = scanner.scan();
        assertEquals( 3, files.size() );
        assertEquals( "blah.txt", files.get( 0 ).getName() );
        assertEquals( "foo.txt", files.get( 1 ).getName() );
        assertEquals( "pig.java", files.get( 2 ).getName() );
    }

    @Test
    public void testDirectoryWithSubdir()
    {
        File rootDir = newMockFile( "root", true );
        {
            File subDir = newMockFile( "subdir", true );
            {
                File child1Txt = newMockFile( "child1.txt" );
                File child2Txt = newMockFile( "child2.txt" );

                when( subDir.listFiles() ).thenReturn( new File[] { child1Txt, child2Txt } );
            }
            File fooTxt = newMockFile( "foo.txt" );
            File pigJava = newMockFile( "pig.java" );

            when( rootDir.listFiles() ).thenReturn( new File[] { subDir, fooTxt, pigJava } );
        }

        DirectoryScanner scanner = new DirectoryScanner( rootDir );
        List<File> files = scanner.scan();
        assertEquals( 4, files.size() );
        assertEquals( "child1.txt", files.get( 0 ).getName() );
        assertEquals( "child2.txt", files.get( 1 ).getName() );
        assertEquals( "foo.txt", files.get( 2 ).getName() );
        assertEquals( "pig.java", files.get( 3 ).getName() );
    }

    @Test
    public void testDirectoryWithSubdirWithSubdir()
    {
        File rootDir = newMockFile( "root", true );
        {
            File subDir1 = newMockFile( "subdir", true );
            {
                File subDir2 = newMockFile( "subdir2", true );
                {
                    File grandChild1Txt = newMockFile( "grandChild1.txt" );

                    when( subDir2.listFiles() ).thenReturn( new File[] { grandChild1Txt } );
                }
                File child1Txt = newMockFile( "child1.txt" );
                File child2Txt = newMockFile( "child2.txt" );

                when( subDir1.listFiles() ).thenReturn( new File[] { subDir2, child1Txt, child2Txt } );

            }
            File fooTxt = newMockFile( "foo.txt" );
            File pigJava = newMockFile( "pig.java" );

            when( rootDir.listFiles() ).thenReturn( new File[] { subDir1, fooTxt, pigJava } );
        }

        DirectoryScanner scanner = new DirectoryScanner( rootDir );
        List<File> files = scanner.scan();
        assertEquals( 5, files.size() );
        assertEquals( "grandChild1.txt", files.get( 0 ).getName() );
        assertEquals( "child1.txt", files.get( 1 ).getName() );
        assertEquals( "child2.txt", files.get( 2 ).getName() );
        assertEquals( "foo.txt", files.get( 3 ).getName() );
        assertEquals( "pig.java", files.get( 4 ).getName() );
    }

    @Test
    public void testSuffixFilter()
    {
        File rootDir = newMockFile( "root", true );
        {
            File blahTxt = newMockFile( "blah.txt" );
            File fooJava = newMockFile( "foo.java" );
            File pigJava = newMockFile( "pig.java" );

            when( rootDir.listFiles() ).thenReturn( new File[] { blahTxt, fooJava, pigJava } );
        }
        DirectoryScanner scanner = new DirectoryScanner( rootDir );
        scanner.addFilter( new SuffixFilter( ".java" ) );
        List<File> files = scanner.scan();
        assertEquals( 2, files.size() );
        assertEquals( "foo.java", files.get( 0 ).getName() );
        assertEquals( "pig.java", files.get( 1 ).getName() );
    }

    @Test
    public void testFilterCallback()
    {
        File rootDir = newMockFile( "root", true );
        {
            File blahTxt = newMockFile( "blah.txt" );
            File fooJava = newMockFile( "foo.java" );
            File pigJava = newMockFile( "pig.java" );

            when( rootDir.listFiles() ).thenReturn( new File[] { blahTxt, fooJava, pigJava } );
        }
        DirectoryScanner scanner = new DirectoryScanner( rootDir );
        Filter mockFilter = new Filter()
        {
            public boolean filter( File file )
            {
                return file.getName().equals( "foo.java" );
            }
        };
        scanner.addFilter( mockFilter );
        List<File> files = scanner.scan();
        assertEquals( 1, files.size() );
        assertEquals( "foo.java", files.get( 0 ).getName() );
    }

    @Test
    public void testMultipleFilters()
    {
        File rootDir = newMockFile( "root", true );
        {
            File blahTxt = newMockFile( "blah.txt" );
            File fooJava = newMockFile( "foo.java" );
            File pigJava = newMockFile( "pig.java" );
            File fooTxt = newMockFile( "foo.txt" );

            when( rootDir.listFiles() ).thenReturn( new File[] { blahTxt, fooJava, pigJava, fooTxt } );
        }
        DirectoryScanner scanner = new DirectoryScanner( rootDir );
        scanner.addFilter( new SuffixFilter( ".java" ) );
        scanner.addFilter( new Filter()
        {
            public boolean filter( File file )
            {
                return file.getName().startsWith( "foo" );
            }
        } );
        List<File> files = scanner.scan();
        assertEquals( 1, files.size() );
        assertEquals( "foo.java", files.get( 0 ).getName() );
    }

    @Test
    public void testFileVisitor()
    {
        File rootDir = newMockFile( "root", true );

        File blahTxt = newMockFile( "blah.txt" );
        File fooJava = newMockFile( "foo.java" );
        File pigJava = newMockFile( "pig.java" );

        when( rootDir.listFiles() ).thenReturn( new File[] { blahTxt, fooJava, pigJava } );

        DirectoryScanner scanner = new DirectoryScanner( rootDir );
        FileVisitor visitor = mock( FileVisitor.class );
        scanner.scan( visitor );

        verify( visitor ).visitFile( blahTxt );
        verify( visitor ).visitFile( fooJava );
        verify( visitor ).visitFile( pigJava );
    }
}
