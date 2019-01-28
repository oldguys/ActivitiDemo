package com.oldguy.example.modules.common.utils;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huangrenhao
 * @date 2018/8/28
 */
public class ReflectUtils {

    /**
     *  获取Method
     * @param clazz
     * @param methodName
     * @param args
     * @return
     */
    public static Method getMethod(Class clazz, String methodName, Class... args) {
        Method method = null;
        try {
            method = clazz.getMethod(methodName, args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return method;
    }

    /**
     * 通过实体表单转换成为实体
     *
     * @param source
     * @param clazz
     * @param <T>
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     */
    public static <T> T updateEntityFormToEntity(Object source, Class<T> clazz) throws IllegalAccessException, InstantiationException, InvocationTargetException {

        Map<String, Method> entityFormMethods = new HashMap<>(16);
        getMethodBySource(source.getClass(), entityFormMethods);

        Map<String, Object> sourceValueMap = new HashMap<>(16);
        getValueBySource(source, source.getClass(), entityFormMethods, sourceValueMap);

        Object obj = clazz.newInstance();
        setEntityMethodsByGetMethodSet(sourceValueMap, clazz, obj);

        return (T) obj;
    }

    /**
     *  将表单值注入到Map
     * @param source
     * @param clazz
     * @param entityFormMethods
     * @param sourceValueMap
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private static void getValueBySource(Object source, Class clazz, Map<String, Method> entityFormMethods, Map<String, Object> sourceValueMap) throws IllegalAccessException, InvocationTargetException {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String getMethodName = tranFieldToGetterMethodName(field.getName());
            Method method = entityFormMethods.get(getMethodName);
            if (null == method) {
                continue;
            }

            Object value = method.invoke(source);
            sourceValueMap.put(field.getName(), value);
        }

        if(!clazz.getSuperclass().equals(Object.class)){
            getValueBySource(source, clazz.getSuperclass(), entityFormMethods, sourceValueMap);
        }
    }

    /**
     *  获取包含父级的所有的 MethodMap
     * @param sourceClazz
     * @param entityFormMethods
     */
    private static void getMethodBySource(Class sourceClazz, Map<String, Method> entityFormMethods) {
        Method[] methods = sourceClazz.getMethods();
        for (Method method : methods) {
            entityFormMethods.put(method.getName(), method);
        }

        if (!sourceClazz.getSuperclass().equals(Object.class)) {
            getMethodBySource(sourceClazz.getSuperclass(), entityFormMethods);
        }
    }

    /**
     * @param sourceValueMap
     * @param obj
     */
    private static void setEntityMethodsByGetMethodSet(Map<String, Object> sourceValueMap, Class clazz, Object obj) throws InvocationTargetException, IllegalAccessException {

        Method[] methods = clazz.getMethods();
        Map<String, Method> setMethods = new HashMap<>(methods.length);
        for (Method method : methods) {
            setMethods.put(method.getName(), method);
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {

            Object value = sourceValueMap.get(field.getName());
            if (null == value) {
                continue;
            }

            Method method = setMethods.get(tranFieldToSetterMethodName(field.getName()));
            if (null != method) {
                method.invoke(obj, value);
            }
        }

        if (!clazz.getSuperclass().equals(Object.class)) {
            setEntityMethodsByGetMethodSet(sourceValueMap, clazz.getSuperclass(), obj);
        }

    }

    /**
     * 默认不更新集合信息
     *
     * @param source
     * @param target
     */
    public static void updateFieldByClass(Object source, Object target) {
        updateFieldByClass(source.getClass(), source, target, false);
    }

    /**
     * 级联更新对象属性值，Object.class 不在更新范围
     *
     * @param clazz             类类型
     * @param source            源对象
     * @param target            更新对象
     * @param updateCollections 是否级联更新集合
     */
    public static void updateFieldByClass(Class clazz, Object source, Object target, boolean updateCollections) {

        Field[] fields = clazz.getDeclaredFields();
        Method[] methods = clazz.getDeclaredMethods();

        Map<String, Method> methodMap = new HashMap<>(methods.length);
        for (Method method : methods) {
            methodMap.put(method.getName(), method);
        }

        updateFieldMap(source, target, updateCollections, fields, methodMap);

        Class superClazz = clazz.getSuperclass();
        if (!superClazz.equals(Object.class)) {
            Log4jUtils.getInstance(ReflectUtils.class).warn("映射类型:" + superClazz.getSimpleName());
            updateFieldByClass(superClazz, source, target, updateCollections);
        }

    }

    /**
     * 更新Filed
     *
     * @param source
     * @param target
     * @param updateCollections
     * @param fields
     * @param methodMap
     */
    private static void updateFieldMap(Object source, Object target, boolean updateCollections, Field[] fields, Map<String, Method> methodMap) {
        try {
            for (Field field : fields) {
                Method setMethod = methodMap.get(tranFieldToSetterMethodName(field.getName()));
                Method getMethod = methodMap.get(tranFieldToGetterMethodName(field.getName()));

                Object objValue = getMethod.invoke(source);
                if (objValue != null) {
                    if (objValue instanceof Collection && !updateCollections) {
                        continue;
                    }
                    if (setMethod != null) {
                        setMethod.invoke(target, objValue);
                    } else {
                        Log4jUtils.getInstance(ReflectUtils.class).warn(tranFieldToSetterMethodName(field.getName()) + " 不存在！");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String tranFieldToGetterMethodName(String field) {
        field = "get" + field.substring(0, 1).toUpperCase() + field.substring(1, field.length());
        return field;
    }

    public static String tranFieldToSetterMethodName(String field) {
        field = "set" + field.substring(0, 1).toUpperCase() + field.substring(1, field.length());
        return field;
    }
}
