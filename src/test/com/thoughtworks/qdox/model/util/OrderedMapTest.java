package com.thoughtworks.qdox.model.util;

import java.util.Iterator;
import junit.framework.TestCase;

public class OrderedMapTest extends TestCase {

    OrderedMap orderedMap = new OrderedMap();
    
    public void testEmptyMap() {
        assertEquals(0, orderedMap.size());
        assertEquals(0, orderedMap.entrySet().size());
        assertEquals(false, orderedMap.entrySet().iterator().hasNext());
    }
    
    public void testAddOneEntry() {
        orderedMap.put("foo", "bar");
        assertEquals(1, orderedMap.size());
        assertEquals(1, orderedMap.entrySet().size());
        assertEquals(true, orderedMap.entrySet().iterator().hasNext());
        assertEquals("bar", orderedMap.get("foo"));
        assertTrue(orderedMap.keySet().contains("foo"));
        assertTrue(orderedMap.values().contains("bar"));
    }
    
    public void testAddTwoEntries() {
        orderedMap.put("foo", "bar");
        orderedMap.put("bork", "flarg");
        assertEquals(2, orderedMap.size());
        assertEquals(2, orderedMap.entrySet().size());
        assertEquals("bar", orderedMap.get("foo"));
        assertEquals("flarg", orderedMap.get("bork"));
    }

    public void testEntryIsReplacedWhenKeysCollide() {
        orderedMap.put("tweedle", "dum");
        assertEquals("dum",orderedMap.put("tweedle", "dee"));
        assertEquals("dee", orderedMap.get("tweedle"));
        assertEquals(1, orderedMap.size());
    }

    public void testEntriesAreReturnedInOrderAdded() {
        orderedMap.put("juan", "x");
        orderedMap.put("twoo", "x");
        orderedMap.put("juan", "x");
        orderedMap.put("free", "x");
        Iterator keyIterator = orderedMap.keySet().iterator();
        assertEquals("juan", keyIterator.next());
        assertEquals("twoo", keyIterator.next());
        assertEquals("free", keyIterator.next());
        assertEquals(false, keyIterator.hasNext());
    }
    
}
