package org.tastefuljava.json;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JSon {
    private static final Logger LOG = Logger.getLogger(JSon.class.getName());


    public static <T> T parse(Reader in, Class<T> clazz)
            throws IOException {
        Handler handler = new Handler(clazz);
        JSonHandler jsonHandler = LOG.isLoggable(Level.FINE)
                ? createLogger(JSonHandler.class, handler)
                : handler;
        JSonParser.parse(in, jsonHandler);
        return clazz.cast(handler.top);
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
                handler.startObject();
                for (Class<?> cl = clazz; cl != Object.class;
                        cl = cl.getSuperclass()) {
                    for (Field field: cl.getDeclaredFields()) {
                        int mods = field.getModifiers();
                        if (!Modifier.isStatic(mods)
                                && !Modifier.isTransient(mods)
                                && !field.getName().startsWith("this$")
                                && !field.getName().startsWith("val$")) {
                            try {
                                field.setAccessible(true);
                                Object value = field.get(object);
                                if (value != null) {
                                    handler.startField(field.getName());
                                    visit(value, handler);
                                    handler.endField(field.getName());
                                }
                            } catch (IllegalArgumentException
                                    | IllegalAccessException ex) {
                                LOG.log(Level.SEVERE, "Cannot access field "
                                        + field.getName(), ex);
                            }
                        }
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
            Field field = accessField(object.getClass(), name);
            clazz = field == null ? Object.class : field.getType();
        }

        @Override
        public void endField(String name) {
            try {
                clazz = classStack.remove(0);
                Object object = stack.get(0);
                Field field = accessField(clazz, name);
                if (field != null) {
                    field.set(object, convert(top, field.getType()));
                }
            } catch (IllegalAccessException | IllegalArgumentException ex) {
                LOG.log(Level.SEVERE, "Error setting field value", ex);
                throw new RuntimeException(ex.getMessage());
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

        private static Field accessField(Class<?> cl, String name) {
            try {
                while (cl != Object.class) {
                    try {
                        Field field = cl.getDeclaredField(name);
                        field.setAccessible(true);
                        return field;
                    } catch (NoSuchFieldException ex) {
                        // ignore
                    }
                    cl = cl.getSuperclass();
                }
                return null;
            } catch (SecurityException ex) {
                LOG.log(Level.SEVERE, "Error accessing field", ex);
                throw new RuntimeException(ex.getMessage());
            }
        }

        private static Object convert(Object value, Class<?> type) {
            if (value == null) {
                return null;
            } else if (type.isAssignableFrom(value.getClass())) {
                return value;
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

    private static <T> T createLogger(Class<T> intf, final T delegate) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Object proxy = Proxy.newProxyInstance(cl, new Class<?>[] {intf},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method,
                            Object[] args) throws Throwable {
                        StringBuilder buf = new StringBuilder(method.getName());
                        buf.append('(');
                        if (args != null) {
                            boolean first = true;
                            for (Object arg: args) {
                                if (first) {
                                    first = false;
                                } else {
                                    buf.append(',');
                                }
                                buf.append(arg);
                            }
                        }
                        buf.append(')');
                        LOG.info(buf.toString());
                        return method.invoke(delegate, args);
                    }                    
                });
        return intf.cast(proxy);
    }
}
