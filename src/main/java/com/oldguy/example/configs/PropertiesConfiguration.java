package com.oldguy.example.configs;

import com.oldguy.example.modules.common.utils.Log4jUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author huangrenhao
 * @date 2019/1/25
 */
@Configuration
public class PropertiesConfiguration {

    /**
     * 路径映射
     */
    public static final Map<String, String> WORKFLOW_TASK_URL_MAP = new HashMap<>();

    @Value("${workflow-configuration-location}")
    private String WORKFLOW_CONFIGURATION_LOCATION;

    /**
     * 初始化环境常量
     *
     * @throws IOException
     */
    public void init() throws IOException {
        Log4jUtils.getInstance(getClass()).info("初始化环境常量");
        setProperties(WORKFLOW_CONFIGURATION_LOCATION, WORKFLOW_TASK_URL_MAP);
    }

    private void setProperties(String configPath, Map<String, String> valueMap) throws IOException {
        Properties properties = new Properties();
        File file = ResourceUtils.getFile("classpath:" + configPath);
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        properties.load(new InputStreamReader(bufferedInputStream, "gbk"));
        fileInputStream.close();

        for (Object key : properties.keySet()) {
            valueMap.put(String.valueOf(key), String.valueOf(properties.get(key)));
        }
    }
}
