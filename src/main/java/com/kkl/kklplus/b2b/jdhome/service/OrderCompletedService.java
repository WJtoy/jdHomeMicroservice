package com.kkl.kklplus.b2b.jdhome.service;

import com.google.common.collect.Lists;
import com.jd.open.api.sdk.domain.jjfw.OrderProcessService.response.uploadFinishImg.ResultInfo;
import com.jd.open.api.sdk.request.jjfw.HomefwTaskFinishRequest;
import com.jd.open.api.sdk.request.jjfw.HomefwTaskUploadFinishImgRequest;
import com.jd.open.api.sdk.response.jjfw.HomefwTaskBookOndoorResponse;
import com.jd.open.api.sdk.response.jjfw.HomefwTaskFinishResponse;
import com.jd.open.api.sdk.response.jjfw.HomefwTaskUploadFinishImgResponse;
import com.kkl.kklplus.b2b.jdhome.entity.*;
import com.kkl.kklplus.b2b.jdhome.mapper.OrderCompletedMapper;
import com.kkl.kklplus.b2b.jdhome.mapper.OrderFinishPicMapper;
import com.kkl.kklplus.b2b.jdhome.mapper.PicUploadMapper;
import com.kkl.kklplus.b2b.jdhome.utils.DateUtils;
import com.kkl.kklplus.b2b.jdhome.utils.JdHomeUtils;
import com.kkl.kklplus.b2b.jdhome.utils.QuarterUtils;
import com.kkl.kklplus.common.exception.MSErrorCode;
import com.kkl.kklplus.common.response.MSResponse;
import com.kkl.kklplus.entity.b2b.common.B2BProcessFlag;
import com.kkl.kklplus.entity.b2bcenter.rpt.B2BOrderProcesslog;
import com.kkl.kklplus.entity.jdhome.JdHomeOrderFinish;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * @Auther wj
 * @Date 2021/2/4 16:09
 */
