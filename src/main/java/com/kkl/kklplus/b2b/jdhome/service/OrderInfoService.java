package com.kkl.kklplus.b2b.jdhome.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jd.open.api.sdk.domain.jjfw.RequestOrderService.response.search.FwTast;
import com.kkl.kklplus.b2b.jdhome.entity.OrderInfo;
import com.kkl.kklplus.b2b.jdhome.mapper.OrderInfoMapper;
import com.kkl.kklplus.b2b.jdhome.utils.QuarterUtils;
import com.kkl.kklplus.entity.b2bcenter.sd.B2BOrderSearchModel;
import com.kkl.kklplus.entity.b2bcenter.sd.B2BOrderTransferResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class OrderInfoService extends B2BBaseService{

    @Resource
    private OrderInfoMapper orderInfoMapper;

    /**
     * 获取未转工单列表
     * @param orderSearchModel
     * @return
     */
    public Page<OrderInfo> getList(B2BOrderSearchModel orderSearchModel) {
        if (orderSearchModel.getPage() != null) {
            PageHelper.startPage(orderSearchModel.getPage().getPageNo(), orderSearchModel.getPage().getPageSize());
            return orderInfoMapper.getList(orderSearchModel);
        }else {
            return null;
        }
    }

    /**
     * 查询工单处理状态
     * @param orderTransferResults
     * @return
     */
    public List<OrderInfo> findOrdersProcessFlag(List<B2BOrderTransferResult> orderTransferResults) {
        return orderInfoMapper.findOrdersProcessFlag(orderTransferResults);
    }

    /**
     * 更新转换结果
     * @param orders
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateTransferResult(List<OrderInfo> orders) {
        for(OrderInfo orderInfo:orders){
            orderInfo.preUpdate();
            orderInfoMapper.updateTransferResult(orderInfo);
        }
    }

    /**
     * 根据orderNo获取ID
     * @param orderNo
     * @return
     */
    public Long getIdByOrderNo(String orderNo) {
        return orderInfoMapper.getIdByOrderNo(orderNo);
    }

    /**
     * 新增工单
     * @param orderInfo
     * @return
     */
    public Integer insert(OrderInfo orderInfo) {
        orderInfo.setCreateById(1L);
        return orderInfoMapper.insert(orderInfo);
    }

    /**
     * 实体转换
     * @param tast
     * @return
     */
    public OrderInfo tastToOrder(FwTast tast) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderNo(tast.getOrderNo());
        orderInfo.setSaleOrderNo(tast.getSaleOrderNo());
        orderInfo.setServiceTypeName(tast.getServiceTypeName());
        orderInfo.setCompanyName(tast.getCompanyName());
        orderInfo.setBrandName(tast.getBrandName());
        orderInfo.setUserName(tast.getUserName());
        orderInfo.setUserProvince(tast.getUserProvince());
        orderInfo.setUserCity(tast.getUserCity());
        orderInfo.setUserCounty(tast.getUserCounty());
        orderInfo.setUserTown(tast.getUserTown());
        orderInfo.setUserAddress(tast.getUserAddress());
        orderInfo.setUserMobile(tast.getUserMobile());
        orderInfo.setAreaIdPath(tast.getAreaIdPath());
        orderInfo.setCompanyShopName(tast.getCompanyShopName());
        orderInfo.setRemark(tast.getRemark());
        orderInfo.setItemCatIdPath(tast.getItemCatIdPath());
        orderInfo.setItemCatNamePath(tast.getItemCatNamePath());
        orderInfo.setSku(tast.getSku());
        orderInfo.setSkuName(tast.getSkuName());
        orderInfo.setShopCode(tast.getShopCode());
        orderInfo.setProductName(tast.getProductName());
        orderInfo.setProductValue(tast.getProductValue());
        String serviceTypeCode = tast.getServiceTypeCode();
        orderInfo.setServiceTypeCode(serviceTypeCode != null ? serviceTypeCode : "");
        orderInfo.setBrandId(tast.getBrandId());
        orderInfo.setServiceItemName(tast.getServiceItemName());
        Date createDate = tast.getCreateDate();
        if(createDate != null) {
            orderInfo.setOrderCreateDate(createDate.getTime());
        }
        Date appointDate = tast.getAppointDate();
        if(appointDate != null){
            orderInfo.setAppointDate(appointDate.getTime());
        }
        orderInfo.preInsert();
        orderInfo.setCreateById(1L);
        orderInfo.setQuarter(QuarterUtils.getQuarter(orderInfo.getCreateDate()));
        return orderInfo;
    }

    /**
     * 获取工单信息
     * @param orderNo
     * @return
     */
    public OrderInfo getOrderByOrderNo(String orderNo) {
        return orderInfoMapper.getOrderByOrderNo(orderNo);
    }

    /**
     * 更新工单状态
     * @param status
     * @param id
     * @return
     */
    public Integer updateOrderStatus(int status, Long id) {
        return orderInfoMapper.updateOrderStatus(status,id);
    }

    public Integer cancelledOrder(B2BOrderTransferResult transferResult) {
        return orderInfoMapper.cancelledOrder(transferResult);
    }

    public OrderInfo getOrderById(Long b2bOrderId) {
        return orderInfoMapper.getOrderById(b2bOrderId);
    }
}
