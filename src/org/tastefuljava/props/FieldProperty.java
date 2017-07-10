package org.tastefuljava.props;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FieldProperty implements Property {
    private static final Logger LOG
            = Logger.getLogger(FieldProperty.class.getName());

    private final Field field;

    FieldProperty(Field field) {
        this.field = field;
    }

    @Override
    public String getName() {
        return field.getName();
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }

    @Override
    public boolean canGet() {
        return true;
    }

    @Override
    public boolean canSet() {
        return true;
    }

    @Override
    public Object get(Object object) {
        try {
            return field.get(object);
        } catch (IllegalAccessException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    @Override
    public void set(Object object, Object value) {
        try {
            field.set(object, value);
        } catch (IllegalAccessException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new IllegalArgumentException(ex.getMessage());
        }
    }
}
