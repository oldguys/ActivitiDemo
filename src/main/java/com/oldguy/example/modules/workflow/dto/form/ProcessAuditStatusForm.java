package com.oldguy.example.modules.workflow.dto.form;

import com.oldguy.example.modules.common.dto.AbstractForm;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Collections;
import java.util.List;

/**
 * @author huangrenhao
 * @date 2019/1/23
 */
public class ProcessAuditStatusForm {

    @NotBlank(message = "processDefinitionId 不能为空!")
    private String processDefinitionId;

    @NotBlank(message = "processDefinitionId 不能为空!")
    private String processDefinitionKey;

    @NotEmpty(message = "elements 不能为空!")
    private List<AuditItem> elements = Collections.emptyList();

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public List<AuditItem> getElements() {
        return elements;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public void setElements(List<AuditItem> elements) {
        this.elements = elements;
    }

    @Data
    public static class AuditItem {

        @NotBlank(message = "code 不能为空!")
        private String code;

        @NotBlank(message = "code 不能为空!")
        private String message;

        @NotBlank(message = "code 不能为空!")
        private String itemId;

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public String getItemId() {
            return itemId;
        }
    }
}
