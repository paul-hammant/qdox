package com.thoughtworks.qdox.model.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializationUtils {

    public static byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(buffer);
            oos.writeObject(obj);
            oos.close();
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("error writing to byte-array!", e);
        }
    }

    public static Object deserialize(byte[] bytes)
            throws ClassNotFoundException {
        try {
            ByteArrayInputStream input = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(input);
            return ois.readObject();
        } catch (IOException e) {
            throw new RuntimeException("error reading from byte-array!", e);
        }
    }

    public static Object serializedCopy(Object obj) {
        try {
            return deserialize(serialize(obj));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("this shouldn't happen");
        }
    }

}