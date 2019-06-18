package com.walm.mson;


import com.walm.mson.parser.BeanSerializer;
import com.walm.mson.parser.DefaultJSONParser;
import com.walm.mson.parser.JSONLexer;

import java.io.IOException;
import java.util.List;

/**
 * <p>JSON</p>
 *
 * @author wangjn
 * @date 2019/6/11
 */
public class JSON {


    /**
     * getObject fromJson
     *
     * @param clazz
     * @param text
     * @param <T>
     * @return
     */
    public static <T> T parse(Class<T> clazz, String text) {
        try {
            Object object = new DefaultJSONParser(new JSONLexer(text)).nextValue();
            return BeanSerializer.castToObject(clazz, object);
        } catch (IOException e) {
        }
        return null;
    }

    /**
     * getListObject fromJson
     *
     * @param clazz
     * @param text
     * @param <T>
     * @return
     */
    public static <T> List<T> parseArray(Class<T> clazz, String text) {
        try {
            Object object = new DefaultJSONParser(new JSONLexer(text)).nextValue();
            return BeanSerializer.castToList(clazz, object);
        } catch (IOException e) {
        }
        return null;
    }

    /**
     * cast obj toJSONString
     *
     * @param object
     * @return
     */
    public static String toJSONString(Object object) {
        return BeanSerializer.toJson(object);
    }
}
