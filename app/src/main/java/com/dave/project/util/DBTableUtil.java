package com.dave.project.util;

import android.util.Log;

import java.util.List;

/**
 * 创建和删除db表的语句
 * Created by Dave on 2016/12/30.
 */
public class DBTableUtil {
    /**
     * 返回拼接好的sql语句(主键自动默认设置为_id,自增长)
     *
     * @param tableName 表名字
     * @param clumList  clum集合
     * @return 返回创建表的语句
     */
    public synchronized static String appendCreateTableSqString(String tableName, List<String> clumList) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("create table if not exists").append(" ");
        stringBuilder.append(tableName).append(" ");
        stringBuilder.append("(_id integer primary key autoincrement,");
        for (String clum : clumList) {
            stringBuilder.append(clum).append(" ");
            if (!clum.equals(clumList.get(clumList.size() - 1))) {
                stringBuilder.append("text,");
            } else {
                stringBuilder.append("text)");
            }
        }
        Log.e("db", stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     * @param tableName 表明
     * @return 返回删除表的语句
     */
    public synchronized static String dropTable(String tableName) {
        return "DROP TABLE IF EXISTS " + tableName;
    }
}
