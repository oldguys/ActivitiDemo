package com.oldguy.example.modules.workflow.service.activiti;

import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;

import java.util.Map;

/**
 * @ClassName: DefaultInstanceConvertToMultiInstance
 * @Author: ren
 * @Description:
 * @CreateTIme: 2019/5/23 0006 上午 10:57
 *
 * 业务功能描述：将 普通节点 -> 会签节点
 *
 *  描述：
 *   流程： 节点1 - 节点2 - 节点3
 *   需求： 完成节点1 时，根据 表单参数确定是否转换 节点2（普通节点） 为 会签节点
 * 注意：
 *      1. EventListener 存在于 进行时任务， 这时已经根据 Bpmn Model 生成了流程 任务，此时改动无效。
 *      2. 经过测试, 会签任务生成的时，会把 入参 Map<"AssingeeList",List<"assignee">> 转换 成为多个任务。
 *      在 EventListener 中 assignee 已经被赋值。
 *      3. 根据测试，在 Activiti 使用 org.activiti.engine.impl.cmd.CompleteTaskCmd 时，会根据 Bpmn Model 进行任务完成，
 *      如果是会签任务，则会进入到会签的 behavior ，如果不具备 behavior 则会把任务安装普通任务执行，导致出现 多个 下一节点任务。
 *      所以需要解决方式 需要把 普通节点 -> 会签节点
 * 实现原理：
 *      1. 在完成 节点1 任务前，获取到Bpmn Model > Process > FlowElement（UseTask）。
 *      2. 根据 UseTask （节点1） 获取 OutgoingFlow 连线 以获取 到下一个节点 节点2（需要从 普通 转换到 会签）
 *      3. 根据 会签节点模型 ，添加需要的组件： MultiInstanceLoopCharacteristics  ParallelMultiInstanceBehavior。注意参数需要与 XML 配置中一致
 *      4. 完成节点1 任务，在完成任务时 传入需要会签的任务列表。此时 根据Bpmn Model 会创建 会签任务。
 *      5. 完成任务后，将 已添加组件 移除，将节点2 从 会签节点转换 成为 普通节点。
 *
 *      以上为 普通 - > 会签 生成会签任务阶段
 *
 *      6. 完成会签任务的时候，需要在完成任务之前 修改 BpmnModel - > UseTask (current Node) ,
 *      将 会签任务的核心组件 Behavior 和 LoopCharacteristics 重新注入到节点之中
 *      7. 完成任务
 *      8. 将  Behavior 和 LoopCharacteristics 从节点中移除 将 会签节点 -> 普通节点
 **/
public interface DefaultInstanceConvertToMultiInstance extends MultiWorkFLow {

    String ASSIGNEE_USER = "assignee";

    String DEFAULT_ASSIGNEE_LIST_EXP = "${assigneeList}";

    String ASSIGNEE_USER_EXP = "${" + ASSIGNEE_USER + "}";

    /**
     * 将 普通节点转换成为会签 任务
     *
     * @param taskId
     * @param sequential
     * @param data
     */
    void covertToMultiInstance(String taskId, boolean sequential, Map<String, Object> data);

    /**
     *  将 普通节点转换成为会签 任务
     * @param taskId
     * @param sequential
     * @param assigneeExp 任务执行人表达式
     * @param data
     */
    void covertToMultiInstance(String taskId, boolean sequential, String assigneeExp, Map<String, Object> data);

    /**
     * 创建 多实例 行为解释器
     *
     * @param userTask
     * @param sequential
     * @return
     */
    MultiInstanceActivityBehavior createMultiInstanceBehavior(UserTask userTask, boolean sequential);

    /**
     * 创建多实例行为解释器
     * @param userTask 流程节点
     * @param sequential 是否串行
     * @param assigneeListExp 用户组表达
     * @param assigneeExp 用户标识
     * @return
     */
    MultiInstanceActivityBehavior createMultiInstanceBehavior(UserTask userTask, boolean sequential, String assigneeListExp, String assigneeExp);

    /**
     * 创建多实例 循环解释器
     *
     * @param isSequential    是否串行
     * @param assigneeListExp 用户组表达
     * @param assignee        用户标识
     * @return
     */
    MultiInstanceLoopCharacteristics createMultiInstanceLoopCharacteristics(boolean isSequential, String assigneeListExp, String assignee);

    /**
     * 创建多实例 循环解释器
     *
     * @param isSequential 是否 串行
     * @return
     */
    MultiInstanceLoopCharacteristics createMultiInstanceLoopCharacteristics(boolean isSequential);
}
