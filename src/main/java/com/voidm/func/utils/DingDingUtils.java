package com.voidm.func.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

/**
 * @author voidm
 * @date 2021/3/15
 */
public class DingDingUtils {

    public static final String TOKEN = "3f131019eacec474cc3be35a63bf1f491879670be86efdf3d8bf000eaf568a38";


    public static void notice (String msg, String mobile , boolean atAll) throws InterruptedException {

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("msgtype", "text");
            JSONObject text = new JSONObject();
            text.put("content", msg);
            requestBody.put("text", text);

            JSONObject at = new JSONObject();
            JSONArray array = new JSONArray();
            array.add(mobile);
            at.put("atMobiles", array);
            at.put("isAtAll", atAll);
            requestBody.put("at", at);

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, requestBody.toJSONString());
            Request request = new Request.Builder()
                    .url("https://oapi.dingtalk.com/robot/send?access_token=" + TOKEN)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}