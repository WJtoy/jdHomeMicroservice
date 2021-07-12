package com.kkl.kklplus.b2b.jdhome.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kkl.kklplus.b2b.jdhome.config.B2BJdProperties;
import com.kkl.kklplus.b2b.jdhome.entity.JDRequestData;
import com.kkl.kklplus.common.exception.MSErrorCode;
import com.kkl.kklplus.common.response.MSResponse;
import com.kkl.kklplus.entity.b2bcenter.md.B2BSystemCodeEnum;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Slf4j
@Component
public class OkHttpUtils {

    private static final MediaType CONTENT_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    @Autowired
    private OkHttpClient okHttpClient;
    @Autowired
    private B2BJdProperties jdProperties;

    public <T> MSResponse<T> transferToOtherSite(String json,String url){
        RequestBody requestBody = RequestBody.create(CONTENT_TYPE_JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        return syncGenericNewCall(request,new TypeToken<MSResponse>(){}.getType());
    }

    public <T> MSResponse<T> syncGenericNewCall(Request request, Type typeOfT) {
        MSResponse responseBody = null;
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBodyJson = response.body().string();
                responseBody = new Gson().fromJson(responseBodyJson, typeOfT);
                if (responseBody == null) {
                    return new MSResponse<>(MSErrorCode.FAILURE);
                }
                return responseBody;
            }
            return new MSResponse<>(response.code(), response.message(), null, null);
        } catch (Exception e) {
            log.error("syncGenericNewCall {}", e);
            return new MSResponse<>(MSErrorCode.FAILURE.getCode(), e.getMessage(), null, null);
        }
    }

    public boolean isThisSystem(Integer systemId, String type, Long id) {
        return this.isThisSystem(systemId,type,id,0L,0L);
    }

    public boolean isThisSystem(Integer systemId, String type, Long id,Long kklOrderId,Long msgEventDt) {
        String systemCode = jdProperties.getSite().getCode();
        B2BSystemCodeEnum thisSystemCodeEnum = B2BSystemCodeEnum.get(systemCode);
        if(thisSystemCodeEnum == B2BSystemCodeEnum.UNKNOWN){
            log.error("没找到本系统枚举,systemCode:"+systemCode);
            return false;
        }
        if(systemId != thisSystemCodeEnum.id){
            B2BSystemCodeEnum systemCodeEnum = B2BSystemCodeEnum.get(systemId);
            if(systemCodeEnum == B2BSystemCodeEnum.UNKNOWN){
                log.error("没找到对应的系统,id:"+systemId);
                return false;
            }
            String url = jdProperties.getSite().getOtherSites().get(systemCodeEnum.code);
            if(url == null){
                log.error("没找到对应的系统的转发地址，id:"+systemId);
                return false;
            }
            JDRequestData requestData = new JDRequestData();
            requestData.setId(id);
            requestData.setType(type);
            requestData.setSite(systemCodeEnum.code);
            requestData.setKklOrderId(kklOrderId);
            requestData.setMsgEventDt(msgEventDt);
            this.transferToOtherSite(new Gson().toJson(requestData),url);
            return false;
        }
        return true;
    }
}
