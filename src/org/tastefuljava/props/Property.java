package org.tastefuljava.props;

public interface Property<O,P> {
    public P get(O object);
    void set(O object, P value);
}
