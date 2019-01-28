package com.oldguy.example.modules.common.utils;/**
 * Created by Administrator on 2018/9/20 0020.
 */

import org.apache.log4j.Logger;

/**
 * @Description:
 * @Author: ren
 * @CreateTime: 2018-09-2018/9/20 0020 22:05
 */
public class Log4jUtils {
    public static Logger logger;

    public static Logger getInstance(Class clazz){
        logger = Logger.getLogger(clazz);
        return logger;
    }
}
