package org.tastefuljava.props;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MethodProperty extends Property {
    private static final Logger LOG
            = Logger.getLogger(MethodProperty.class.getName());

    private final String name;
    private final Method getter;
    private final Method setter;

    MethodProperty(String name, Class<?> type, Method getter, Method setter) {
        super(name, type);
        if (getter == null && setter == null) {
            throw new IllegalArgumentException(
                    "Getter and setter cannot both be null.");
        }
        this.name = name;
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public boolean canGet() {
        return getter != null;
    }

    @Override
    public boolean canSet() {
        return setter != null;
    }

    @Override
    public Object get(Object object) {
        if (getter == null) {
            throw new UnsupportedOperationException("No getter for property");
        }
        try {
            return getter.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    @Override
    public void set(Object object, Object value) {
        if (setter == null) {
            throw new UnsupportedOperationException("No setter for property");
        }
        try {
            setter.invoke(object, value);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new IllegalArgumentException(ex.getMessage());
        }
    }
}
