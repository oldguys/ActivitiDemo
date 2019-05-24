package com.oldguy.example.modules.common.utils;/**
 * Created by Administrator on 2018/10/26 0026.
 */

import com.google.gson.Gson;

import java.util.Map;

/**
 * @Description:
 * @Author: ren
 * @CreateTime: 2018-10-2018/10/26 0026 15:46
 */
public class JsonUtils {

    private JsonUtils() {
    }

    private static volatile Gson gson;

    public static Gson getInstance() {

        Gson temp = gson;
        if (temp == null) {
            synchronized (Gson.class) {
                if (gson == null) {
                    gson = new Gson();
                    temp = gson;
                }
            }
        }
        return temp;
    }

    public static Map<String,Object> parseResultMap(String json){
        return getInstance().fromJson(json,Map.class);
    }
}
