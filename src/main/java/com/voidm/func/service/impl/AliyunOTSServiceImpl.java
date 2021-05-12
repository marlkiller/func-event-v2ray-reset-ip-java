package com.voidm.func.service.impl;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author voidm
 * @date 2021/4/18
 */
public class AliyunOTSServiceImpl {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static SyncClient  client = null;
    private static String AK = "";
    private static String SK = "";

    public static final String END_POINT = "https://marlkiller-ots.cn-hangzhou.ots.aliyuncs.com";
    public static final String INSTANCE_NAME = "marlkiller-ots";
    public static final String TABLE_NAME = "t_v2ray_check_task";

    static {
        try {
            client = new SyncClient(END_POINT,AK,SK, INSTANCE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void insertRow(String ip,long successCount) {
        try {
            //构造主键。
            PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
            LocalDateTime localDateTime = LocalDateTime.now().plusHours(8L);
            primaryKeyBuilder.addPrimaryKeyColumn("id", PrimaryKeyValue.fromString(localDateTime.format(FORMATTER)));
            PrimaryKey primaryKey = primaryKeyBuilder.build();
            //设置数据表名称。
            RowPutChange rowPutChange = new RowPutChange(TABLE_NAME, primaryKey);

            rowPutChange.addColumn("ip",ColumnValue.fromString(ip));
            rowPutChange.addColumn("success_count",ColumnValue.fromLong(successCount));

            client.putRow(new PutRowRequest(rowPutChange));
            client.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main (String[] args) {
        System.out.println(LocalDateTime.now().atZone(ZoneId.of("Asia/Shanghai")).format(FORMATTER));
    }
}