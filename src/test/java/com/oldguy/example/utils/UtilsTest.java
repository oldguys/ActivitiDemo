package com.oldguy.example.utils;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: UtilsTest
 * @Author: ren
 * @Description:
 * @CreateTIme: 2019/4/29 0029 下午 11:39
 **/
public class UtilsTest {


    @Test
    public void test(){

        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");

        String str = StringUtils.join(list,",");
        System.out.println(str);

    }
}
