package org.tastefuljava.json;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tastefuljava.props.Properties;
import org.tastefuljava.props.Property;
import org.tastefuljava.util.InvocationLogger;

public class JSon {
    private static final Logger LOG = Logger.getLogger(JSon.class.getName());

    public static <T> T parse(String json, Class<T> clazz) throws IOException {
        return parse(new StringReader(json), clazz);
    }

    public static <T> T parse(Reader in, Class<T> clazz)
            throws IOException {
        Handler handler = new Handler(clazz);
        JSonHandler jsonHandler = InvocationLogger.wrap(
                Level.INFO, handler, JSonHandler.class);
        JSonParser.parse(in, jsonHandler);
        return clazz.cast(handler.top);
    }

    public static void write(Object object, PrintWriter out, boolean format) {
        try (JSonFormatter fmt = new JSonFormatter(out, format)) {
            visit(object, fmt);
        }
    }

    public static void write(Object object, Writer writer, boolean format) {
        if (writer instanceof PrintWriter) {
            write(object, (PrintWriter)writer, format);
        } else {
            try (PrintWriter out = new PrintWriter(writer)) {
                write(object, out, format);
            }
        }
    }

    public static String stringify(Object object, boolean format) {
        StringWriter writer = new StringWriter();
        write(object, writer, format);
        return writer.toString();
    }

    public static void visit(Object object, JSonHandler handler) {
        if (object == null) {
            handler.handleNull();
        } else if (object instanceof Boolean) {
            handler.handleBoolean((Boolean)object);
        } else if (object instanceof Number) {
            handler.handleNumber(((Number)object).doubleValue());
        } else if (object instanceof String) {
            handler.handleString((String)object);
        } else if (object instanceof Date) {
            handler.handleString(JSonDates.format((Date)object));
        } else {
            Class<?> clazz = object.getClass();
            if (clazz.isArray()) {
                handler.startArray();
                int length = Array.getLength(object);
                for (int i = 0; i < length; ++i) {
                    handler.startElement();
                    visit(Array.get(object, i), handler);
                    handler.endElement();
                }
                handler.endArray();
            } else {
                Map<String,Property> props = Properties.classProperties(clazz);
                handler.startObject();
                for (Property prop: props.values()) {
                    Object value = prop.get(object);
                    if (value != null) {
                        handler.startField(prop.getName());
                        visit(value, handler);
                        handler.endField(prop.getName());
                    }
                }
                handler.endObject();
            }
        }
    }

    private static class Handler implements JSonHandler {
        private Object top = null;
        private final List<Object> stack = new ArrayList<>();
        private Class<?> clazz;
        private final List<Class<?>> classStack = new ArrayList<>();

        private Handler(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public void startObject() {
            try {
                stack.add(0, clazz.newInstance());
            } catch (InstantiationException | IllegalAccessException ex) {
                LOG.log(Level.SEVERE, "Error instanciating object", ex);
                throw new RuntimeException(ex.getMessage());
            }
        }

        @Override
        public void endObject() {
            top = stack.remove(0);
        }

        @Override
        public void startField(String name) {
            classStack.add(0, clazz);
            Object object = stack.get(0);
            Map<String, Property> props
                    = Properties.classProperties(object.getClass());
            Property prop = props.get(name);
            clazz = prop == null ? Object.class : prop.getType();
        }

        @Override
        public void endField(String name) {
            clazz = classStack.remove(0);
            Object object = stack.get(0);
            Map<String, Property> props
                    = Properties.classProperties(object.getClass());
            Property prop = props.get(name);
            if (prop != null && prop.canSet()) {
                prop.set(object, convert(top, prop.getType()));
            }
        }

        @Override
        public void startArray() {
            stack.add(0, new ArrayList<>());
        }

        @Override
        public void endArray() {
            top = stack.remove(0);
            if (clazz.isArray()) {
                @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>)top;
                int length = list.size();
                top = list.toArray((Object[])Array.newInstance(
                        clazz.getComponentType(), length));
            }
        }

        @Override
        public void startElement() {
            classStack.add(0, clazz);
            if (clazz.isArray()) {
                clazz = clazz.getComponentType();
            } else if (Collection.class.isAssignableFrom(clazz)) {
                clazz = Object.class;
            }
        }

        @Override
        public void endElement() {
            clazz = classStack.remove(0);
            @SuppressWarnings("unchecked")
            List<Object> array = (List<Object>)stack.get(0);
            array.add(top);
        }

        @Override
        public void handleNull() {
            top = null;
        }

        @Override
        public void handleBoolean(boolean value) {
            top = value;
        }

        @Override
        public void handleNumber(double value) {
            top = value;
        }

        @Override
        public void handleString(String value) {
            top = value;
        }

        private static Object convert(Object value, Class<?> type) {
            if (value == null) {
                return null;
            } else if (type.isAssignableFrom(value.getClass())) {
                return type.cast(value);
            } else if ((type == boolean.class || type == Boolean.class)
                    && value.getClass() == Boolean.class) {
                return value;
            } else if (value instanceof Number) {
                Number number = (Number)value;
                if (type == byte.class || type == Byte.class) {
                    return number.byteValue();
                } else if (type == short.class || type == Short.class) {
                    return number.shortValue();
                } else if (type == int.class || type == Integer.class) {
                    return number.intValue();
                } else if (type == long.class || type == Long.class) {
                    return number.longValue();
                } else if (type == float.class || type == Float.class) {
                    return number.floatValue();
                } else if (type == double.class || type == Double.class) {
                    return number.doubleValue();
                } else if (type == BigDecimal.class) {
                    return BigDecimal.valueOf(number.doubleValue());
                } else {
                    throw new RuntimeException("Cannot convert value of type "
                            + type);
                }
            } else if (type == Date.class && value instanceof String) {
                return JSonDates.parse((String)value);
            } else if (Enum.class.isAssignableFrom(type)
                    && value instanceof String) {
                @SuppressWarnings("unchecked")
                Object result = Enum.valueOf((Class<? extends Enum>)type,
                        (String)value);
                return result;
            } else {
                throw new RuntimeException("Cannot convert value of type "
                        + type);
            }
        }
    }
}
