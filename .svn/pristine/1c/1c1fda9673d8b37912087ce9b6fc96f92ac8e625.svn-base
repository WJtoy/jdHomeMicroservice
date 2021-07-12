package com.kkl.kklplus.b2b.jdhome.service;

import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.JdException;
import com.jd.open.api.sdk.request.JdRequest;
import com.jd.open.api.sdk.response.AbstractResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class B2BBaseService {

    @Autowired
    private JdClient jdClient;

    /**
     * 调用天猫接口
     * @param request
     * @param <T>
     * @return
     * @throws JdException
     */
    public <T extends AbstractResponse> T executeJdAPI(JdRequest<T> request) throws Exception {
        return jdClient.execute(request);
    }

}
