package com.oldguy.example.services;

import com.oldguy.example.modules.test.dao.entities.Entity4Process;
import com.oldguy.example.modules.test.dao.jpas.Entity4ProcessMapper;
import com.oldguy.example.modules.workflow.service.activiti.DefaultInstanceConvertToMultiInstance;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * @ClassName: DefaultInstanceConvertToMultiInstanceTest
 * @Author: ren
 * @Description:
 * @CreateTIme: 2019/5/24 0024 上午 8:47
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class DefaultInstanceConvertToMultiInstanceTest extends AbstractMultiWorkFLowServiceTest<Entity4Process> {

    @Autowired
    private DefaultInstanceConvertToMultiInstance service;


    /**
     * 测试开启新流程，到节点1 不进入节点2 ，以便于测试
     */
    @Test
    public void testOpenProcessInstance() throws Exception {

        System.out.println(openProcessInstance(Entity4Process.class));
    }

    /**
     * 测试完成 节点1 任务到节点2 ，进行 普通任务向 会签任务进行转换
     */
    @Test
    public void testCovertToMultiInstance() {

        List<String> assigneeList = new ArrayList<>();
        assigneeList.add("1");
        assigneeList.add("2");
        assigneeList.add("3");
        Map<String, Object> map = new HashMap<>();
        map.put("assigneeList", assigneeList);

        service.covertToMultiInstance("7505", true, map);
    }

    /**
     * 完成任务 会签任务
     */
    @Test
    public void testCompleteTask() {

        service.completeTask("15002", null);
//        service.completeTask("2520", null);
//        service.completeTask("2522", null);
//        throw new RuntimeException("test....");
    }





}
