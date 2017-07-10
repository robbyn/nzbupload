package org.tastefuljava.props;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FieldProperty extends Property {
    private static final Logger LOG
            = Logger.getLogger(FieldProperty.class.getName());

    private final Field field;

    FieldProperty(Field field) {
        super(field.getName(), field.getType());
        this.field = field;
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
