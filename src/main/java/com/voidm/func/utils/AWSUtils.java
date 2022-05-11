package com.voidm.func.utils;

import com.voidm.func.service.DNSService;
import com.voidm.func.service.impl.AliyunDNSServiceImpl;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lightsail.LightsailClient;
import software.amazon.awssdk.services.lightsail.model.GetInstanceRequest;
import software.amazon.awssdk.services.lightsail.model.GetInstanceResponse;
import software.amazon.awssdk.services.lightsail.model.Instance;
import software.amazon.awssdk.services.lightsail.model.StartInstanceRequest;
import software.amazon.awssdk.services.lightsail.model.StopInstanceRequest;

public class AWSUtils {
    /**
     * aws 云商 ak/sk
     */
    public static final String AK = "";
    public static final String SK = "";

    /**
     * 实例名称
     */
    public static final String INSTANCE_NAME = "CentOS-1-V2ray";
    public static final int INSTANCE_PORT = 3306;

    public static DNSService dnsService = new AliyunDNSServiceImpl();

    private static final LightsailClient lightsailClient;

    static {
        AwsCredentials credentials = AwsBasicCredentials.create(AWSUtils.AK, AWSUtils.SK);
        lightsailClient =
                LightsailClient.builder().region(Region.AP_SOUTHEAST_1).credentialsProvider(StaticCredentialsProvider.create(credentials)).build();

    }

    /**
     * 更换 ip 流程 (以 当前机器状态为启动状态为前提)
     * 1. 关闭机器
     * 2. 开启机器 (aws 开机自动刷新 ip)
     * 3. 将新的 ip 地址 映射到 cloudflare 域名中
     */
    public static Instance refreshIp() throws InterruptedException {
        Instance instance;
        // AwsCredentials credentials = AwsBasicCredentials.create(AWSUtils.AK, AWSUtils.SK);
        // LightsailClient lightsailClient = LightsailClient.builder().region(Region.AP_SOUTHEAST_1)
        // .credentialsProvider(StaticCredentialsProvider.create(credentials)).build();

        // 当前状态是否为已启动
        checkStatus(lightsailClient, 16);
        stopInstance();
        // 当前状态是否为已停止
        checkStatus(lightsailClient, 80);
        startInstance();
        // 当前状态是否为已启动
        instance = checkStatus(lightsailClient, 16);
        // dnsService.setDNSValue(instance.publicIpAddress());
        dnsService.setDNSValue(instance.publicIpAddress());
        DingDingUtils.notice(String.format("v2ray vpn new ip is  [ %s ]", instance.publicIpAddress()), "17665212866",
                true);
        return instance;
    }

    public static void startInstance() {
        lightsailClient.startInstance(StartInstanceRequest.builder().instanceName(AWSUtils.INSTANCE_NAME).build());
    }

    public static void stopInstance() {
        lightsailClient.stopInstance(StopInstanceRequest.builder().instanceName(AWSUtils.INSTANCE_NAME).build());
    }

    /**
     * 根据指定状态返回
     */
    private static Instance checkStatus(LightsailClient lightsailClient, int currentCode) throws InterruptedException {
        while (true) {
            Thread.sleep(5000);
            GetInstanceResponse instanceResponse =
                    lightsailClient.getInstance(GetInstanceRequest.builder().instanceName(AWSUtils.INSTANCE_NAME).build());
            Instance instance = instanceResponse.instance();
            String format = String.format("current public_ip : {%s} , current status : {%s}:{%s} ",
                    instance.publicIpAddress(), instance.state().code(), instance.state().name());
            System.out.println(format);
            GlobalBody.add(format);
            if (instance.state().code().equals(currentCode)) {
                return instance;
            }
        }
    }

    /**
     * 获取当前 lightsail 实例
     */
    public static Instance getInstance() {
        // AwsCredentials credentials = AwsBasicCredentials.create(AWSUtils.AK, AWSUtils.SK);
        // LightsailClient lightsailClient = LightsailClient.builder().region(Region.AP_SOUTHEAST_1)
        // .credentialsProvider(StaticCredentialsProvider.create(credentials)).build();
        GetInstanceResponse instanceResponse =
                lightsailClient.getInstance(GetInstanceRequest.builder().instanceName(AWSUtils.INSTANCE_NAME).build());
        return instanceResponse.instance();

    }

    public static void checkDNSValue(String publicIpAddress) {
        String dnsValue = dnsService.getDNSValue();
        if (dnsValue != null) {
            if (!dnsValue.equals(publicIpAddress)) {
                dnsService.setDNSValue(publicIpAddress);
            }
        }
    }
}