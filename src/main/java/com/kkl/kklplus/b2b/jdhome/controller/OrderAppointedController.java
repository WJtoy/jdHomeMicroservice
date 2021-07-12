package com.kkl.kklplus.b2b.jdhome.controller;

import com.jd.open.api.sdk.domain.jjfw.OrderProcessService.response.bookOndoor.ResultInfo;

import com.jd.open.api.sdk.request.jjfw.HomefwTaskBookOndoorRequest;

import com.jd.open.api.sdk.response.jjfw.HomefwTaskBookOndoorResponse;
import com.kkl.kklplus.b2b.jdhome.entity.*;

import com.kkl.kklplus.b2b.jdhome.service.B2BProcesslogService;
import com.kkl.kklplus.b2b.jdhome.service.OrderAppointedService;
import com.kkl.kklplus.b2b.jdhome.service.OrderCompletedService;
import com.kkl.kklplus.b2b.jdhome.service.SysLogService;
import com.kkl.kklplus.b2b.jdhome.utils.DateUtils;
import com.kkl.kklplus.b2b.jdhome.utils.JdHomeUtils;

import com.kkl.kklplus.b2b.jdhome.utils.QuarterUtils;
import com.kkl.kklplus.common.exception.MSErrorCode;
import com.kkl.kklplus.common.response.MSResponse;

import com.kkl.kklplus.entity.b2b.common.B2BProcessFlag;
import com.kkl.kklplus.entity.b2bcenter.rpt.B2BOrderProcesslog;

import com.kkl.kklplus.entity.jdhome.JdHomeOrderAppointed;
import com.kkl.kklplus.entity.jdhome.JdHomeOrderFinish;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;


/**
 * @Auther wj
 * @Date 2021/2/4 13:53
 */

@Slf4j
@RestController
@RequestMapping("/orderAppointed")
public class OrderAppointedController {


    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private OrderAppointedService orderAppointedService;

    @Autowired
    private B2BProcesslogService b2BProcesslogService;

    /**
     * 预约工单
     * @param
     * @return
     */
    @PostMapping("/appointed")
    public MSResponse appointed(@RequestBody JdHomeOrderAppointed jdHomeOrderAppointed) {
        MSResponse msResponse = new MSResponse(MSErrorCode.SUCCESS);
        HomefwTaskBookOndoorRequest homefwTaskBookOndoorRequest = new HomefwTaskBookOndoorRequest();
        B2BOrderProcesslog processlog = new B2BOrderProcesslog();
        processlog.setB2bOrderNo(jdHomeOrderAppointed.getOrderNo());
        processlog.setInterfaceName(homefwTaskBookOndoorRequest.getApiMethod());
        processlog.setCreateById(jdHomeOrderAppointed.getCreateById());
        processlog.setUpdateById(jdHomeOrderAppointed.getCreateById());
        processlog.preInsert();
        processlog.setQuarter(QuarterUtils.getQuarter(processlog.getCreateDt()));

        try {
            homefwTaskBookOndoorRequest.setBookDate(DateUtils.formatDateTime
                    (new Date(jdHomeOrderAppointed.getBookDate())));
            homefwTaskBookOndoorRequest.setOrderNo(jdHomeOrderAppointed.getOrderNo());
            homefwTaskBookOndoorRequest.setMasterPhone(jdHomeOrderAppointed.getMasterPhone());
            homefwTaskBookOndoorRequest.setOperateTime
                    (DateUtils.formatDateTime
                            (new Date(jdHomeOrderAppointed.getOperateTime())));
            homefwTaskBookOndoorRequest.setMasterName(jdHomeOrderAppointed.getMasterName());
            homefwTaskBookOndoorRequest.setRemark(jdHomeOrderAppointed.getRemarks());
            processlog.setInfoJson(homefwTaskBookOndoorRequest.getAppJsonParams());
            b2BProcesslogService.insert(processlog);
            orderAppointedService.insert(jdHomeOrderAppointed);
            HomefwTaskBookOndoorResponse response =  orderAppointedService.executeJdAPI(homefwTaskBookOndoorRequest);
            processlog.setResultJson(response.getMsg());

            ResultInfo resultInfo = response.getResultInfo();
            if ("0".equals(response.getCode()) && resultInfo != null &&100 == resultInfo.getResultCode()){
                processlog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_SUCESS.value);
                jdHomeOrderAppointed.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_SUCESS.value);
            }else {
                String errorMsg = "";
                if( resultInfo != null ){
                    errorMsg = response.getResultInfo().getErrMsg();
                }else {
                    errorMsg = response.getZhDesc();
                }
                String code = response.getCode();
                if(StringUtils.isNumeric(code)){
                    long codeint = Long.valueOf(code);
                    if(codeint > 60 && codeint < 93
                            && codeint != 76 && codeint != 81
                            && codeint != 82 && codeint != 84){
                        msResponse.setMsg(errorMsg);
                        msResponse.setCode(10000);
                    }
                }
                processlog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_FAILURE.value);
                jdHomeOrderAppointed.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_FAILURE.value);
                processlog.setProcessComment(errorMsg);
                jdHomeOrderAppointed.setProcessComment(errorMsg);
            }
            b2BProcesslogService.updateProcessFlag(processlog);
            orderAppointedService.updateProcessFlag(jdHomeOrderAppointed);
            return msResponse;
        } catch (Exception e) {
            log.error("预约工单失败:{}", e.getMessage(),e);
            sysLogService.insert(1L, JdHomeUtils.toJson(jdHomeOrderAppointed),
                     e.getMessage(),"预约工单失败", "appointed", "POST");
            return new MSResponse<>(new MSErrorCode(1000, StringUtils.left(e.getMessage(),255)));
        }
    }








}
