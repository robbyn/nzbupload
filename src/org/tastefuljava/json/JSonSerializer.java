package org.tastefuljava.json;

public interface JSonSerializer<T> {
    public void serialize(T obj, JSonWriter out);
}
