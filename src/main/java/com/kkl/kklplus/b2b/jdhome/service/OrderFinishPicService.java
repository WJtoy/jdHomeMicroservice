package com.kkl.kklplus.b2b.jdhome.service;

import com.kkl.kklplus.b2b.jdhome.entity.OrderFinishPic;
import com.kkl.kklplus.b2b.jdhome.mapper.OrderFinishPicMapper;
import com.kkl.kklplus.b2b.jdhome.utils.QuarterUtils;
import com.kkl.kklplus.entity.b2b.common.B2BProcessFlag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Auther wj
 * @Date 2021/2/5 15:22
 */
@Service
@Slf4j
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class OrderFinishPicService {

    @Autowired
    private OrderFinishPicMapper orderFinishPicMapper;

    public Integer insert(OrderFinishPic orderFinishPic){
        orderFinishPic.setUpdateById(orderFinishPic.getCreateById());
        orderFinishPic.preInsert();
        orderFinishPic.setQuarter(QuarterUtils.getQuarter(orderFinishPic.getCreateDt()));
        return orderFinishPicMapper.insert(orderFinishPic);
    }

    public void updateProcessFlag(OrderFinishPic orderFinishPic){
        orderFinishPic.preUpdate();
        orderFinishPicMapper.updateProcessFlag(orderFinishPic);
    }
}
