package com.kkl.kklplus.b2b.jdhome.controller;

import com.kkl.kklplus.b2b.jdhome.service.OrderCompletedService;
import com.kkl.kklplus.b2b.jdhome.service.SysLogService;
import com.kkl.kklplus.b2b.jdhome.utils.JdHomeUtils;
import com.kkl.kklplus.common.exception.MSErrorCode;
import com.kkl.kklplus.common.response.MSResponse;
import com.kkl.kklplus.entity.jdhome.JdHomeOrderFinish;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/orderCompleted")
public class OrderCompletedController {


    @Autowired
    private OrderCompletedService orderCompletedService;

    @Autowired
    private SysLogService sysLogService;

    /**
     * 工单完成
     */
    @PostMapping("/completed")
    public MSResponse completed(@RequestBody JdHomeOrderFinish jdHomeOrderFinish) {
        try {
            return orderCompletedService.transformation(jdHomeOrderFinish);
        } catch (Exception e) {
            log.error("完成工单失败:{}", e.getMessage());
            sysLogService.insert(1L, JdHomeUtils.toJson(jdHomeOrderFinish),
                    e.getMessage(),"完成工单失败", "completed", "POST");
            return new MSResponse<>(new MSErrorCode(1000, StringUtils.left(e.getMessage(),255)));
        }
    }
}
