package com.walm;

import com.walm.mson.JSON;
import com.walm.mson.parser.BeanSerializer;
import com.walm.mson.parser.DefaultJSONParser;
import com.walm.mson.parser.JSONLexer;

import java.io.IOException;
import java.util.List;

/**
 * <p>mson</p>
 *
 * @author wangjn
 * @date 2019/6/18
 */
public class MsonTest {

    public static void main(String[] args) throws IOException {
        String text = "[{\"name\":\"小林\",\"age\":90}]";
        System.out.println(text);
        Object obje = new DefaultJSONParser(new JSONLexer(text)).nextValue();
        List<TestBO> list = BeanSerializer.castToList(TestBO.class, obje);
        System.out.println(BeanSerializer.toJson(list));
    }


    public static class TestBO {
        private String name;
        private Long age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getAge() {
            return age;
        }

        public void setAge(Long age) {
            this.age = age;
        }
    }
}
