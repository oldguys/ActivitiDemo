package com.oldguy.example.services;

import com.oldguy.example.modules.test.service.Entity5ProcessService;
import org.activiti.engine.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public void test1() {

        List<String> assigneeList = new ArrayList<>();
        assigneeList.add("2");
        assigneeList.add("4");
        assigneeList.add("6");

        entity5ProcessService.test1("17520", assigneeList);
    }

    @Test
    public void testOpenInstance() {
        entity5ProcessService.openProcessInstance();
    }

    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testDefaultComplete() {
        Map<String, Object> data = new HashMap<>(1);
        data.put("assignee", "3");
        taskService.complete("112506", data);
//        throw new RuntimeException("transpondTask。。。。。。。。。。。");
    }

    @Test
    public void testTranspondTask() {
        entity5ProcessService.transpondTask("97522", "transpondTask-b", "transpondTask-c", "transpondTask-d", "transpondTask-e");
    }
}
