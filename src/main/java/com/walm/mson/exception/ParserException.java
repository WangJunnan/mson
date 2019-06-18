package com.walm.mson.exception;

/**
 * <p>ParserException</p>
 *
 * @author wangjn
 * @date 2019/6/18
 */
public class ParserException extends RuntimeException {
    public ParserException(String msg) {
        super(msg);
    }

    public ParserException() {
        super();
    }
}
