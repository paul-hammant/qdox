package com.thoughtworks.qdox.model.util;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is a simple Map implementation backed by a List of Map.Entry objects. 
 * It has the property that iterators return entries in the order in whick
 * they were inserted.
 * 
 * Operations involving searching, including get() and put(), have cost linear 
 * to the size of the map. In other words, avoid this implementation if your 
 * Map might get large.
 * 
 * If we could assume Java 1.4+, we'd just use java.util.LinkedHashMap 
 * instead of this class.  But we can't.
 * 
 * @author Mike Williams
 */
public class OrderedMap extends AbstractMap {

    private Set _entrySet = new OrderedSet();

    public Set entrySet() {
        return _entrySet;
    }

    public Object put(Object key, Object value) {
        Entry existingEntry = getEntryWithKey(key);
        if (existingEntry == null) {
            entrySet().add(new Entry(key, value));
            return null;
        }
        Object previousValue = existingEntry.getValue();
        existingEntry.setValue(value);
        return previousValue;
    }    

    private Entry getEntryWithKey(Object key) {
        Iterator i = entrySet().iterator();
        while (i.hasNext()) {
            Entry e = (Entry) i.next();
            if (eq(e.getKey(), key)) {
                return e;
            }
        }
        return null;
    }
    
    static class OrderedSet extends AbstractSet {

        private List _elementList = new LinkedList();

        public int size() {
            return _elementList.size();
        }

        public Iterator iterator() {
            return _elementList.iterator();
        }

        public boolean add(Object o) {
            _elementList.add(o);
            return true;
        }
        
    }

    static class Entry implements Map.Entry {
        
        Object _key;
        Object _value;

        public Entry(Object key, Object value) {
            _key = key;
            _value = value;
        }

        public Object getKey() {
            return _key;
        }

        public Object getValue() {
            return _value;
        }

        public Object setValue(Object value) {
            Object oldValue = _value;
            _value = value;
            return oldValue;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry) o;
            return eq(_key, e.getKey()) && eq(_value, e.getValue());
        }

        public int hashCode() {
            return ((_key == null) ? 0 : _key.hashCode()) ^
                   ((_value == null) ? 0 : _value.hashCode());
        }

        public String toString() {
            return _key + "=" + _value;
        }

    }

    private static boolean eq(Object o1, Object o2) {
        return (o1 == null ? o2 == null : o1.equals(o2));
    }
    
}