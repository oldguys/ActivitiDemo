package com.oldguy.example.services;

import com.oldguy.example.modules.test.service.Entity6ProcessService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * @ClassName: Entity5ProcessTest
 * @Author: ren
 * @Description:
 * @CreateTIme: 2019/5/6 0006 下午 12:18
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class Entity6ProcessTest {

    @Autowired
    private Entity6ProcessService entity6ProcessService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;

    /**
     *  测试会签减签
     */
    @Test
    public void testRemoveCountersigningTask() {

        List<String> assigneeList = new ArrayList<>();
        assigneeList.add("12");
        assigneeList.add("23");
//        assigneeList.add("777");
        assigneeList.add("888");
//        assigneeList.add("999");

        entity6ProcessService.removeCountersigningTask("22502", assigneeList);
    }

    /**
     *  测试会签加签
     */
    @Test
    public void testAddCountersigningTask() {

        List<String> assigneeList = new ArrayList<>();

        assigneeList.add("23");
        assigneeList.add("777");
        assigneeList.add("888");
        assigneeList.add("123");
        assigneeList.add("12");
        assigneeList.add("456");
        assigneeList.add("789");
        assigneeList.add("999");

        entity6ProcessService.addCountersigningTask("10002", assigneeList,"789");
    }

    @Test
    public void testOpenInstance() {
        entity6ProcessService.openProcessInstance();
    }

    /**
     *  会签任务:
     *   父级-execution_id
     *      子级 execution_id set
     *   完成任务时候，流程变量只是 子级的，而流程走到下一节点，是父级节点所以流程参数不在统一作用域，需要使用
     *   runtimeService.setVariables("2524", data); 将变量设置到父级 execution中
     */
    @Test
    public void completeFinalTask(){
        /**
         *  完成最后会签任务添加变量
         */
        Map<String, Object> data = new HashMap<>(1);
        data.put("assignee", "123456");
        runtimeService.setVariables("2505", data);

        taskService.complete("2514");
    }


    /**
     *  默认提交
     */
    @Test
    public void testDefaultComplete() {
//        List<String> assigneeList = new ArrayList<>();
//        assigneeList.add("12");
//        assigneeList.add("23");
//        assigneeList.add("456");
//
//        Map<String, Object> data = new HashMap<>(1);
//        data.put("assigneeList", assigneeList);
//        taskService.complete("5003",data);

//        Map<String, Object> data = new HashMap<>(1);
//        data.put("assignee", "123456");
//        taskService.complete("10003",data);

        taskService.complete("17502");

    }


}
