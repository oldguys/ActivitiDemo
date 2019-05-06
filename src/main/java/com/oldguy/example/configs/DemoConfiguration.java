package com.oldguy.example.configs;

import com.oldguy.example.modules.common.configs.DbRegisterConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * @Date: 2019/1/13 0013
 * @Author: ren
 * @Description:
 */
@Configuration
@MapperScan(basePackages={
        "com.oldguy.example.modules.sys.dao.jpas",
        "com.oldguy.example.modules.test.dao.jpas",
        "com.oldguy.example.modules.workflow.dao.jpas"
})
public class DemoConfiguration {


    public static final Integer DEFAULT_PAGE_SIZE = 15;

    public static class WorkFlowConfig {

        public static String ENTITY1_PROCESS = "ENTITY1_PROCESS";

    }

    @Autowired
    private DataSource dataSource;

    @Value("${mybatis.type-aliases-package}")
    private String typeAliasesPackage;

    @PostConstruct
    public void init() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        DbRegisterConfiguration configuration = new DbRegisterConfiguration();
        configuration.initDB(typeAliasesPackage, jdbcTemplate);
    }
}
