package com.kkl.kklplus.b2b.jdhome.service;

import com.kkl.kklplus.b2b.jdhome.mapper.OrderAppointedMapper;
import com.kkl.kklplus.b2b.jdhome.utils.QuarterUtils;
import com.kkl.kklplus.entity.b2b.common.B2BProcessFlag;
import com.kkl.kklplus.entity.jdhome.JdHomeOrderAppointed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Auther wj
 * @Date 2021/2/4 14:36
 */
@Service
@Slf4j
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class OrderAppointedService extends B2BBaseService {

    @Autowired
    private OrderAppointedMapper orderAppointedMapper;



    public Integer insert(JdHomeOrderAppointed jdHomeOrderAppointed){
        jdHomeOrderAppointed.setUpdateById(jdHomeOrderAppointed.getCreateById());
        jdHomeOrderAppointed.preInsert();
        jdHomeOrderAppointed.setQuarter(QuarterUtils.getQuarter(jdHomeOrderAppointed.getCreateDt()));
        return orderAppointedMapper.insert(jdHomeOrderAppointed);
    }


    public void updateProcessFlag(JdHomeOrderAppointed jdHomeOrderAppointed){
        orderAppointedMapper.updateProcessFlag(jdHomeOrderAppointed);
    }





}
