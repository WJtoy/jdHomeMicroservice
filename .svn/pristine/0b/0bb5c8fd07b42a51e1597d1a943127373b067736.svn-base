package com.kkl.kklplus.b2b.jdhome.service;

import com.jd.open.api.sdk.domain.jjfw.OutinterfaceCloseorderService.response.close.ColseOrder;
import com.kkl.kklplus.b2b.jdhome.entity.OrderClose;
import com.kkl.kklplus.b2b.jdhome.entity.OrderInfo;
import com.kkl.kklplus.b2b.jdhome.mapper.OrderCloseMapper;
import com.kkl.kklplus.b2b.jdhome.mapper.OrderInfoMapper;
import com.kkl.kklplus.b2b.jdhome.mq.sender.B2BCenterOrderProcessMQSend;
import com.kkl.kklplus.b2b.jdhome.utils.QuarterUtils;
import com.kkl.kklplus.entity.b2b.common.B2BProcessFlag;
import com.kkl.kklplus.entity.b2bcenter.md.B2BDataSourceEnum;
import com.kkl.kklplus.entity.b2bcenter.pb.MQB2BOrderProcessMessage;
import com.kkl.kklplus.entity.b2bcenter.sd.B2BOrderActionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Slf4j
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class OrderCloseService {

    @Resource
    private OrderCloseMapper orderCloseMapper;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private B2BCenterOrderProcessMQSend orderProcessMQSend;

    public void closeOrderProcess(ColseOrder closeOrder) {
        OrderInfo orderInfo = orderInfoService.getOrderByOrderNo(closeOrder.getOrderNo());
        OrderClose orderClose = transformEntity(closeOrder);
        if(orderInfo != null) {
            Integer orderStatus = orderInfo.getOrderStatus();
            if(orderStatus == 100){
                return;
            }
            this.insert(orderClose);
            Long kklOrderId = orderInfo.getKklOrderId();
            if( kklOrderId != null && kklOrderId > 0){
                MQB2BOrderProcessMessage.B2BOrderProcessMessage processMessage =
                        MQB2BOrderProcessMessage.B2BOrderProcessMessage.newBuilder()
                                .setMessageId(orderClose.getId())
                                .setB2BOrderNo(orderClose.getOrderNo())
                                .setRemarks(orderClose.getReason())
                                .setKklOrderId(kklOrderId)
                                .setB2BOrderId(orderInfo.getId())
                                .setDataSource(B2BDataSourceEnum.JD_HOME.id)
                                .setActionType(B2BOrderActionEnum.CONVERTED_CANCEL.value).build();
                orderProcessMQSend.send(processMessage);
            }
            orderInfoService.updateOrderStatus(100, orderInfo.getId());
            orderClose.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_SUCESS.value);
        }else{
            this.insert(orderClose);
            orderClose.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_FAILURE.value);
            orderClose.setProcessComment("没有找到对应工单！");
        }
        this.updateProcessResult(orderClose);
    }

    public Integer updateProcessResult(OrderClose orderClose) {
        orderClose.preUpdate();
        return orderCloseMapper.updateProcessResult(orderClose);
    }

    public Integer insert(OrderClose orderClose) {
        return orderCloseMapper.insert(orderClose);
    }

    private OrderClose transformEntity(ColseOrder closeOrder) {
        OrderClose orderClose = new OrderClose();
        orderClose.setOrderNo(closeOrder.getOrderNo());
        orderClose.setReason(closeOrder.getReason());
        orderClose.preInsert();
        orderClose.setQuarter(QuarterUtils.getQuarter(orderClose.getCreateDate()));
        orderClose.setCreateById(1L);
        return orderClose;
    }
}
