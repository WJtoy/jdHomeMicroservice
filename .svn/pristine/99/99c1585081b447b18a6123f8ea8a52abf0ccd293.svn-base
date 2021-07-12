package com.kkl.kklplus.b2b.jdhome.service;


import com.kkl.kklplus.b2b.jdhome.entity.SysLog;
import com.kkl.kklplus.b2b.jdhome.mapper.SysLogMapper;
import com.kkl.kklplus.b2b.jdhome.utils.QuarterUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
@Slf4j
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class SysLogService {

    @Resource
    private SysLogMapper sysLogMapper;

    /**
     * 添加异常日志
     * @param sysLog
     */
    @Transactional()
    public void insert(SysLog sysLog) {
        try {
            sysLogMapper.insert(sysLog);
        } catch (Exception e) {
            log.error("[sysLogMapper.insert] {}", e.getMessage());
        }
    }

    public void insert(Long createId, String params,String exception, String title,String uri,String method){
        SysLog sysLog = new SysLog();
        sysLog.setCreateDt(System.currentTimeMillis());
        sysLog.setType(1);
        sysLog.setCreateById(createId);
        sysLog.setRequestUri(uri);
        sysLog.setMethod(method);
        sysLog.setParams(params);
        sysLog.setException(exception);
        sysLog.setTitle(title);
        sysLog.setQuarter(QuarterUtils.getQuarter(new Date(sysLog.getCreateDt())));
        try {
            sysLogMapper.insert(sysLog);
        }catch (Exception e){
            log.error("报错信息记录失败:{}:{}",sysLog.toString(),e.getMessage());
        }
    }
}
