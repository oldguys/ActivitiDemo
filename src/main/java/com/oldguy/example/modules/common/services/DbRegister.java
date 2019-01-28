package com.oldguy.example.modules.common.services;



import com.oldguy.example.modules.common.dao.entities.SqlTableObject;
import com.oldguy.example.modules.common.services.impls.MySQLTableFactory;
import com.oldguy.example.modules.common.utils.ClassUtils;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @Description:
 * @Author: ren
 * @CreateTime: 2018-10-2018/10/16 0016 10:33
 */
public class DbRegister {

    private TableFactory tableFactory;

    public DbRegister() {
        this.tableFactory = new MySQLTableFactory();
    }

    public DbRegister(TableFactory tableFactory) {
        this.tableFactory = tableFactory;
    }


    /**
     * 编写数据库配置文件
     *
     * @param packageNames
     */
    public Map<String, String> registerClassToDB(String... packageNames) {

        if (packageNames.length == 0) {
            return Collections.emptyMap();
        }

        List<Class> classList = new ArrayList<>();
        for (String packageName : packageNames) {
            classList.addAll(ClassUtils.getClasses(packageName));
        }

        List<SqlTableObject> sqlTableObjects = new ArrayList<>();
        for (Class clazz : classList) {
            if (clazz.isAnnotationPresent(Entity.class)) {

                SqlTableObject obj = new SqlTableObject();
                String tableName = "";
                String preIndex = "";

                if (clazz.isAnnotationPresent(Entity.class)) {
                    Entity annotation = (Entity) clazz.getAnnotation(Entity.class);
                    tableName = annotation.name();
                }

                tableName = StringUtils.isEmpty(tableName) ? preIndex + formatTableName(clazz.getSimpleName()) : tableName;
                obj.setTableName(tableName);

                // 配置字段
                List<Field> fields = new ArrayList<>();
                getAllField(clazz, fields);
                setTableColumns(obj, fields);
                sqlTableObjects.add(obj);
            }
        }

        //转换成为SQL Schema
        return trainToDBSchema(sqlTableObjects);
    }

    /**
     * 转换成为SQLSchema
     *
     * @param sqlTableObjects
     */
    private Map<String, String> trainToDBSchema(List<SqlTableObject> sqlTableObjects) {

        if (null == tableFactory) {
            throw new RuntimeException("TableFactory 不能为空！");
        }

        return tableFactory.trainToDBSchema(sqlTableObjects);
    }

    /**
     * 设置表格字段
     *
     * @param obj
     * @param fields
     */
    private void setTableColumns(SqlTableObject obj, List<Field> fields) {

        List<SqlTableObject.Column> columnList = new ArrayList<>();
        for (Field field : fields) {
            if (tableFactory.getColumnType().containsKey(field.getType())) {

                SqlTableObject.Column column = new SqlTableObject.Column();

                if (field.isAnnotationPresent(Id.class)) {
                    column.setPrimaryKey(true);
                    if (field.isAnnotationPresent(GeneratedValue.class)) {
                        GeneratedValue annotation = field.getAnnotation(GeneratedValue.class);
                        if (annotation.strategy().equals(GenerationType.AUTO)) {
                            column.setAutoIncrement(true);
                        }
                    }
                }

                if (field.isAnnotationPresent(Column.class)) {
                    Column annotation = field.getAnnotation(Column.class);

                    if (!StringUtils.isEmpty(annotation.name())) {
                        column.setName(annotation.name());
                    } else {
                        column.setName(formatTableName(field.getName()));
                    }

                    if (!StringUtils.isEmpty(annotation.columnDefinition())) {
                        column.setType(annotation.columnDefinition());
                    } else {
                        column.setType(tableFactory.getColumnType().get(field.getType()));
                    }

                    column.setLength(annotation.length());
                    column.setUnique(annotation.unique());
                    column.setNullable(annotation.nullable());
                } else {
                    column.setName(formatTableName(field.getName()));
                    column.setType(tableFactory.getColumnType().get(field.getType()));
                }
                columnList.add(column);
            }
        }
        obj.setColumns(columnList);
    }


    /**
     * 获取所有的 字段
     *
     * @param clazz
     * @param fields
     */
    private static void getAllField(Class clazz, List<Field> fields) {
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if (!clazz.getSuperclass().equals(Object.class)) {
            getAllField(clazz.getSuperclass(), fields);
        }
    }

    /**
     * 驼峰转双峰
     *
     * @param name
     * @return
     */
    public static String formatTableName(String name) {
        StringBuilder formatResult = new StringBuilder();
        char[] upperCaseArrays = name.toUpperCase().toCharArray();
        char[] defaultArrays = name.toCharArray();

        for (int i = 0; i < upperCaseArrays.length; i++) {
            if (i == 0) {
                formatResult.append(String.valueOf(defaultArrays[0]).toLowerCase());
                continue;
            }
            if (defaultArrays[i] == upperCaseArrays[i]) {
                formatResult.append("_" + String.valueOf(defaultArrays[i]).toLowerCase());
            } else {
                formatResult.append(defaultArrays[i]);
            }
        }

        return formatResult.toString();
    }


    public void setTableFactory(TableFactory tableFactory) {
        this.tableFactory = tableFactory;
    }

    public TableFactory getTableFactory() {
        return tableFactory;
    }

}
