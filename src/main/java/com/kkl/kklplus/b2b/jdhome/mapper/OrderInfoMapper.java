package com.kkl.kklplus.b2b.jdhome.mapper;


import com.github.pagehelper.Page;
import com.kkl.kklplus.b2b.jdhome.entity.OrderInfo;
import com.kkl.kklplus.entity.b2bcenter.sd.B2BOrderSearchModel;
import com.kkl.kklplus.entity.b2bcenter.sd.B2BOrderTransferResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderInfoMapper {


    Integer insert(OrderInfo newOrderInfo);
    /**
     * 获取未转工单列表
     * @param orderSearchModel
     * @return
     */
    Page<OrderInfo> getList(B2BOrderSearchModel orderSearchModel);

    /**
     *查询需转工单的状态
     * @param orderTransferResults
     * @return
     */
    List<OrderInfo> findOrdersProcessFlag(@Param("orderTransferResults") List<B2BOrderTransferResult> orderTransferResults);

    /**
     * 更新转换结果
     * @param orderInfo
     * @return
     */
    Integer updateTransferResult(OrderInfo orderInfo);

    /**
     * 获取ID
     * @param orderNo
     * @return
     */
    Long getIdByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 获取工单信息
     * @param orderNo
     * @return
     */
    OrderInfo getOrderByOrderNo(@Param("orderNo")String orderNo);

    /**
     * 更新状态值
     * @param status
     * @param id
     * @return
     */
    Integer updateOrderStatus(@Param("status") int status, @Param("id") Long id);

    /**
     * 取消转单
     * @param transferResult
     * @return
     */
    Integer cancelledOrder(B2BOrderTransferResult transferResult);

    /**
     * 根据ID获取工单信息
     * @param b2bOrderId
     * @return
     */
    OrderInfo getOrderById(@Param("id") Long b2bOrderId);
}