@Service
@Slf4j
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class OrderCompletedService extends B2BBaseService {

    @Autowired
    private B2BProcesslogService b2BProcesslogService;

    @Resource
    private OrderCompletedMapper orderCompletedMapper;

    @Autowired
    private OrderFinishPicService orderFinishPicService;

    @Autowired
    private PicUploadService picUploadService;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;



    public MSResponse transformation(JdHomeOrderFinish jdHomeOrderFinish) {
        MSResponse msResponse = orderComplete(jdHomeOrderFinish);    //京东完工
        threadPoolTaskExecutor.execute(() -> {
            List<PictureUpload> listPic = uploadPics(jdHomeOrderFinish);
            int size = listPic.size();
            if (listPic != null && size > 0) {
                OrderFinishPic orderFinishPic = new OrderFinishPic();
                orderFinishPic.setOrderNo(jdHomeOrderFinish.getOrderNo());
                orderFinishPic.setB2bOrderId(jdHomeOrderFinish.getB2bOrderId());
                orderFinishPic.setCreateById(jdHomeOrderFinish.getCreateById());
                if (size > 4) {
                    orderFinishPic.setAfterCompletion1(listPic.get(0).getJdPictureUrl());
                    orderFinishPic.setAfterCompletion2(listPic.get(1).getJdPictureUrl());
                    orderFinishPic.setAfterCompletion3(listPic.get(2).getJdPictureUrl());
                    orderFinishPic.setAfterCompletion4(listPic.get(3).getJdPictureUrl());
                    orderFinishPic.setBeforeCompletion1(listPic.get(4).getJdPictureUrl());
                    switch (size){
                        case 8:
                            orderFinishPic.setBeforeCompletion4(listPic.get(7).getJdPictureUrl());
                        case 7:
                            orderFinishPic.setBeforeCompletion3(listPic.get(6).getJdPictureUrl());
                        case 6:
                            orderFinishPic.setBeforeCompletion2(listPic.get(5).getJdPictureUrl());
                            break;
                    }
                } else {
                    orderFinishPic.setBeforeCompletion1(listPic.get(0).getJdPictureUrl());
                    orderFinishPic.setAfterCompletion1(listPic.get(0).getJdPictureUrl());
                    switch (size){
                        case 4:
                            orderFinishPic.setAfterCompletion4(listPic.get(3).getJdPictureUrl());
                        case 3:
                            orderFinishPic.setAfterCompletion3(listPic.get(2).getJdPictureUrl());
                        case 2:
                            orderFinishPic.setAfterCompletion2(listPic.get(1).getJdPictureUrl());
                            break;
                    }
                }
                taskUploadFinishImg(orderFinishPic);    //京东完工图片上传
            }
        });
        return msResponse;
    }

    /**
     * @param jdHomeOrderFinish
     * @return
     */
    private List<PictureUpload> uploadPics(JdHomeOrderFinish jdHomeOrderFinish) {
        AdsDspUploadPicRequest adsDspUploadPicRequest = new AdsDspUploadPicRequest();
        List<PictureUpload> listPic = Lists.newArrayList();
        try {
            for (String pic : jdHomeOrderFinish.getPics()) {
                PictureUpload pictureUpload = new PictureUpload();
                pictureUpload.setOrderNo(jdHomeOrderFinish.getOrderNo());
                pictureUpload.setB2bOrderId(jdHomeOrderFinish.getB2bOrderId());
                pictureUpload.setCreateById(jdHomeOrderFinish.getCreateById());
                pictureUpload.setKklPictureUrl(pic);
                byte[] bytes = doGetRequestForFile(pic);
                if(bytes == null){
                    pictureUpload.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_FAILURE.value);
                    pictureUpload.setProcessComment("图片下载失败");
                    picUploadService.updateProcessFlag(pictureUpload);
                    continue;
                }
                adsDspUploadPicRequest.setParam1(bytes);
                picUploadService.insert(pictureUpload);
                AdsDspUploadPicResponse adsDspUploadPicResponse = this.executeJdAPI(adsDspUploadPicRequest);
                AdsDspUploadPicResponse.ResultInfo resultInfo = adsDspUploadPicResponse.getReturnType();
                if ("0".equals(adsDspUploadPicResponse.getCode()) && resultInfo != null && resultInfo.getData() != null) {
                    pictureUpload.setJdPictureUrl(resultInfo.getData());
                    pictureUpload.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_SUCESS.value);
                    listPic.add(pictureUpload);
                } else {
                    String errorMsg = "";
                    if (resultInfo != null) {
                        errorMsg = adsDspUploadPicResponse.getReturnType().getErrorMessage();
                    } else {
                        errorMsg = adsDspUploadPicResponse.getZhDesc();
                    }
                    pictureUpload.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_FAILURE.value);
                    pictureUpload.setProcessComment(errorMsg);
                }
                picUploadService.updateProcessFlag(pictureUpload);
            }
        } catch (Exception e) {
            log.error("完成图片上传失败:{}", e.getMessage());
            String json = JdHomeUtils.toJson(adsDspUploadPicRequest);
            sysLogService.insert(1L, json, e.getMessage(),
                    "完成图片上传失败Exception", adsDspUploadPicRequest.getApiMethod(), "POST");
        }
        return listPic;
    }

    /**
     * 完工图片接口
     *
     * @param orderFinishPic
     */
    public void taskUploadFinishImg(OrderFinishPic orderFinishPic) {
        HomefwTaskUploadFinishImgRequest homefwTaskUploadFinishImgRequest = new HomefwTaskUploadFinishImgRequest();
        homefwTaskUploadFinishImgRequest.setBeforeCompletion1(orderFinishPic.getBeforeCompletion1());
        homefwTaskUploadFinishImgRequest.setBeforeCompletion2(orderFinishPic.getBeforeCompletion2() == null ? "" : orderFinishPic.getBeforeCompletion2());
        homefwTaskUploadFinishImgRequest.setBeforeCompletion3(orderFinishPic.getBeforeCompletion3() == null ? "" : orderFinishPic.getBeforeCompletion3());
        homefwTaskUploadFinishImgRequest.setBeforeCompletion4(orderFinishPic.getBeforeCompletion4() == null ? "" : orderFinishPic.getBeforeCompletion4());
        homefwTaskUploadFinishImgRequest.setAfterCompletion1(orderFinishPic.getAfterCompletion1());
        homefwTaskUploadFinishImgRequest.setAfterCompletion2(orderFinishPic.getAfterCompletion2() == null ? "" : orderFinishPic.getAfterCompletion2());
        homefwTaskUploadFinishImgRequest.setAfterCompletion3(orderFinishPic.getAfterCompletion3() == null ? "" : orderFinishPic.getAfterCompletion3());
        homefwTaskUploadFinishImgRequest.setAfterCompletion4(orderFinishPic.getAfterCompletion4() == null ? "" : orderFinishPic.getAfterCompletion4());
        homefwTaskUploadFinishImgRequest.setOrderNo(orderFinishPic.getOrderNo());
        B2BOrderProcesslog processlog = new B2BOrderProcesslog();
        processlog.setB2bOrderNo(orderFinishPic.getOrderNo());
        processlog.setInterfaceName(homefwTaskUploadFinishImgRequest.getApiMethod());
        processlog.setCreateById(orderFinishPic.getCreateById());
        processlog.setUpdateById(orderFinishPic.getCreateById());
        processlog.preInsert();
        processlog.setQuarter(QuarterUtils.getQuarter(processlog.getCreateDt()));
        try {
            processlog.setInfoJson(homefwTaskUploadFinishImgRequest.getAppJsonParams());
            b2BProcesslogService.insert(processlog);
            orderFinishPicService.insert(orderFinishPic);
            HomefwTaskUploadFinishImgResponse response = this.executeJdAPI(homefwTaskUploadFinishImgRequest);
            processlog.setResultJson(response.getMsg());
            ResultInfo resultInfo = response.getReturnType();
            if ("0".equals(response.getCode()) && resultInfo != null && 100 == resultInfo.getResultCode()) {
                processlog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_SUCESS.value);
                orderFinishPic.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_SUCESS.value);
            } else {
                String errorMsg = "";
                if (resultInfo != null) {
                    errorMsg = response.getReturnType().getErrMsg();
                } else {
                    errorMsg = response.getZhDesc();
                }
                processlog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_FAILURE.value);
                processlog.setProcessComment(errorMsg);
                orderFinishPic.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_FAILURE.value);
                orderFinishPic.setProcessComment(errorMsg);
            }
            b2BProcesslogService.updateProcessFlag(processlog);
            orderFinishPicService.updateProcessFlag(orderFinishPic);
        } catch (Exception e) {
            log.error("完成图片上传失败:{}", e.getMessage());
            String json = JdHomeUtils.toJson(homefwTaskUploadFinishImgRequest);
            sysLogService.insert(1L, json, e.getMessage(),
                    "完成图片上传失败Exception", homefwTaskUploadFinishImgRequest.getApiMethod(), "POST");
        }
    }

    public MSResponse orderComplete(JdHomeOrderFinish jdHomeOrderFinish) {
        MSResponse msResponse = new MSResponse(MSErrorCode.SUCCESS);
        HomefwTaskFinishRequest homefwTaskFinishRequest = new HomefwTaskFinishRequest();
        homefwTaskFinishRequest.setOrderNo(jdHomeOrderFinish.getOrderNo());
        homefwTaskFinishRequest.setOperateTime(DateUtils.formatDate(DateUtils.timeStampToDate(jdHomeOrderFinish.getOperateTime()), "yyyy-MM-dd HH:mm:ss"));
        B2BOrderProcesslog processlog = new B2BOrderProcesslog();
        processlog.setB2bOrderNo(jdHomeOrderFinish.getOrderNo());
        processlog.setInterfaceName(homefwTaskFinishRequest.getApiMethod());
        processlog.setCreateById(jdHomeOrderFinish.getCreateById());
        processlog.setUpdateById(jdHomeOrderFinish.getCreateById());
        processlog.preInsert();
        processlog.setQuarter(QuarterUtils.getQuarter(processlog.getCreateDt()));
        OrderCompleted orderCompleted = new OrderCompleted();
        orderCompleted.setOrderNo(jdHomeOrderFinish.getOrderNo());
        orderCompleted.setOperateTime(jdHomeOrderFinish.getOperateTime());
        orderCompleted.setB2bOrderId(jdHomeOrderFinish.getB2bOrderId());
        orderCompleted.setCreateById(jdHomeOrderFinish.getCreateById());
        orderCompleted.setUpdateById(jdHomeOrderFinish.getCreateById());
        orderCompleted.setCreateDt(jdHomeOrderFinish.getCreateDt());
        orderCompleted.setQuarter(QuarterUtils.getQuarter(jdHomeOrderFinish.getCreateDt()));
        try {
            processlog.setInfoJson(homefwTaskFinishRequest.getAppJsonParams());
            b2BProcesslogService.insert(processlog);
            orderCompletedMapper.insert(orderCompleted);
            HomefwTaskFinishResponse response = this.executeJdAPI(homefwTaskFinishRequest);
            processlog.setResultJson(response.getMsg());
            com.jd.open.api.sdk.domain.jjfw.OrderProcessService.response.finish.ResultInfo resultInfo = response.getResultInfo();
            if ("0".equals(response.getCode()) && resultInfo != null && 100 == resultInfo.getResultCode()) {
                processlog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_SUCESS.value);
                orderCompleted.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_SUCESS.value);
            } else {
                String errorMsg = "";
                if (resultInfo != null) {
                    errorMsg = response.getResultInfo().getErrMsg();
                } else {
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
                processlog.setProcessComment(errorMsg);
                orderCompleted.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_FAILURE.value);
                orderCompleted.setProcessComment(errorMsg);
            }
            b2BProcesslogService.updateProcessFlag(processlog);
            orderCompleted.preUpdate();
            orderCompletedMapper.updateProcessFlag(orderCompleted);
            return msResponse;
        } catch (Exception e) {
            log.error("完成工单失败:{}", e.getMessage());
            String json = JdHomeUtils.toJson(homefwTaskFinishRequest);
            sysLogService.insert(1L, json, e.getMessage(),
                    "抓取工单异常Exception", homefwTaskFinishRequest.getApiMethod(), "POST");
            return new MSResponse<>(new MSErrorCode(10000,StringUtils.left(e.getMessage(),250)));
        }
    }


    public final static byte[] doGetRequestForFile(String urlStr) {
        InputStream is = null;
        ByteArrayOutputStream os = null;
        byte[] buff = new byte[1024];
        int len = 0;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "plain/text;charset=utf-8");
            conn.setRequestProperty("charset", "utf-8");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setReadTimeout(1000);
            conn.connect();
            is = conn.getInputStream();
            os = new ByteArrayOutputStream();
            while ((len = is.read(buff)) != -1) {
                os.write(buff, 0, len);
            }
            return os.toByteArray();
        } catch (IOException e) {
            log.error("发起请求出现异常:", e);
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("【关闭流异常】");
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error("【关闭流异常】");
                }
            }
        }
    }

}
