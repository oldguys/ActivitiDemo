package com.oldguy.example.modules.common.dto;

/**
 * @author huangrenhao
 * @date 2018/8/19
 */
public interface Form<T> {

    String DATE_TIME_REGEXP = "/^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\\s+(20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d$/";

    /**
     *  专函成为实体
     * @return
     */
    T trainToEntity();
}
