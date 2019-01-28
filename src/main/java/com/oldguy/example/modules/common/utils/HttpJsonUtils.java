package com.oldguy.example.modules.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

/**
 * @author huangrenhao
 * @date 2019/1/19
 */
public class HttpJsonUtils {

    public static final HttpJsonResult OK = build(HttpStatus.OK, HttpStatus.OK.getReasonPhrase());
    public static final HttpJsonResult ERROR = build(HttpStatus.INTERNAL_SERVER_ERROR, "系统异常，请联系管理员!");

    public static HttpJsonResult buildValid(String message) {
        return build(HttpStatus.BAD_REQUEST, message);
    }

    public static HttpJsonResult buildError(String message) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public static HttpJsonResult buildSuccess(String message) {
        return build(HttpStatus.OK, message);
    }

    public static HttpJsonResult build(HttpStatus httpStatus, String message) {
        return build(httpStatus, message, null);
    }

    public static HttpJsonResult build(HttpStatus httpStatus, String message, Object object) {
        return new HttpJsonResult(httpStatus.value(), message, object);
    }

    private static class HttpJsonResult {

        private Integer code;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String message;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Object object;

        public HttpJsonResult(Integer code, String message, Object object) {
            this.code = code;
            this.message = message;
            this.object = object;
        }

        public Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }
}
