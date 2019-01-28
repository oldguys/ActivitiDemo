package com.oldguy.example.modules.sys.aop;/**
 * Created by Administrator on 2018/10/24 0024.
 */



import com.oldguy.example.modules.common.exceptions.NoLoginException;
import com.oldguy.example.modules.common.utils.Log4jUtils;
import com.oldguy.example.modules.sys.dao.entities.UserEntity;
import com.oldguy.example.modules.sys.services.UserEntityService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


/**
 * @Description:
 * @Author: ren
 * @CreateTime: 2018-10-2018/10/24 0024 20:59
 */
@Aspect
@Component
public class LoginAspect {

    @Pointcut("within(com.oldguy.example.modules.*.controllers.*)")
    public void login() {
    }

    @Pointcut("@annotation(com.oldguy.example.modules.sys.annonation.NoLoginPerm)")
    public void noLoginMethod() {
    }

    @Pointcut("within(com.oldguy.example.modules.view.controllers.LoginController)")
    public void noLogin() {
    }

    @Before("login() && !noLoginMethod() && !noLogin()")
    public void before() {
        UserEntity entity = UserEntityService.getCurrentUserEntity();
        if(entity == null){
            throw new NoLoginException();
        }
        Log4jUtils.getInstance(getClass()).info("用户[" + entity.getUserId() + "]：正在使用");
    }
}
