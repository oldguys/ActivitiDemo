package com.oldguy.example.services;

import com.oldguy.example.modules.test.dao.entities.Entity3Process;
import com.oldguy.example.modules.test.dao.jpas.Entity3ProcessMapper;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * @ClassName: Entity3ProcessTest
 * @Author: ren
 * @Description:
 * @CreateTIme: 2019/4/30 0030 上午 8:49
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class Entity3ProcessTest {


    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private Entity3ProcessMapper entity3ProcessMapper;
    @Autowired
    private TaskService taskService;




    /**
     * 测试完成 节点1 任务到节点2 ，进行 普通任务向 会签任务进行转换
     */
    @Test
//    @Transactional(rollbackFor = Exception.class)
    public void testCompleteTask() {
        String taskId = "2506";


        List<String> assigneeList = new ArrayList<>();
        assigneeList.add("1");
        assigneeList.add("2");
        assigneeList.add("3");
        Map<String, Object> map = new HashMap<>();
        map.put("assigneeList", assigneeList);

        taskService.complete(taskId, map);
//        throw new RuntimeException("测试。。。。。。。。。。。");
    }

    /**
     * 测试开启新流程，到节点1 不进入节点2 ，以便于测试
     */
    @Test
    public void onlyOpenProcessInstance() {

        Entity3Process entity = newEntityInstance();
        Map<String, Object> map = new HashMap<>();
        map.put("assignee", "2");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(Entity3Process.class.getSimpleName(), Entity3Process.class.getSimpleName() + "." + entity.getId(), map);
        System.out.println(processInstance.getId());
    }

    private Entity3Process newEntityInstance() {

        Entity3Process entity = new Entity3Process();

        entity.setCreateTime(new Date());
        entity.setAuditStatus("1");
        entity.setCreatorId("2");

        entity3ProcessMapper.save(entity);

        return entity;

    }

}
