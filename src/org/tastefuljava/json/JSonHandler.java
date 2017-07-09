package org.tastefuljava.json;

public interface JSonHandler {
    void startObject();
    void endObject();
    void startField(String name);
    void endField(String name);
    void startArray();
    void endArray();
    void startElement();
    void endElement();
    void handleNull();
    void handleBoolean(boolean value);
    void handleNumber(double value);
    void handleString(String value);
}
