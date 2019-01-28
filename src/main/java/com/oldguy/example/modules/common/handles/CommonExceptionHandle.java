package com.oldguy.example.modules.common.handles;/**
 * Created by Administrator on 2018/10/29 0029.
 */


import com.oldguy.example.modules.common.exceptions.FormValidException;
import com.oldguy.example.modules.common.utils.HttpJsonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Description:
 * @Author: ren
 * @CreateTime: 2018-10-2018/10/29 0029 15:23
 */
@RestControllerAdvice
public class CommonExceptionHandle {

    /**
     *  表单异常处理
     * @param exception
     * @return
     */
    @ExceptionHandler(FormValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Object unauthenticatedException(FormValidException exception) {
        return HttpJsonUtils.buildValid(exception.getMessage());
    }
}
