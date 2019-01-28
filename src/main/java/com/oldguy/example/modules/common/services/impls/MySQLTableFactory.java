package com.oldguy.example.modules.common.services.impls;


import com.oldguy.example.modules.common.dao.entities.SqlTableObject;
import com.oldguy.example.modules.common.services.TableFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: ren
 * @CreateTime: 2018-10-2018/10/23 0023 13:54
 */
public class MySQLTableFactory implements TableFactory {

    private static final Map<Class, String> columnType;

    static {
        columnType = new HashMap<>();
        columnType.put(Integer.class, "INT");
        columnType.put(Long.class, "BIGINT");
        columnType.put(String.class, "VARCHAR");
        columnType.put(Date.class, "DATETIME");
        columnType.put(Boolean.class, "TINYINT");
        columnType.put(Double.class, "DOUBLE");
    }


    @Override
    public String showTableSQL() {
        return "show tables";
    }

    @Override
    public String getDialect() {
        return "MySQL";
    }

    @Override
    public Map<Class, String> getColumnType() {
        return columnType;
    }

    @Override
    public Map<String, String> trainToDBSchema(List<SqlTableObject> sqlTableObjects) {

        Map<String, String> tableMap = new HashMap<>(sqlTableObjects.size());

        for (SqlTableObject obj : sqlTableObjects) {
            StringBuilder builder = new StringBuilder();
            builder.append("CREATE TABLE IF NOT EXISTS `" + obj.getTableName() + "` (").append("\n");

            for (int i = 0; i < obj.getColumns().size(); i++) {

                SqlTableObject.Column column = obj.getColumns().get(i);
                builder.append("`").append(column.getName()).append("` ");

                if (column.getType().equals("VARCHAR")) {
                    if (column.getLength() == null) {
                        builder.append(column.getType()).append("(").append(255).append(")");
                    } else {
                        builder.append(column.getType()).append("(").append(column.getLength()).append(")");
                    }
                } else {
                    builder.append(column.getType().toUpperCase());
                }

                if (column.isPrimaryKey()) {
                    builder.append(" PRIMARY KEY");
                    if (column.isAutoIncrement()) {
                        builder.append(" AUTO_INCREMENT");
                    }
                }

                if (!column.isNullable()) {
                    builder.append(" NOT NULL");
                }

                if(column.isUnique()){
                    builder.append(" UNIQUE");
                }

                if (i < obj.getColumns().size() - 1) {
                    builder.append(",");
                }

                builder.append("\n");
            }

            builder.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8 ;").append("\n\n");

            if (tableMap.containsKey(obj.getTableName())) {
                throw new RuntimeException(obj.getTableName() + " 表名重复。");
            } else {
                tableMap.put(obj.getTableName(), builder.toString());
            }
        }

        return tableMap;
    }
}
