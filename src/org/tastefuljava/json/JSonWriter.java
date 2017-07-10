package org.tastefuljava.json;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import org.tastefuljava.props.Properties;
import org.tastefuljava.props.Property;

public class JSonWriter {
    private final JSonHandler handler;

    public JSonWriter(JSonHandler handler) {
        this.handler = handler;
    }

    public void printValue(Object value) {
        if (value == null) {
            handler.handleNull();
        } else if (value instanceof Boolean) {
            handler.handleBoolean((Boolean)value);
        } else if (value instanceof Number) {
            handler.handleNumber(((Number)value).doubleValue());
        } else if (value instanceof String) {
            handler.handleString((String)value);
        } else if (value instanceof Date) {
            handleDate((Date)value);
        } else if (value instanceof Enum<?>) {
            Enum<?> enm = (Enum<?>)value;
            handler.handleString(enm.name());
        } else {
            Class<?> clazz = value.getClass();
            if (clazz.isArray()) {
                printArray(value);
            } else {
                printObject(value);
            }
        }
    }

    public void printArray(Object value) {
        handler.startArray();
        try {
            int length = Array.getLength(value);
            for (int i = 0; i < length; ++i) {
                Object element = Array.get(value, i);
                handler.startElement();
                try {
                    printValue(element);
                } finally {
                    handler.endElement();
                }
            }
        } finally {
            handler.endArray();
        }
    }

    public void printObject(Object value) {
        handler.startObject();
        try {
            Map<String,Property> props = Properties.classProperties(
                    value.getClass());
            for (Property prop: props.values()) {
                if (prop.canGet()) {
                    
                }
            }
            for (Method method: value.getClass().getMethods()) {
                if (method.getDeclaringClass() == Object.class) {
                    // skip
                } else if (method.getParameterTypes().length > 0) {
                    // skip
                } else {
                    String name = method.getName();
                    String propName = null;
                    if (name.startsWith("get") && name.length() > 3) {
                        propName = Character.toLowerCase(name.charAt(3))
                                + name.substring(4);
                    } else if (name.startsWith("is") && name.length() > 2) {
                        Class<?> retType = method.getReturnType();
                        if (retType == boolean.class
                                || retType == Boolean.class) {
                            propName = Character.toLowerCase(name.charAt(3))
                                    + name.substring(4);
                        }
                    }
                    if (propName != null) {
                        try {
                            printField(propName, method.invoke(value));
                        } catch (IllegalAccessException
                                | IllegalArgumentException
                                | InvocationTargetException ex) {
                            throw new RuntimeException(
                                    "Error getting value of " + propName, ex);
                        }
                    }
                }
            }
        } finally {
            handler.endObject();
        }
    }

    public void printObject(Object value, String... propNames) {
        handler.startObject();
        try {
            printProps(value, propNames);
        } finally {
            handler.endObject();
        }
    }

    private void printProps(Object value, String... propNames) {
        Class<?> clazz = value.getClass();
        for (String propName: propNames) {
            try {
                String getterName = "get"
                        + Character.toUpperCase(propName.charAt(0))
                        + propName.substring(1);
                Method getter = clazz.getMethod(getterName);
                if (getter == null) {
                    getterName = "is"
                        + Character.toUpperCase(propName.charAt(0))
                        + propName.substring(1);
                    Method g = clazz.getMethod(getterName);
                    getter = g;
                    Class<?> retType = getter.getReturnType();
                    if (retType != boolean.class
                            && retType != Boolean.class) {
                        getter = null;
                    }
                }
                if (getter != null) {
                    printField(propName, getter.invoke(value));
                }
            } catch (NoSuchMethodException | SecurityException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex) {
                throw new RuntimeException(
                        "Error getting value of " + propName, ex);
            }
        }
    }

    public void printField(String name, Object value) {
        handler.startField(name);
        try {
            printValue(value);
        } finally {
            handler.endField(name);
        }
    }

    public void startArrayField(String name) {
        handler.startField(name);
        handler.startArray();
    }

    public void endArrayField(String name) {
        handler.endArray();
        handler.endField(name);
    }

    public void startObjectField(String name) {
        handler.startField(name);
        handler.startObject();
    }

    public void endObjectField(String name) {
        handler.endObject();
        handler.endField(name);
    }

    private void handleDate(Date date) {
        String s = JSonDates.format(date);
        handler.handleString(s);
    }
}
