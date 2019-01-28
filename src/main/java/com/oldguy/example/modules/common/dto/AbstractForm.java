package com.oldguy.example.modules.common.dto;


import com.oldguy.example.modules.common.utils.ReflectUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * @author huangrenhao
 * @date 2018/11/22
 */
public abstract class AbstractForm<T> implements Form {

    protected <T> T defaultTrainToEntity(Class<T> clazz) {

        try {
            return ReflectUtils.updateEntityFormToEntity(this, clazz);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }
}
