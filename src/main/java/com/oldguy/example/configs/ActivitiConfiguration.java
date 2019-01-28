package com.oldguy.example.configs;

import org.activiti.spring.SpringAsyncExecutor;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.AbstractProcessEngineAutoConfiguration;
import org.activiti.spring.boot.ActivitiProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * @author huangrenhao
 * @date 2018/11/27
 */
@Configuration
@EnableConfigurationProperties(ActivitiProperties.class)
public class ActivitiConfiguration extends AbstractProcessEngineAutoConfiguration {

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public SpringProcessEngineConfiguration springProcessEngineConfiguration(
            DataSource dataSource,
            PlatformTransactionManager transactionManager,
            SpringAsyncExecutor springAsyncExecutor) throws IOException {

        return this.baseSpringProcessEngineConfiguration(dataSource, transactionManager, springAsyncExecutor);
    }
}
