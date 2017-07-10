package org.tastefuljava.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InvocationLogger implements InvocationHandler {
    private static final Logger LOG
            = Logger.getLogger(InvocationLogger.class.getName());

    private final Level level;
    private final Object delegate;

    public static <T> T wrap(Level level, T object, Class<T> intf,
            Class<?>... intfs) {
        if (!LOG.isLoggable(level)) {
            return object;
        }
        Class<?>[] allIntfs = new Class[intfs.length+1];
        allIntfs[0] = intf;
        for (int i = 0; i < intfs.length; ++i) {
            allIntfs[i+1] = intfs[i];
        }
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Object proxy = Proxy.newProxyInstance(cl, allIntfs,
                        new InvocationLogger(level, object));
        return intf.cast(proxy);
    }

    private InvocationLogger(Level level, Object object) {
        this.level = level;
        this.delegate = object;
    }

    @Override
    public Object invoke(Object proxy, Method method,
            Object[] args) throws Throwable {
        String signature = signature(method, args);
        LOG.log(level, "start {0}", signature);
        try {
            return method.invoke(delegate, args);
        } finally {
            LOG.log(level, "end {0}", method.getName());
        }
    }

    private String signature(Method method, Object[] args) {
        StringBuilder buf = new StringBuilder(method.getName());
        buf.append('(');
        if (args != null && args.length > 0) {
            buf.append(args[0]);
            for (int i = 1; i < args.length; ++i) {
                buf.append(',');
                buf.append(args[i]);
            }
        }
        buf.append(')');
        return buf.toString();
    }                    
}
