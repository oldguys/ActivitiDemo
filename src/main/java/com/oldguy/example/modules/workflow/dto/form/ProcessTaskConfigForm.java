package com.oldguy.example.modules.workflow.dto.form;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Collections;
import java.util.List;

/**
 * @Date: 2019/1/27 0027
 * @Author: ren
 * @Description:
 */
@Data
public class ProcessTaskConfigForm {

    @NotBlank(message = "processDefinitionId 不能为空!")
    private String processDefinitionId;

    @NotBlank(message = "processDefinitionId 不能为空!")
    private String processDefinitionKey;

    @NotEmpty(message = "elements 不能为空!")
    private List<TaskConfigItem> elements = Collections.emptyList();

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public List<TaskConfigItem> getElements() {
        return elements;
    }

    @Data
    public static class TaskConfigItem{

        @NotBlank(message = "flowId 不能为空!")
        private String flowId;

        @NotBlank(message = "btn 不能为空!")
        private String btn;

        @NotBlank(message = "url 不能为空!")
        private String url;

        @NotBlank(message = "flowFlag 不能为空!")
        private String flowFlag;

        public String getFlowId() {
            return flowId;
        }

        public String getBtn() {
            return btn;
        }

        public String getUrl() {
            return url;
        }

        public String getFlowFlag() {
            return flowFlag;
        }
    }
}
