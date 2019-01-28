package com.oldguy.example.modules.common.utils;

import com.oldguy.example.modules.common.exceptions.FormValidException;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author huangrenhao
 * @date 2019/1/8
 */
public class FormValidateUtils {

    private static Map<Class, String> annotationsType = new HashMap<>();
    private static Set<Class> baseType = new HashSet<>();

    static {
        annotationsType.put(NotBlank.class, "message");
        annotationsType.put(NotNull.class, "message");
        annotationsType.put(NotEmpty.class, "message");

        baseType.add(Integer.class);
        baseType.add(Long.class);
        baseType.add(Boolean.class);
        baseType.add(Float.class);
        baseType.add(Double.class);
        baseType.add(String.class);
        baseType.add(Character.class);
        baseType.add(Byte.class);
    }

    /**
     * 校验
     *
     * @param object     */

    public static void validate(Object object) {
        validate(object.getClass(), object, false);
    }

    public static void validate(Object object, boolean cascade) {
        validate(object.getClass(), object, cascade);
    }

    private static void validate(Class clazz, Object obj, boolean cascade) {
        if (clazz.equals(Object.class)) {
            return;
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            validateField(obj.getClass(), field, obj);
            if (cascade) {
                validateObjectField(field, obj);
            }
        }

        Class superClass = clazz.getSuperclass();
        if (!superClass.equals(Object.class)) {
            validate(superClass, obj, cascade);
        }

    }

    /**
     * 校验对象数据类型
     *
     * @param field
     * @param obj
     */
    private static void validateObjectField(Field field, Object obj) {

        String getMethodName = ReflectUtils.tranFieldToGetterMethodName(field.getName());
        Object value = null;
        try {
            // 不需要校验
            Method method = obj.getClass().getMethod(getMethodName);
            if (null == method) {
                return;
            }
            value = method.invoke(obj);
            if (null == value) {
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 剔除基本数据类型
        if (baseType.contains(value.getClass())) {
            Log4jUtils.getInstance(FormValidateUtils.class).debug("Base Type");
            return;
        }

        // 级联校验
        if (value instanceof Collection) {

            Log4jUtils.getInstance(FormValidateUtils.class).debug("validate Collection");
            Collection collection = Collection.class.cast(value);
            for (Object item : collection) {
                if (!ObjectUtils.isEmpty(item)) {
                    validate(item, true);
                }
            }
        } else if (value instanceof Map) {

            Log4jUtils.getInstance(FormValidateUtils.class).debug("validate Map");

        } else if (value instanceof Object) {

            Log4jUtils.getInstance(FormValidateUtils.class).debug("validate field [ " + value.getClass().getName() + " ]");
            validate(value, true);
        }


    }

    /**
     * 检验基本数据类型
     *
     * @param clazz
     * @param field
     * @param object
     */
    private static void validateField(Class clazz, Field field, Object object) {

        String getMethod = ReflectUtils.tranFieldToGetterMethodName(field.getName());

        String errorMessage = null;
        try {

            Method method = clazz.getMethod(getMethod);
            if (null == method) {
                // 不需要校验
                return;
            }
            Object value = method.invoke(object);

            for (Class key : annotationsType.keySet()) {
                Annotation annotation = field.getAnnotation(key);
                if (null != annotation) {
                    Object obj = key.cast(annotation);

                    Log4jUtils.getInstance(FormValidateUtils.class).debug(field.getClass().getSimpleName() + ":" + obj);

                    // 非空，有效值
                    if (!ObjectUtils.isEmpty(value)) {
                        Log4jUtils.getInstance(FormValidateUtils.class).debug(field.getClass().getSimpleName() + " 有效值!");
                        return;
                    }

                    Method messageMethod = key.getMethod(annotationsType.get(key));
                    if (null != messageMethod) {
                        errorMessage = (String) messageMethod.invoke(obj);
                        if (StringUtils.isEmpty(errorMessage)) {
                            errorMessage = field.getName() + " 出现异常";
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!StringUtils.isEmpty(errorMessage)) {
            Log4jUtils.getInstance(FormValidateUtils.class).warn("表单校验异常:" + errorMessage);
            throw new FormValidException(errorMessage);
        }
    }


}
