package com.thoughtworks.qdox.directorywalker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class SuffixFilterTest
{

    @Test
    public void testNullFile()
    {
        try
        {
            new SuffixFilter( ".java" ).filter( null );
            Assertions.fail("Can't filter null");
        }
        catch ( NullPointerException e )
        {
        }
    }

    @Test
    public void testNullSuffix()
    {
        try
        {
            new SuffixFilter( null ).filter( new File( "test.java" ) );
            Assertions.fail("Can't filter without a suffix");
        }
        catch ( NullPointerException e )
        {
        }
    }
    
    @Test
    public void testEmptySuffix()
    {
      Assertions.assertTrue(new SuffixFilter( "" ).filter( new File("test.java") ));
    }
    
    @Test
    public void testMatchingSuffix()
    {
      Assertions.assertTrue(new SuffixFilter( ".java" ).filter( new File("test.java") ));
    }

    @Test
    public void testNonMatchingSuffix()
    {
      Assertions.assertFalse(new SuffixFilter( "test" ).filter( new File("test.java") ));
    }

}
