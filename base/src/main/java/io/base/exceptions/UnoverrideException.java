package io.base.exceptions;

/**
 * Created by gaochao on 2016/3/4.
 */
public class UnoverrideException extends RuntimeException {
    public UnoverrideException(String msg){
        super(msg + "\nthis method has to be overridden");
    }
}
