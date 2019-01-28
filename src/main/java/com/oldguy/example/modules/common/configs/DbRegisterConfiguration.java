package com.oldguy.example.modules.common.configs;



import com.oldguy.example.modules.common.services.DbRegister;
import com.oldguy.example.modules.common.utils.Log4jUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;


import java.util.*;

/**
 * @Description:
 * @Author: ren
 * @CreateTime: 2018-10-2018/10/15 0015 17:45
 */
public class DbRegisterConfiguration {

    public void initDB(String typeAliasesPackage,JdbcTemplate jdbcTemplate) {

        DbRegister dbRegister = new DbRegister();
        Map<String, String> tableMap = new HashMap<>(16);
        List<String> typeAliasesPackages = splitPackagesPath(typeAliasesPackage);

        for (String path : typeAliasesPackages) {
            tableMap.putAll(dbRegister.registerClassToDB(path));
        }

        if (!tableMap.keySet().isEmpty()) {

            List<Map<String, Object>> mapList = jdbcTemplate.queryForList(dbRegister.getTableFactory().showTableSQL());
            Set<String> tableNameSet = new HashSet<>();
            for (Map<String, Object> item : mapList) {
                for (String key : item.keySet()) {
                    tableNameSet.add((String) item.get(key));
                }
            }

            for (String key : tableMap.keySet()) {
                if (!tableNameSet.contains(key)) {
                    Log4jUtils.getInstance(getClass()).info("未找到表[" + key + "],进行创建.");
                    String sql = tableMap.get(key);
                    if (sql.trim().length() > 0) {
                        jdbcTemplate.execute(sql);
                        Log4jUtils.getInstance(getClass()).info("\n\n" + sql);
                    }
                } else {
                    Log4jUtils.getInstance(getClass()).info("表[" + key + "] 已存在");
                }
            }
        }
    }

    private List<String> splitPackagesPath(String typeAliasesPackage) {
        List<String> paths = new ArrayList<>();
        String[] packagePaths = typeAliasesPackage.split(";");
        for (String path : packagePaths) {
            if (!StringUtils.isEmpty(path)) {
                paths.add(path);
            }
        }
        return paths;
    }


}
