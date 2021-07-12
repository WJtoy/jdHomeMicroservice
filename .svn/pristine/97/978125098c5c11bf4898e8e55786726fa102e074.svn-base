package com.kkl.kklplus.b2b.jdhome.controller;


import com.github.pagehelper.Page;
import com.google.gson.Gson;
import com.jd.open.api.sdk.domain.jjfw.OutinterfaceCloseorderService.response.close.ColseOrder;
import com.jd.open.api.sdk.domain.jjfw.RequestOrderService.response.search.FwTast;
import com.jd.open.api.sdk.domain.jjfw.RequestOrderService.response.search.ResultInfo;
import com.jd.open.api.sdk.request.jjfw.*;
import com.jd.open.api.sdk.response.jjfw.*;
import com.kkl.kklplus.b2b.jdhome.config.B2BJdProperties;
import com.kkl.kklplus.b2b.jdhome.entity.OrderInfo;
import com.kkl.kklplus.b2b.jdhome.mq.sender.B2BOrderMQSender;
import com.kkl.kklplus.b2b.jdhome.service.B2BProcesslogService;
import com.kkl.kklplus.b2b.jdhome.service.OrderCloseService;
import com.kkl.kklplus.b2b.jdhome.service.OrderInfoService;
import com.kkl.kklplus.b2b.jdhome.service.SysLogService;
import com.kkl.kklplus.b2b.jdhome.utils.DateUtils;
import com.kkl.kklplus.b2b.jdhome.utils.JdHomeUtils;
import com.kkl.kklplus.b2b.jdhome.utils.QuarterUtils;
import com.kkl.kklplus.common.exception.MSErrorCode;
import com.kkl.kklplus.common.response.MSResponse;
import com.kkl.kklplus.entity.b2b.common.B2BProcessFlag;
import com.kkl.kklplus.entity.b2bcenter.md.B2BDataSourceEnum;
import com.kkl.kklplus.entity.b2bcenter.md.B2BShopEnum;
import com.kkl.kklplus.entity.b2bcenter.pb.MQB2BOrderMessage;
import com.kkl.kklplus.entity.b2bcenter.rpt.B2BOrderProcesslog;
import com.kkl.kklplus.entity.b2bcenter.sd.B2BOrder;
import com.kkl.kklplus.entity.b2bcenter.sd.B2BOrderSearchModel;
import com.kkl.kklplus.entity.b2bcenter.sd.B2BOrderTransferResult;
import com.kkl.kklplus.entity.common.MSPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/orderInfo")
public class OrderInfoController {

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private OrderCloseService orderCloseService;

    @Autowired
    private B2BProcesslogService b2BProcesslogService;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private B2BJdProperties jdProperties;

    @Autowired
    private B2BOrderMQSender b2BOrderMQSender;

    //region JOB

