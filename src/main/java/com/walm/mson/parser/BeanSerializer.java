package com.walm.mson.parser;

import com.walm.mson.JSONArray;
import com.walm.mson.JSONObject;
import com.walm.mson.utils.ReflectUtils;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>BeanSerializer</p>
 * <p>use to serializer jsonObject or jsonArray</p>
 *
 * @author wangjn
 * @date 2019/6/14
 */
public class BeanSerializer {

    /**
     * castToObject
     *
     * @param clazz
     * @param object
     * @param <T>
     * @return
     */
    public static <T> T castToObject(Class<T> clazz, Object object) {
        if (object == null) {
            return null;
        }

        if (ReflectUtils.isBasicType(clazz)) {
            // TODO 需要做基本类型转换
            return (T)object;
        } else if (object instanceof JSONObject) {
            // 解析 JSONObject
            return castMap2Object(clazz, (Map<String, Object>) object);
        }
        throw new RuntimeException("only deserialize map or list !");
    }

    /**
     * castToList
     *
     * @param clazz
     * @param object
     * @param <T>
     * @return
     */
    public static <T> List<T> castToList(Class<T> clazz, Object object) {
        if (object == null) {
            return null;
        }
        // 解析 JSONArray
        if (object instanceof JSONArray) {
            List<T> list = new ArrayList<>();
            for (Object obj : (JSONArray) object) {
                list.add(castToObject(clazz, obj));
            }
            return list;
        }
        throw new RuntimeException("only deserialize map or list !");
    }

    /**
     * array to jsonArray
     *
     * @param objects
     * @return
     */
    public static String toJson(Object [] objects) {
        StringBuilder jsonStr = new StringBuilder();
        jsonStr.append('[');
        for (Object obj : objects) {
            String objJson = toJson(obj);
            jsonStr.append(objJson + ",");
        }
        int index = jsonStr.lastIndexOf(",");
        if (index > -1) {
            jsonStr.substring(index);
        }
        jsonStr.append(']');
        return jsonStr.toString();
    }

    /**
     * object to jsonString
     *
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        StringBuilder jsonStr = new StringBuilder();
        Class clazz = object.getClass();
        if (object instanceof List) {
            jsonStr.append('[');
            List<Object> list = (List<Object>) object;
            for (Object obj : list) {
                String objJson = toJson(obj);
                jsonStr.append(objJson + ",");
            }
            int index = jsonStr.lastIndexOf(",");
            if (index > -1) {
                jsonStr.deleteCharAt(index);
            }
            jsonStr.append(']');
        } else {
            jsonStr.append('{');
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                String methodName = method.getName().toLowerCase();
                if (!methodName.startsWith("get") || methodName.startsWith("getclass")) {
                    continue;
                }
                Class returnType = method.getReturnType();
                String key = method.getName().toLowerCase().substring(3);
                jsonStr.append("\"" + key + "\"" + ":");
                try {
                    Object result = method.invoke(object);
                    if (ReflectUtils.isBasicType(returnType)) {
                        if (result.getClass().equals(String.class)) {
                            jsonStr.append("\"" + result + "\"" + ",");
                        } else {
                            jsonStr.append(result + ",");
                        }
                    } else {
                        String json = toJson(result);
                        jsonStr.append(json + ",");
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            int index = jsonStr.lastIndexOf(",");
            if (index > -1) {
                jsonStr.deleteCharAt(index);
            }
            jsonStr.append('}');
        }
        return jsonStr.toString();
    }


    private static <T> T[] cast2Array(Class<T> clazz, List<Object> list) {
        T[] collection = (T[]) Array.newInstance(clazz, list.size());
        for (int i = 0; i < list.size(); ++i) {
            collection[i] = castToObject(clazz, list.get(i));
        }
        return collection;
    }


    private static <T> T castMap2Object(Class<T> clazz, Map<String, Object> map) {
        Method[] methods = clazz.getMethods();
        T t = ReflectUtils.instantiate(clazz);

        // clazz是Map或则子类
        if (Map.class.isAssignableFrom(clazz)) {
            return (T)map;
        }

        for (Method method : methods) {
            String methodName = method.getName().toLowerCase();
            if (!methodName.startsWith("set")) {
                continue;
            }
            for (Map.Entry entry : map.entrySet()) {
                String key = (String) entry.getKey();
                String ketMethodName = "set" + key;
                Object valueObj = entry.getValue();
                if (ketMethodName.toLowerCase().equals(methodName)) {
                    Type[] types = method.getGenericParameterTypes();
                    Type type = types[0];
                    try {
                        if (type instanceof ParameterizedType) {
                            // 解析泛型
                            Type[] parameterTypes = ((ParameterizedType) type).getActualTypeArguments();
                            Type valueType = parameterTypes[0];
                            Class rawType = (Class)((ParameterizedType) type).getRawType();
                            if (List.class.isAssignableFrom(rawType)) {
                                method.invoke(t, castToList((Class) valueType, entry.getValue()));
                                break;
                            }
                            throw new RuntimeException("Generics other than List cannot be resolved");
                        } else if (type instanceof Class) {
                            // 数组
                            if (((Class) type).isArray() && List.class.isAssignableFrom(entry.getValue().getClass())) {
                                method.invoke(t, cast2Array(clazz, (List)entry.getValue()));
                                break;
                            } else {
                                method.invoke(t, castToObject((Class) type, entry.getValue()));
                                break;
                            }
                        } else if (type instanceof GenericArrayType) {
                            // 解析泛型数组类型
                            if (valueObj instanceof List) {
                                Type parameterType = ((GenericArrayType) type).getGenericComponentType();
                                method.invoke(t, cast2Array((Class)parameterType, (List)valueObj));
                                break;
                            }
                        } else {
                            throw new RuntimeException("unKnown type");
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
            }
        }
        return t;
    }
}
