package com.oldguy.example.services;

import com.oldguy.example.modules.test.dao.entities.Entity4Process;
import com.oldguy.example.modules.test.dao.jpas.Entity4ProcessMapper;
import com.oldguy.example.modules.test.service.Entity4ProcessService;
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
 * @ClassName: Entity4ProcessTest
 * @Author: ren
 * @Description: 测试流程，将 动态修改 普通流程节点
 * @CreateTIme: 2019/4/29 0029 上午 10:45
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class Entity4ProcessTest {


    @Autowired
    private Entity4ProcessService entity4ProcessService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private Entity4ProcessMapper entity4ProcessMapper;
    @Autowired
    private TaskService taskService;


    /**
     * 测试完成 节点1 任务到节点2 ，进行 普通任务向 会签任务进行转换
     */
    @Test
    public void testCompleteTask() {
        entity4ProcessService.changeNode("102505");
    }

    /**
     * 测试开启新流程，到节点1 不进入节点2 ，以便于测试
     */
    @Test
    public void onlyOpenProcessInstance() {

        Entity4Process entity = newEntityInstance();

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(Entity4Process.class.getSimpleName(), Entity4Process.class.getSimpleName() + "." + entity.getId());
        System.out.println(processInstance.getId());
    }

    private Entity4Process newEntityInstance() {

        Entity4Process entity = new Entity4Process();

        entity.setCreateTime(new Date());
        entity.setAuditStatus("1");
        entity.setCreatorId("2");

        entity4ProcessMapper.save(entity);

        return entity;

    }
}
