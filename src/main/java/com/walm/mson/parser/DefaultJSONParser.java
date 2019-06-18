package com.walm.mson.parser;

import com.walm.mson.JSONArray;
import com.walm.mson.JSONObject;
import com.walm.mson.exception.ParserException;

import java.io.IOException;

/**
 * <p>DefaultJSONParser</p>
 *
 * @author wangjn
 * @date 2019/6/12
 */
public class DefaultJSONParser {

    private JSONLexer lexer;

    public DefaultJSONParser(JSONLexer jsonLexer) {
        this.lexer = jsonLexer;
    }

    public Object nextValue() throws IOException {
        int ch = lexer.nextNonWhitespace();
        switch (ch) {
            case '{':
                JSONObject jsonObject = new JSONObject();
                for (;;) {
                    String key = nextValue().toString();
                    if (lexer.nextNonWhitespace() != ':') {
                        throw new ParserException("unExcepted symbol, should be :");
                    }
                    jsonObject.put(key, nextValue());
                    switch (lexer.nextNonWhitespace()) {
                        case ',':
                            break;
                        case '}':
                            return jsonObject;
                        default:
                            throw new ParserException("unExcepted symbol");
                    }
                }
            case '[':
                JSONArray jsonArray = new JSONArray();
                for (;;) {
                    jsonArray.add(nextValue());
                    switch (lexer.nextNonWhitespace()) {
                        case ',':
                            break;
                        case ']':
                            return jsonArray;
                        default:
                            throw new ParserException("unExcepted symbol");
                    }
                }
            case '"':
            case '\'':
                return lexer.nextString();
            case 'f':
                return Boolean.FALSE;
            case 't':
                return Boolean.TRUE;
            case '-':
                return lexer.nextNumber();
            case 'n':
                return null;
            default:
                if (lexer.isDigit((char)ch)) {
                    return lexer.nextNumber();
                }
                throw new ParserException();
        }
    }
}
