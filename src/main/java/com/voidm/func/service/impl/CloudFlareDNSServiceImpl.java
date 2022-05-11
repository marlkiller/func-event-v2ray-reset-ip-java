package com.voidm.func.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.voidm.func.service.DNSService;
import com.voidm.func.utils.GlobalBody;
import okhttp3.*;

/**
 * @author voidm
 * @date 2021/4/18
 */
public class CloudFlareDNSServiceImpl implements DNSService {


    public static final String CLOUD_FLARE_TOKEN = "";
    public static final String CLOUD_FLARE_REGION_ID = "01f630488de2981abd054da4322c8d74";
    public static final String CLOUD_FLARE_DNS_RECORD_ID = "003237c103c310ff86602003bb8039a7";

    @Override
    public String getDNSValue() {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("https://api.cloudflare.com/client/v4/zones/" + CLOUD_FLARE_REGION_ID + "/dns_records/" + CLOUD_FLARE_DNS_RECORD_ID)
                    .method("GET", null)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", CLOUD_FLARE_TOKEN)
                    .build();
            Response response = client.newCall(request).execute();
            return JSONObject.parseObject(response.body().string()).getJSONObject("result").getString("content");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setDNSValue(String ip) {
        try {

            JSONObject requestBody = new JSONObject();
            requestBody.put("type", "A");
            requestBody.put("name", "open.voidm.com");
            requestBody.put("content", ip);
            requestBody.put("ttl", 120);
            requestBody.put("proxied", false);

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, requestBody.toJSONString());
            Request request = new Request.Builder()
                    .url("https://api.cloudflare.com/client/v4/zones/" + CLOUD_FLARE_REGION_ID + "/dns_records/" + CLOUD_FLARE_DNS_RECORD_ID)
                    .method("PUT", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", CLOUD_FLARE_TOKEN)
                    .build();
            Response response = client.newCall(request).execute();

            String x = "refreshCloudFlareDNS.....";
            System.out.println(x);
            GlobalBody.add(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}