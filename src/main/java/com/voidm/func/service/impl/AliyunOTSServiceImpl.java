package com.voidm.func.service.impl;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.DeleteRowRequest;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.PrimaryKeyBuilder;
import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;
import com.alicloud.openservices.tablestore.model.PutRowRequest;
import com.alicloud.openservices.tablestore.model.RangeIteratorParameter;
import com.alicloud.openservices.tablestore.model.Row;
import com.alicloud.openservices.tablestore.model.RowDeleteChange;
import com.alicloud.openservices.tablestore.model.RowPutChange;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

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
        getRangeByIterator(client,"2021-12-13 10:23:32","2023-12-13 10:23:32");
    }

    private static void getRangeByIterator(SyncClient client, String startPkValue, String endPkValue) {
        RangeIteratorParameter rangeIteratorParameter = new RangeIteratorParameter(TABLE_NAME);

        //设置起始主键。
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        primaryKeyBuilder.addPrimaryKeyColumn("id", PrimaryKeyValue.fromString(startPkValue));
        rangeIteratorParameter.setInclusiveStartPrimaryKey(primaryKeyBuilder.build());

        //设置结束主键。
        primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        primaryKeyBuilder.addPrimaryKeyColumn("id", PrimaryKeyValue.fromString(endPkValue));
        rangeIteratorParameter.setExclusiveEndPrimaryKey(primaryKeyBuilder.build());

        rangeIteratorParameter.setMaxVersions(1);

        Iterator<Row> iterator = client.createRangeIterator(rangeIteratorParameter);

        System.out.println("使用Iterator进行GetRange的结果为：");
        while (iterator.hasNext()) {
            Row row = iterator.next();
            System.out.println(row);
            DeleteRowRequest deleteRowRequest = new DeleteRowRequest();
            deleteRowRequest.setRowChange(new RowDeleteChange(TABLE_NAME,row.getPrimaryKey()));
            client.deleteRow(deleteRowRequest);
        }
    }
}