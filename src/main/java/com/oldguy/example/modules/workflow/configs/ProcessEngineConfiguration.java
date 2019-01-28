package com.oldguy.example.modules.workflow.configs;

import com.oldguy.example.modules.common.utils.Log4jUtils;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.stereotype.Component;

/**
 * @author huangrenhao
 * @date 2018/8/13
 * @Descripton 配置字符集
 */
@Component
public class ProcessEngineConfiguration implements ProcessEngineConfigurationConfigurer {

    @Override
    public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {
        processEngineConfiguration.setActivityFontName("宋体");
        processEngineConfiguration.setLabelFontName("宋体");
        processEngineConfiguration.setAnnotationFontName("宋体");
        Log4jUtils.getInstance(getClass()).info("配置字体:" + processEngineConfiguration.getActivityFontName());
    }
}
