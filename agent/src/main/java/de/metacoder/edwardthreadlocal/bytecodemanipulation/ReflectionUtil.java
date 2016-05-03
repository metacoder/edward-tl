package de.metacoder.edwardthreadlocal.bytecodemanipulation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtil {

    public static Object invokeMethod(Object o, String fieldName){
        try {
            final Method m = o.getClass().getMethod(fieldName);
            return m.invoke(o);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("Reflection error!", e);
        }
    }
}

