package com.oldguy.example.modules.common.exceptions;

/**
 * @author huangrenhao
 * @date 2019/1/21
 */
public class NoLoginException extends RuntimeException {

    public NoLoginException() {
        super("当前用户未登录!");
    }
}
