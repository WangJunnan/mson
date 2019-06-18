package com.walm.mson.parser;

import com.walm.mson.exception.ParserException;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * <p>JSONLexer</p>
 *
 * @author wangjn
 * @date 2019/6/12
 */
public class JSONLexer {

    /**
     * 要解析的 json char数组
     */
    private char[] buffer;

    /**
     * 当前下标 pos
     */
    private int pos;

    /**
     * 限制位下标
     */
    private int limit;

    private char current;

    public JSONLexer(String text) {
        buffer = text.toCharArray();
        limit = buffer.length;
        pos = 0;
    }

    /**
     * 返回当前下标 pos 对应的值
     */
    public int nextNonWhitespace() throws IOException {
        while (hasNext()) {
            current = buffer[pos++];
            if (current != ' ')
                return current;
        }
        return -1;
    }

    public int nextChar() {
        current = buffer[pos++];
        return current;
    }

    public int backChar() {
        current = buffer[--pos];
        return current;
    }

    /**
     * 获取下一个 String
     *
     * @return
     * @throws IOException
     */
    public String nextString() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (;;) {
            if (hasNext()) {
                int ch = nextChar();
                switch (ch) {
                    case '\n':
                    case '\r':
                        throw new ParserException();
                    case '"':
                    case '\'':
                        return sb.toString();
                    case '\\':
                        int escape = nextChar();
                        switch (escape) {
                            case 'b':
                                sb.append('\b');
                                break;
                            case 't':
                                sb.append('\t');
                                break;
                            case 'n':
                                sb.append('\n');
                                break;
                            case 'f':
                                sb.append('\f');
                                break;
                            case 'r':
                                sb.append('\r');
                                break;
                            case '"':
                            case '\'':
                            case '\\':
                            case '/':
                                sb.append(escape);
                                break;
                            case 'u': // 处理unicode字符
                                char result = 0;
                                for (int i = 0; i < 4; i++) {
                                    int c = nextChar();
                                    result <<= 4;
                                    if (c >= '0' && c <= '9') {
                                        result += (c - '0');
                                    } else if (c >= 'a' && c <= 'f') {
                                        result += (c - 'a' + 10);
                                    } else if (c >= 'A' && c <= 'F') {
                                        result += (c - 'A' + 10);
                                    } else {
                                        throw new ParserException("\\u unexpected char = " + (char)c);
                                    }
                                }
                                sb.append(result);
                                break;
                            default:
                                throw new ParserException("\\ unexpected char = " + (char)ch);

                        }
                    default:
                        sb.append((char)ch);

                }
            }
        }
    }

    public Number nextNumber() {
        StringBuilder nb = new StringBuilder();
        Boolean floatNumber = false;
        if (current == '-') {
            nb.append('-');
            for (;;) {
                int ch = nextChar();
                if (ch >= '0' && ch <= '9') {
                    nb.append((char)ch);
                } else if (ch == '.') {
                    floatNumber = true;
                    nb.append((char)ch);
                } else {
                    break;
                }
            }
        } else {
            nb.append(current);
            for (;;) {
                int ch = nextChar();
                if (ch >= '0' && ch <= '9') {
                    nb.append((char)ch);
                } else if (ch == '.') {
                    floatNumber = true;
                    nb.append((char)ch);
                } else {
                    break;
                }
            }
        }
        backChar();
        String numberStr = nb.toString();
        if (floatNumber) {
            return new BigDecimal(numberStr);
        } else {
            return Long.valueOf(numberStr);
        }
    }

    public boolean hasNext() {
        return pos < limit;
    }

    public boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }
}
