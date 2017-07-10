package org.tastefuljava.props;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Properties {
    private static final Logger LOG
            = Logger.getLogger(FieldProperty.class.getName());
    private static final Map<Class<?>,Map<String,Property>> CLASS_PROPERTIES
            = new HashMap<>();

    private Properties() {
    }

    public static Map<String,Property> classProperties(Class<?> clazz) {
        Map<String,Property> props = CLASS_PROPERTIES.get(clazz);
        if (props == null) {
            props = new LinkedHashMap<>();
            extractProperties(clazz, props);
            CLASS_PROPERTIES.put(clazz, props);
        }
        return props;
    }

    private static void extractProperties(Class<?> clazz,
            Map<String, Property> props) {
        long tm = System.nanoTime();
        extractFieldProps(clazz, props);
        extractMethodProps(clazz, props);
        tm = System.nanoTime()-tm;
        LOG.log(Level.INFO, "Properties extracted in {0}ns", tm);
    }

    private static void extractFieldProps(Class<?> clazz,
            Map<String, Property> props) {
        for (Class<?> cl = clazz; cl != Object.class;
                cl = cl.getSuperclass()) {
            for (Field field: cl.getDeclaredFields()) {
                String name = field.getName();
                int mods = field.getModifiers();
                if (!props.containsKey(name)
                        && !Modifier.isStatic(mods)
                        && !Modifier.isTransient(mods)
                        && !field.getName().startsWith("this$")
                        && !field.getName().startsWith("val$")) {
                    field.setAccessible(true);
                    props.put(name, new FieldProperty(field));
                }
            }
        }
    }

    private static void extractMethodProps(Class<?> cl, Map<String,
            Property> props) {
        Map<String,Method> getters = extractGetters(cl);
        Map<String,Method> setters = extractSetters(cl);
        for (Map.Entry<String,Method> e: getters.entrySet()) {
            String name = e.getKey();
            Method getter = e.getValue();
            Method setter = setters.get(name);
            props.put(name, new MethodProperty(
                    name, getter.getReturnType(), getter, setter));
        }
        for (Map.Entry<String,Method> e: setters.entrySet()) {
            String name = e.getKey();
            if (!getters.containsKey(name)) {
                Method setter = e.getValue();
                props.put(name, new MethodProperty(
                        name, setter.getParameterTypes()[0], null, setter));
            }
        }
    }

    private static Map<String, Method> extractGetters(Class<?> cl) {
        Map<String, Method> getters = new LinkedHashMap<>();
        for (Method method: cl.getMethods()) {
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
                if (propName != null && !getters.containsKey(propName)) {
                    getters.put(propName, method);
                }
            }
        }
        return getters;
    }

    private static Map<String, Method> extractSetters(Class<?> cl) {
        Map<String, Method> setters = new LinkedHashMap<>();
        for (Method method: cl.getMethods()) {
            if (method.getDeclaringClass() == Object.class) {
                // skip
            } else if (method.getParameterTypes().length != 1) {
                // skip
            } else {
                String name = method.getName();
                String propName = null;
                if (name.startsWith("set") && name.length() > 3) {
                    propName = Character.toLowerCase(name.charAt(3))
                            + name.substring(4);
                }
                if (propName != null && !setters.containsKey(propName)) {
                    setters.put(propName, method);
                }
            }
        }
        return setters;
    }
}
