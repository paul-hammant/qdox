package com.thoughtworks.qdox.directorywalker;

import java.io.File;

import org.junit.Test;
import static org.junit.Assert.*;

public class SuffixFilterTest
{

    @Test
    public void testNullFile()
    {
        try
        {
            new SuffixFilter( ".java" ).filter( null );
            fail( "Can't filter null" );
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
            fail( "Can't filter without a suffix" );
        }
        catch ( NullPointerException e )
        {
        }
    }
    
    @Test
    public void testEmptySuffix()
    {
      assertTrue( new SuffixFilter( "" ).filter( new File("test.java") ) );    
    }
    
    @Test
    public void testMatchingSuffix()
    {
      assertTrue( new SuffixFilter( ".java" ).filter( new File("test.java") ) );    
    }

    @Test
    public void testNonMatchingSuffix()
    {
      assertFalse( new SuffixFilter( "test" ).filter( new File("test.java") ) );    
    }

}
