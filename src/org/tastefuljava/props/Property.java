package org.tastefuljava.props;

public interface Property {
    public String getName();
    public Class<?> getType();
    public boolean canGet();
    public boolean canSet();
    public Object get(Object object);
    public void set(Object object, Object value);
}
