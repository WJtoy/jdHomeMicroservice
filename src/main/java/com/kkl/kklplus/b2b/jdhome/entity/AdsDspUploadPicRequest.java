package com.kkl.kklplus.b2b.jdhome.entity;

import com.jd.open.api.sdk.internal.util.JsonUtil;
import com.jd.open.api.sdk.request.AbstractRequest;
import com.jd.open.api.sdk.request.JdRequest;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Auther wj
 * @Date 2021/2/4 18:17
 */
public class AdsDspUploadPicRequest extends AbstractRequest implements JdRequest<AdsDspUploadPicResponse> {

    public byte[] getParam1() {
        return param1;
    }

    public void setParam1(byte[] param1) {
        this.param1 = param1;
    }

    private byte[] param1;

    @Override
    public String getApiMethod() {
        return "jingdong.ads.dsp.uploadPic";
    }

    @Override
    public String getAppJsonParams() throws IOException {
        Map<String, Object> pmap = new TreeMap();
        pmap.put("param1", this.param1);
        return JsonUtil.toJson(pmap);
    }

    @Override
    public Class<AdsDspUploadPicResponse> getResponseClass() {
        return AdsDspUploadPicResponse.class;
    }
}
