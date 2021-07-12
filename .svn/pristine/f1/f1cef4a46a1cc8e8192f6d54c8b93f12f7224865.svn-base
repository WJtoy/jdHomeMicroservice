package com.kkl.kklplus.b2b.jdhome.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JdHomeUtils {

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    public static String toJson(Object object) {
        try {
            return gson.toJson(object);
        }catch (Exception e){
            log.error("JSON格式化异常:{}",object.toString());
            return "";
        }
    }
}
