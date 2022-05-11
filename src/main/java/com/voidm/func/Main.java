package com.voidm.func;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.StreamRequestHandler;
import com.voidm.func.service.impl.AliyunOTSServiceImpl;
import com.voidm.func.utils.AWSUtils;
import com.voidm.func.utils.GlobalBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import software.amazon.awssdk.services.lightsail.model.Instance;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * @author voidm
 * @date 2021/4/9
 */
public class Main implements StreamRequestHandler {

    public static void main(String[] args) {
        AWSUtils.checkDNSValue(AWSUtils.getInstance().publicIpAddress());
    }

    public String ginV2rayHost = "https://1356827337907157.cn-beijing.fc.aliyuncs.com/2016-08-15/proxy/fun_service_bj.LATEST/go_dev/";
    // public String ginV2rayHost = "http://go.fc.voidm.com";

    @Override
    public void handleRequest (InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        GlobalBody.add("hello world");
        try {
            Instance instance = AWSUtils.getInstance();
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(60, TimeUnit.MINUTES)
                    .readTimeout(60, TimeUnit.MINUTES)
                    .writeTimeout(60, TimeUnit.MINUTES)
                    .build();
            String url = ginV2rayHost + "?vmess=" + getVmess(instance.publicIpAddress());
            Request pingRequest = new Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .build();
            Response pingResponse = client.newCall(pingRequest).execute();
            JSONObject result = JSONObject.parseObject(pingResponse.body().string());
            int successCount = result.getIntValue("success");
            GlobalBody.add("pingReq : " + url);
            GlobalBody.add("pingResp : " + result.toJSONString());

            AliyunOTSServiceImpl.insertRow(instance.publicIpAddress(), successCount);

            if (successCount <= 1) {
                instance = AWSUtils.refreshIp();
            } else {
                if (instance.publicIpAddress() != null) {
                    AWSUtils.checkDNSValue(instance.publicIpAddress());
                }
            }
            // String requestPath = (String) request.getAttribute("FC_REQUEST_PATH");
            // String requestURI = (String) request.getAttribute("FC_REQUEST_URI");
            // String requestClientIP = (String) request.getAttribute("FC_REQUEST_CLIENT_IP");
            String body = String.format("ip: %s", instance.publicIpAddress());
            GlobalBody.add(body);

        } catch (Exception e) {
            e.printStackTrace();
            GlobalBody.add("error: " + e.getMessage());
        }
        outputStream.write(new String(GlobalBody.ret()).getBytes());
    }


    public String getVmess (String ip) {
        JSONObject v2rayJSON = new JSONObject();
        v2rayJSON.put("v", "2");
        v2rayJSON.put("ps", "aws-tcp");
        v2rayJSON.put("add", ip);
        // v2rayJSON.put("add", "3.0.182.85");
        v2rayJSON.put("port", "3306");
        v2rayJSON.put("id", System.getenv("uuid"));
        v2rayJSON.put("aid", "0");
        v2rayJSON.put("net", "tcp");
        v2rayJSON.put("type", "none");
        v2rayJSON.put("host", "");
        v2rayJSON.put("path", "");
        v2rayJSON.put("tls", "");
        return Base64.getEncoder().encodeToString(v2rayJSON.toJSONString().getBytes());
    }
}