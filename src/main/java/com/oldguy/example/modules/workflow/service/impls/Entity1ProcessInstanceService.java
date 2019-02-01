package com.oldguy.example.modules.workflow.service.impls;

import com.oldguy.example.modules.test.dao.entities.Entity1Process;
import com.oldguy.example.modules.test.dao.jpas.Entity1ProcessMapper;
import com.oldguy.example.modules.workflow.configs.WorkFlowConfiguration;
import com.oldguy.example.modules.workflow.dto.WorkBtn;
import com.oldguy.example.modules.workflow.service.AbstractProcessInstanceService;
import com.oldguy.example.modules.workflow.service.ProcessInstanceService;
import org.activiti.bpmn.model.SequenceFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangrenhao
 * @date 2019/1/21
 */
@Service
public class Entity1ProcessInstanceService extends AbstractProcessInstanceService<Entity1Process> {

}
