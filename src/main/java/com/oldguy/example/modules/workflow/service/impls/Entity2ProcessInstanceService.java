package com.oldguy.example.modules.workflow.service.impls;

import com.oldguy.example.modules.test.dao.entities.Entity2Process;
import com.oldguy.example.modules.test.dao.jpas.Entity2ProcessMapper;
import com.oldguy.example.modules.workflow.configs.WorkFlowConfiguration;
import com.oldguy.example.modules.workflow.dto.WorkBtn;
import com.oldguy.example.modules.workflow.service.AbstractProcessInstanceService;
import com.oldguy.example.modules.workflow.service.ProcessInstanceService;
import org.activiti.bpmn.model.SequenceFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author huangrenhao
 * @date 2019/1/24
 */
@Service
public class Entity2ProcessInstanceService extends AbstractProcessInstanceService<Entity2Process> {

}
