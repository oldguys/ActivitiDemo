package com.oldguy.example.services;

import com.oldguy.example.modules.test.service.Entity5ProcessService;
import org.activiti.engine.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: Entity5ProcessTest
 * @Author: ren
 * @Description:
 * @CreateTIme: 2019/5/6 0006 下午 12:18
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class Entity5ProcessTest {

    @Autowired
    private Entity5ProcessService entity5ProcessService;
    @Autowired
    private TaskService taskService;

    @Test
    public void testOpenInstance(){
        entity5ProcessService.openProcessInstance();
    }

    @Test
    public void testDefaultComplete(){
        Map<String, Object> data = new HashMap<>(1);
        data.put("assignee", "3");
        taskService.complete("77506",data);
    }

    @Test
    public void test(){
        entity5ProcessService.test("87506","test-b","test-c");
    }
}
