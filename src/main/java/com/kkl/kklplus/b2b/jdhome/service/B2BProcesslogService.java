package com.kkl.kklplus.b2b.jdhome.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.gson.Gson;
import com.kkl.kklplus.b2b.jdhome.entity.SysLog;
import com.kkl.kklplus.b2b.jdhome.mapper.B2BProcesslogMapper;
import com.kkl.kklplus.b2b.jdhome.mapper.SysLogMapper;
import com.kkl.kklplus.b2b.jdhome.utils.JdHomeUtils;
import com.kkl.kklplus.b2b.jdhome.utils.QuarterUtils;
import com.kkl.kklplus.entity.b2bcenter.rpt.B2BOrderProcesslog;
import com.kkl.kklplus.entity.b2bcenter.rpt.B2BProcessLogSearchModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Slf4j
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class B2BProcesslogService {

    @Resource
    private B2BProcesslogMapper b2BProcesslogMapper;

    @Resource
    private SysLogMapper sysLogMapper;

    /**
     * 添加原始数据
     * @param b2BProcesslog
     */
    public void insert(B2BOrderProcesslog b2BProcesslog){
        if(StringUtils.isBlank(b2BProcesslog.getB2bOrderNo())){
            b2BProcesslog.setB2bOrderNo("");
        }
        b2BProcesslogMapper.insert(b2BProcesslog);
    }

    public void updateProcessFlag(B2BOrderProcesslog b2BProcesslog) {
        try{
            b2BProcesslog.preUpdate();
            b2BProcesslogMapper.updateProcessFlag(b2BProcesslog);
        }catch (Exception e) {
            log.error("原始数据结果修改错误:{},{}", e.getMessage(),b2BProcesslog.toString());
            SysLog sysLog = new SysLog();
            sysLog.setCreateDt(System.currentTimeMillis());
            sysLog.setType(1);
            sysLog.setCreateById(1L);
            sysLog.setParams(JdHomeUtils.toJson(b2BProcesslog));
            sysLog.setException( e.getMessage());
            sysLog.setTitle("原始数据结果修改错误");
            sysLog.setQuarter(QuarterUtils.getQuarter(sysLog.getCreateDt()));
            sysLogMapper.insert(sysLog);
        }
    }

    public Page<B2BOrderProcesslog> getList(B2BProcessLogSearchModel processLogSearchModel, String code) {
        if (processLogSearchModel.getPage() != null) {
            PageHelper.startPage(processLogSearchModel.getPage().getPageNo(), processLogSearchModel.getPage().getPageSize());
            return b2BProcesslogMapper.getList(processLogSearchModel,code);
        } else {
            return null;
        }
    }
}
