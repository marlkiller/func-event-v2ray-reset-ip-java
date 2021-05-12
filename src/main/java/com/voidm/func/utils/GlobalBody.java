package com.voidm.func.utils;


import com.alibaba.fastjson.JSONArray;

/**
 * @author voidm
 * @date 2021/4/9
 */
public class GlobalBody {

    public static JSONArray body = new JSONArray();


    public static void add (String item) {
        body.add(item);
    }

    public static String ret (){
        return body.toString();
    }
}