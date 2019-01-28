package com.oldguy.example.modules.common.handles;

import com.oldguy.example.modules.common.exceptions.FormValidException;
import com.oldguy.example.modules.common.exceptions.NoLoginException;
import com.oldguy.example.modules.common.utils.HttpJsonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author huangrenhao
 * @date 2019/1/21
 */
@ControllerAdvice
public class LoginExceptionHandle {

    @ExceptionHandler(NoLoginException.class)
    public String NoLoginException(NoLoginException exception, Model model) {
        model.addAttribute("errorMessage", exception.getMessage());
        return "/login";
    }
}