    /**
     * 抓取工单JOB
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void orderGetJob(){
        B2BJdProperties.AuthorizationConfig config = jdProperties.getAuthorizationConfig();
        if (config.getScheduleEnabled()) {
            this.getOrder();
        }
    }

    /**
     * 获取工单
     */
    private void getOrder() {
        HomefwTaskSearchRequest request = new HomefwTaskSearchRequest();
        request.setPage(1);
        request.setPageSize(200);
        B2BOrderProcesslog processlog = new B2BOrderProcesslog();
        processlog.setInterfaceName(request.getApiMethod());
        processlog.setCreateById(1L);
        processlog.setUpdateById(1L);
        processlog.preInsert();
        processlog.setQuarter(QuarterUtils.getQuarter(processlog.getCreateDt()));
        try {
            processlog.setInfoJson(request.getAppJsonParams());
            b2BProcesslogService.insert(processlog);
            HomefwTaskSearchResponse response = orderInfoService.executeJdAPI(request);
            processlog.setResultJson(response.getMsg());
            ResultInfo resultInfo = response.getResultInfo();
            if("0".equals(response.getCode()) && resultInfo != null && 100 == resultInfo.getResultCode()){
                processlog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_SUCESS.value);
                List<FwTast> fwTastList = resultInfo.getFwTastList();
                String orderNosStr = "";
                for(FwTast tast :fwTastList){
                    OrderInfo orderInfo = orderInfoService.tastToOrder(tast);
                    String orderNo = orderInfo.getOrderNo();
                    Long id = orderInfoService.getIdByOrderNo(orderInfo.getOrderNo());
                    if(id == null || id== 0){
                        orderInfoService.insert(orderInfo);
                        sendJDOrderMQ(orderInfo);
                    }
                    orderNosStr = orderNosStr.concat(orderNo).concat(",");
                }
                if(StringUtils.isNotBlank(orderNosStr)){
                    orderConfirm(orderNosStr);
                }
            }else{
                String errorMsg = "";
                if( resultInfo != null ){
                    errorMsg = resultInfo.getErrMsg();
                }else {
                    errorMsg = response.getZhDesc();
                }
                processlog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_FAILURE.value);
                processlog.setProcessComment(errorMsg);
            }
            b2BProcesslogService.updateProcessFlag(processlog);
        }catch (IOException e){
            String json = JdHomeUtils.toJson(request);
            log.error("抓取工单异常IOException:{}:{}",json,e.getMessage(),e);
            sysLogService.insert(1L, json,
                    e.getMessage(), "抓取工单异常IOException", request.getApiMethod(), "POST");
        }catch (Exception e){
            String json = JdHomeUtils.toJson(request);
            log.error("抓取工单异常Exception:{}:{}",json,e.getMessage(),e);
            sysLogService.insert(1L, json,e.getMessage(),
                    "抓取工单异常Exception", request.getApiMethod(), "POST");
        }
    }

    public void sendJDOrderMQ(OrderInfo orderInfo) {
        MQB2BOrderMessage.B2BOrderMessage.Builder builder = MQB2BOrderMessage.B2BOrderMessage.newBuilder()
                .setId(orderInfo.getId())
                .setDataSource(B2BDataSourceEnum.JD_HOME.id)
                .setShopId(orderInfo.getShopCode())
                .setOrderNo(orderInfo.getOrderNo())
                .setParentBizOrderId(orderInfo.getSaleOrderNo())
                .setUserName(orderInfo.getUserName())
                .setUserMobile(orderInfo.getUserMobile())
                .setUserAddress(orderInfo.getUserAddress())
                .setStatus(1)
                .setDescription(orderInfo.getRemark() != null ? orderInfo.getRemark() : "")
                .setQuarter(orderInfo.getQuarter());
        Long appointDate = orderInfo.getAppointDate();
        if (appointDate != null && appointDate > 3600000) {
            try {
                builder.setExpectServiceTime(DateUtils.formatDate(
                        DateUtils.timeStampToDate(appointDate
                        ), "yyyy年MM月dd日 HH:mm:ss"
                ));
            } catch (Exception el) {
                log.error("日期格式化失败！:{}:{}",orderInfo.getOrderNo(),appointDate);
            }
        }
        MQB2BOrderMessage.B2BOrderItem b2BOrderItem = MQB2BOrderMessage.B2BOrderItem.newBuilder()
                .setProductCode(orderInfo.getSku())
                .setProductName(orderInfo.getProductName() != null ? orderInfo.getProductName() : "")
                .setQty(1)
                .setWarrantyType("保内")
                .setServiceType(orderInfo.getServiceTypeCode())
                .build();
        builder.addB2BOrderItem(b2BOrderItem);
        MQB2BOrderMessage.B2BOrderMessage b2BOrderMessage = builder.build();
        //调用转单队列
        b2BOrderMQSender.send(b2BOrderMessage);
    }

    /**
     * 抓取已关闭的工单JOB
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void closeOrderJob(){
        B2BJdProperties.AuthorizationConfig config = jdProperties.getAuthorizationConfig();
        if (config.getCancelScheduleEnabled()) {
            this.getCloseOrder(1);
        }
    }

    private void getCloseOrder(int page) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long currentTimeMillis = System.currentTimeMillis();
        HomefwTaskCloseRequest request = new HomefwTaskCloseRequest();
        request.setPage(1);
        request.setPageSize(200);
        request.setBeginDate(sdf.format(new Date(currentTimeMillis-800000)));
        request.setEndDate(sdf.format(new Date(currentTimeMillis)));
        B2BOrderProcesslog processlog = new B2BOrderProcesslog();
        processlog.setInterfaceName(request.getApiMethod());
        processlog.setCreateById(1L);
        processlog.setUpdateById(1L);
        processlog.preInsert();
        processlog.setQuarter(QuarterUtils.getQuarter(processlog.getCreateDt()));
        try {
            processlog.setInfoJson(request.getAppJsonParams());
            b2BProcesslogService.insert(processlog);
            HomefwTaskCloseResponse response = orderInfoService.executeJdAPI(request);
            processlog.setResultJson(response.getMsg());
            com.jd.open.api.sdk.domain.jjfw.OutinterfaceCloseorderService.response.close.ResultInfo resultInfo = response.getResultInfo();
            if("0".equals(response.getCode()) && resultInfo != null && 100 == resultInfo.getResultCode()){
                processlog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_SUCESS.value);
                List<ColseOrder> closeOrderList = resultInfo.getCloseOrderList();
                for(ColseOrder colseOrder :closeOrderList){
                    orderCloseService.closeOrderProcess(colseOrder);
                }
                //若没查完，则继续查
                Integer pageSize = resultInfo.getPageSize();
                if(pageSize == 200){
                    getCloseOrder(page + 1);
                }
            }else{
                String errorMsg = "";
                if( resultInfo != null ){
                    errorMsg = resultInfo.getErrMsg();
                }else {
                    errorMsg = response.getZhDesc();
                }
                processlog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_FAILURE.value);
                processlog.setProcessComment(errorMsg);
            }
            b2BProcesslogService.updateProcessFlag(processlog);
        }catch (IOException e){
            String json = JdHomeUtils.toJson(request);
            log.error("抓取已关闭工单异常IOException:{}:{}",json,e.getMessage(),e);
            sysLogService.insert(1L, json,
                    e.getMessage(), "抓取已关闭工单异常IOException", request.getApiMethod(), "POST");
        }catch (Exception e){
            String json = JdHomeUtils.toJson(request);
            log.error("抓取已关闭工单异常Exception:{}:{}",json,e.getMessage(),e);
            sysLogService.insert(1L, json,e.getMessage(),
                    "抓取已关闭工单异常Exception", request.getApiMethod(), "POST");
        }
    }

    //endregion
    /**
     * 确认工单
     * @param orderNosStr
     */
    private void orderConfirm(String orderNosStr) {
        HomefwTaskPushHandleStatRequest request=new HomefwTaskPushHandleStatRequest();
        request.setOrderNos(orderNosStr);
        B2BOrderProcesslog processlog = new B2BOrderProcesslog();
        processlog.setInterfaceName(request.getApiMethod());
        processlog.setCreateById(1L);
        processlog.setUpdateById(1L);
        processlog.preInsert();
        processlog.setQuarter(QuarterUtils.getQuarter(processlog.getCreateDt()));
        try {
            processlog.setInfoJson(request.getAppJsonParams());
            b2BProcesslogService.insert(processlog);
            HomefwTaskPushHandleStatResponse response = orderInfoService.executeJdAPI(request);
            processlog.setResultJson(response.getMsg());
            com.jd.open.api.sdk.domain.jjfw.RequestOrderService.response.pushHandleStat.ResultInfo resultInfo = response.getResultInfo();
            if("0".equals(response.getCode()) && resultInfo != null &&100 == resultInfo.getResultCode()){
                processlog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_SUCESS.value);
            }else{
                String errorMsg = "";
                if( resultInfo != null ){
                    errorMsg = resultInfo.getErrMsg();
                }else {
                    errorMsg = response.getZhDesc();
                }
                processlog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_FAILURE.value);
                processlog.setProcessComment(errorMsg);
            }
            b2BProcesslogService.updateProcessFlag(processlog);
        }catch (IOException e){
            String json = JdHomeUtils.toJson(request);
            log.error("确认工单异常IOException:{}:{}",json,e.getMessage(),e);
            sysLogService.insert(1L, json,
                    e.getMessage(), "确认工单异常IOException", request.getApiMethod(), "POST");
        }catch (Exception e){
            String json = JdHomeUtils.toJson(request);
            log.error("确认工单异常Exception:{}:{}",json,e.getMessage(),e);
            sysLogService.insert(1L, json,
                    e.getMessage(),
                    "确认工单异常Exception", request.getApiMethod(), "POST");
        }
    }


    //region 操作模块
    /**
     * 获取工单(分页)
     * @param orderSearchModel
     * @return
     */
    @PostMapping("/getList")
    public MSResponse<MSPage<B2BOrder>> getList(@RequestBody B2BOrderSearchModel orderSearchModel) {
        try {
            Page<OrderInfo> orderInfoPage = orderInfoService.getList(orderSearchModel);
            Page<B2BOrder> customerPoPage = new Page<>();
            for(OrderInfo orderInfo:orderInfoPage){
                B2BOrder customerPo = new B2BOrder();
                customerPo.setId(orderInfo.getId());
                customerPo.setB2bOrderId(orderInfo.getId());
                customerPo.setDataSource(B2BDataSourceEnum.JD_HOME.id);
                customerPo.setOrderNo(orderInfo.getOrderNo());
                customerPo.setParentBizOrderId(orderInfo.getSaleOrderNo());
                customerPo.setShopId(orderInfo.getShopCode());
                customerPo.setUserName(orderInfo.getUserName());
                customerPo.setUserMobile(orderInfo.getUserMobile());
                customerPo.setUserAddress(orderInfo.getUserAddress());
                customerPo.setBrand(orderInfo.getBrandName());
                Long appointDate = orderInfo.getAppointDate();
                if (appointDate != null && appointDate > 3600000) {
                    try {
                        customerPo.setExpectServiceTime(DateUtils.formatDate(
                                DateUtils.timeStampToDate(appointDate
                                ), "yyyy年MM月dd日 HH:mm:ss"
                        ));
                    } catch (Exception el) {
                        log.error("日期格式化失败！:{}:{}",orderInfo.getOrderNo(),appointDate);
                    }
                }
                customerPo.setDescription(StringUtils.left(StringUtils.trimToEmpty(orderInfo.getRemark()), 200));
                customerPo.setProcessFlag(orderInfo.getProcessFlag());
                customerPo.setProcessTime(orderInfo.getProcessTime());
                customerPo.setProcessComment(orderInfo.getProcessComment());
                customerPo.setQuarter(orderInfo.getQuarter());
                B2BOrder.B2BOrderItem orderItem = new B2BOrder.B2BOrderItem();
                orderItem.setProductName(orderInfo.getProductName());
                orderItem.setProductCode(orderInfo.getSku());
                orderItem.setServiceType(orderInfo.getServiceTypeCode());
                orderItem.setWarrantyType("保内");
                orderItem.setQty(1);
                customerPo.getItems().add(orderItem);
                customerPoPage.add(customerPo);
            }
            MSPage<B2BOrder> returnPage = new MSPage<>();
            returnPage.setPageNo(orderInfoPage.getPageNum());
            returnPage.setPageSize(orderInfoPage.getPageSize());
            returnPage.setPageCount(orderInfoPage.getPages());
            returnPage.setRowCount((int) orderInfoPage.getTotal());
            returnPage.setList(customerPoPage.getResult());
            return new MSResponse<>(MSErrorCode.SUCCESS, returnPage);
        } catch (Exception e) {
            log.error("查询工单失败:{}", e.getMessage());
            sysLogService.insert(1L, JdHomeUtils.toJson(orderSearchModel),
                    e.getMessage(),"查询工单失败", "getList", "POST");
            return new MSResponse<>(new MSErrorCode(1000, StringUtils.left(e.getMessage(),255)));
        }
    }

    /**
     * 检查工单是否可以转换
     * @param orderNos
     * @return
     */
    @PostMapping("/checkWorkcardProcessFlag")
    public MSResponse checkWorkcardProcessFlag(@RequestBody List<B2BOrderTransferResult> orderNos){
        try {
            if(orderNos == null){
                return new MSResponse(new MSErrorCode(1000, "参数错误，工单编号不能为空"));
            }
            //查询出对应工单的状态
            List<OrderInfo> orderInfos = orderInfoService.findOrdersProcessFlag(orderNos);
            if(orderInfos == null){
                return new MSResponse(MSErrorCode.FAILURE);
            }
            for (OrderInfo orderInfo : orderInfos) {
                if (orderInfo.getOrderStatus() > 0 || orderInfo.getProcessFlag() >= B2BProcessFlag.PROCESS_FLAG_SUCESS.value) {
                    return new MSResponse(new MSErrorCode(1000, orderInfo.getOrderNo()+"工单已经转换成功或取消,不能转换"));
                }
            }
            return new MSResponse(MSErrorCode.SUCCESS);
        }catch (Exception e){
            log.error("检查工单失败:{}", e.getMessage());
            sysLogService.insert(1L,JdHomeUtils.toJson(orderNos),e.getMessage(),
                    "检查工单失败", "checkWorkcardProcessFlag", "POST");
            return new MSResponse(new MSErrorCode(1000, StringUtils.left(e.getMessage(),255)));
        }
    }

    /**
     * 更新转换结果
     * @param orderTransferResults
     * @return
     */
    @PostMapping("/updateTransferResult")
    public MSResponse updateTransferResult(@RequestBody List<B2BOrderTransferResult> orderTransferResults) {
        try {
            //根据数据源分组，在根据B2BOrderID分组
            Map<Long, B2BOrderTransferResult> b2bOrderIdMap = orderTransferResults.stream().collect(
                    Collectors.toMap(B2BOrderTransferResult::getB2bOrderId, Function.identity(),(key1, key2) -> key2));
            //查询出需要转换的工单
            List<OrderInfo> orderInfos = orderInfoService.findOrdersProcessFlag(orderTransferResults);
            //存放需要转换的工单集合
            List<OrderInfo> orders = new ArrayList<>();
            for(OrderInfo orderInfo :orderInfos){
                //如果工单为转换成功的才存放进工单集合
                if(orderInfo.getProcessFlag() != B2BProcessFlag.PROCESS_FLAG_SUCESS.value){
                    B2BOrderTransferResult transferResult = b2bOrderIdMap.get(orderInfo.getId());
                    if(transferResult != null){
                        orderInfo.setProcessFlag(transferResult.getProcessFlag());
                        orderInfo.setKklOrderId(transferResult.getOrderId());
                        orderInfo.setKklOrderNo(transferResult.getKklOrderNo());
                        orderInfo.setUpdateDt(transferResult.getUpdateDt());
                        orderInfo.setProcessComment(transferResult.getProcessComment());
                        orders.add(orderInfo);
                        if(transferResult.getProcessFlag() == B2BProcessFlag.PROCESS_FLAG_SUCESS.value){
                            acceptOrder(orderInfo);
                        }
                    }
                }
            }
            orderInfoService.updateTransferResult(orders);
            return new MSResponse(MSErrorCode.SUCCESS);
        } catch (Exception e) {
            log.error("工单转换失败:{}", e.getMessage());
            sysLogService.insert(1L,JdHomeUtils.toJson(orderTransferResults),
                     e.getMessage(),"工单转换失败", "updateTransferResult", "POST");
            return new MSResponse(new MSErrorCode(1000, StringUtils.left(e.getMessage(),255)));
        }
    }
    @PostMapping("/cancelOrderTransition")
    public MSResponse cancelOrderTransition(@RequestBody B2BOrderTransferResult transferResult) {
//        OrderInfo orderInfo = orderInfoService.getOrderById(transferResult.getB2bOrderId());
//        Integer processFlag = orderInfo.getProcessFlag();
//        if(orderInfo == null){
//            return new MSResponse(new MSErrorCode(1000, "没有找到对应工单信息！"));
//        }
//        if(orderInfo.getOrderStatus()== 100 || processFlag == 5){
//            return new MSResponse(MSErrorCode.SUCCESS);
//        }
//        if(processFlag == 4){
//            return new MSResponse(new MSErrorCode(1000, "工单已转，拒单失败！"));
//        }
//        HomefwTaskRefuseRequest request = new HomefwTaskRefuseRequest();
//        request.setSaleOrderNo(orderInfo.getSaleOrderNo());
//        request.setRefuseType(4);
//        B2BOrderProcesslog processlog = new B2BOrderProcesslog();
//        processlog.setB2bOrderNo(transferResult.getB2bOrderNo());
//        processlog.setInterfaceName(request.getApiMethod());
//        processlog.setCreateById(1L);
//        processlog.setUpdateById(1L);
//        processlog.preInsert();
//        processlog.setQuarter(QuarterUtils.getQuarter(processlog.getCreateDt()));
        try {
//            processlog.setInfoJson(request.getAppJsonParams());
//            b2BProcesslogService.insert(processlog);
//            HomefwTaskRefuseResponse response = orderInfoService.executeJdAPI(request);
//            processlog.setResultJson(response.getMsg());
//            com.jd.open.api.sdk.domain.jjfw.OutinterfaceCloseorderService.response.refuse.ResultInfo resultInfo = response.getReturnType();
//            if("0".equals(response.getCode()) && resultInfo != null && 100 == resultInfo.getResultCode()){
//                processlog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_SUCESS.value);
//                orderInfoService.cancelledOrder(transferResult);
//                b2BProcesslogService.updateProcessFlag(processlog);
//                return new MSResponse(MSErrorCode.SUCCESS);
//            }else{
//                String errorMsg = "";
//                if( resultInfo != null ){
//                    errorMsg = resultInfo.getErrMsg();
//                }else {
//                    errorMsg = response.getZhDesc();
//                }
//                errorMsg = StringUtils.left(errorMsg,220);
//                processlog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_FAILURE.value);
//                processlog.setProcessComment(errorMsg);
//                b2BProcesslogService.updateProcessFlag(processlog);
//                return new MSResponse(new MSErrorCode(1000, errorMsg));
//            }
            orderInfoService.cancelledOrder(transferResult);
            return new MSResponse(MSErrorCode.SUCCESS);
//        }catch (IOException e){
//            String json = JdHomeUtils.toJson(request);
//            log.error("拒单异常IOException:{}:{}",json,e.getMessage(),e);
//            sysLogService.insert(1L, json,
//                    e.getMessage(), "拒单异常IOException", request.getApiMethod(), "POST");
//            return new MSResponse(new MSErrorCode(1000, StringUtils.left(e.getMessage(),255)));
        }catch (Exception e){
            String json = JdHomeUtils.toJson(transferResult);
            log.error("拒单异常Exception:{}:{}",json,e.getMessage(),e);
            sysLogService.insert(1L, json,e.getMessage(),
                    "拒单异常Exception","cancelOrderTransition", "POST");
            return new MSResponse(new MSErrorCode(1000, StringUtils.left(e.getMessage(),255)));
        }
    }
    private void acceptOrder(OrderInfo orderInfo) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        HomefwTaskAcceptRequest request = new HomefwTaskAcceptRequest();
        request.setOrderNo(orderInfo.getOrderNo());
        request.setAcceptType(2);
        request.setHandleTime(sdf.format(new Date()));
        request.setIsOneOrder(2);
        B2BOrderProcesslog processlog = new B2BOrderProcesslog();
        processlog.setB2bOrderNo(orderInfo.getOrderNo());
        processlog.setInterfaceName(request.getApiMethod());
        processlog.setCreateById(1L);
        processlog.setUpdateById(1L);
        processlog.preInsert();
        processlog.setQuarter(QuarterUtils.getQuarter(processlog.getCreateDt()));
        try {
            processlog.setInfoJson(request.getAppJsonParams());
            b2BProcesslogService.insert(processlog);
            HomefwTaskAcceptResponse response = orderInfoService.executeJdAPI(request);
            processlog.setResultJson(response.getMsg());
            com.jd.open.api.sdk.domain.jjfw.OrderProcessService.response.accept.ResultInfo resultInfo = response.getResultInfo();
            if("0".equals(response.getCode()) && resultInfo != null && 100 == resultInfo.getResultCode()){
                processlog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_SUCESS.value);
            }else{
                String errorMsg = "";
                if( resultInfo != null ){
                    errorMsg = resultInfo.getErrMsg();
                }else {
                    errorMsg = response.getZhDesc();
                }
                errorMsg = StringUtils.left(errorMsg,220);
                processlog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_FAILURE.value);
                processlog.setProcessComment(errorMsg);
                orderInfo.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_FAILURE.value);
                orderInfo.setProcessComment(errorMsg);
            }
            b2BProcesslogService.updateProcessFlag(processlog);
        }catch (IOException e){
            String json = JdHomeUtils.toJson(request);
            log.error("回传工单异常IOException:{}:{}",json,e.getMessage(),e);
            sysLogService.insert(1L, json,
                    e.getMessage(), "回传工单异常IOException", request.getApiMethod(), "POST");
        }catch (Exception e){
            String json = JdHomeUtils.toJson(request);
            log.error("回传工单异常Exception:{}:{}",json,e.getMessage(),e);
            sysLogService.insert(1L, json,e.getMessage(),
                    "回传工单异常Exception", request.getApiMethod(), "POST");
        }
    }
    //endregion

}
