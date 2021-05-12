package com.voidm.func.service.impl;

import com.aliyun.alidns20150109.Client;
import com.aliyun.alidns20150109.models.DescribeDomainRecordInfoRequest;
import com.aliyun.alidns20150109.models.DescribeDomainRecordInfoResponse;
import com.aliyun.alidns20150109.models.DescribeDomainRecordInfoResponseBody;
import com.aliyun.alidns20150109.models.UpdateDomainRecordRequest;
import com.aliyun.teaopenapi.models.Config;
import com.voidm.func.service.DNSService;

/**
 * @author voidm
 * @date 2021/4/18
 */
public class AliyunDNSServiceImpl implements DNSService {

    private static final String RECORD_ID = "21619113321704448";
    private static Client client = null;
    private static String AK = "";
    private static String SK = "";

    static {
        try {
            Config config = new Config()
                    // 您的AccessKey ID
                    .setAccessKeyId(AK)
                    // 您的AccessKey Secret
                    .setAccessKeySecret(SK);
            // 访问的域名
            config.endpoint = "alidns.cn-beijing.aliyuncs.com";
            client = new Client(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDNSValue() {
        try {
            DescribeDomainRecordInfoRequest describeDomainRecordInfoRequest = new DescribeDomainRecordInfoRequest()
                    .setRecordId(RECORD_ID);
            // 复制代码运行请自行打印 API 的返回值
            DescribeDomainRecordInfoResponse describeDomainRecordInfoResponse =
                    client.describeDomainRecordInfo(describeDomainRecordInfoRequest);
            DescribeDomainRecordInfoResponseBody body = describeDomainRecordInfoResponse.getBody();
            return body.getValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setDNSValue(String ip) {
        try {
            UpdateDomainRecordRequest updateDomainRecordRequest = new UpdateDomainRecordRequest()
                    .setRecordId(RECORD_ID)
                    .setRR("open")
                    .setType("A")
                    .setValue(ip);
            // 复制代码运行请自行打印 API 的返回值
            client.updateDomainRecord(updateDomainRecordRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}