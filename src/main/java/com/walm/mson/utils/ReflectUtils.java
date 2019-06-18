package com.walm.mson.utils;

import java.lang.reflect.Type;

/**
 * <p>ReflectUtils</p>
 *
 * @author wangjn
 * @date 2019/6/14
 */
public class ReflectUtils {

    /**
     * reflect instantiate
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T instantiate(Class<T> clazz) {
        if (clazz.isInterface()) {
            throw new RuntimeException("Specified class is an interface");
        }
        try {
            return clazz.newInstance();
        }
        catch (InstantiationException ex) {
            throw new RuntimeException("Is it an abstract class?", ex);
        }
        catch (IllegalAccessException ex) {
            throw new RuntimeException("Is the constructor accessible?", ex);
        }
    }

    /**
     * isBasicType
     *
     * @param type
     * @return
     */
    public static boolean isBasicType(Type type) {

        return type.equals(String.class) || type.equals(Integer.class) ||
                type.equals(Long.class) || type.equals(Double.class) ||
                type.equals(Float.class) || type.equals(Short.class) ||
                type.equals(Boolean.class) || type.equals(Byte.class) ||
                type.equals(Character.class) || type.equals(int.class) ||
                type.equals(long.class) || type.equals(double.class) ||
                type.equals(float.class) || type.equals(short.class) ||
                type.equals(boolean.class) || type.equals(byte.class) ||
                type.equals(char.class);
    }
}
