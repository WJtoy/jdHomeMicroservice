package com.kkl.kklplus.b2b.jdhome.entity;

import com.kkl.kklplus.entity.b2b.common.B2BBase;
import lombok.Data;

@Data
public class OrderInfo extends B2BBase<OrderInfo>{
    private String kklOrderNo;
    private Long kklOrderId;

    private String orderNo;//任务工单号，服务平台单号，唯一
    private String saleOrderNo;//京东商城订单号
    private String serviceTypeName;//参考服务类型编码
    private String companyName;//商家名称
    private String brandName;//品牌名称
    private String userName;//用户姓名
    private String userMobile;//用户电话
    private String userProvince;//用户所在省
    private String userCity;//用户所在地级市
    private String userCounty;//用户所在区县
    private String userTown;//用户所在镇街
    private String userAddress;//用户所在详细地址
    private String areaIdPath;//镇级地区编码（省_市_区_镇）
    private String companyShopName;//京东商城店铺名称
    private String remark;//备注
    private Long orderCreateDate = 0L;//创建时间(java用Date类型接收,非java接收到的是时间戳,需转换)
    private String itemCatNamePath;//京东产品分类名称:1级_2级_3级
    private String itemCatIdPath;//京东产品分类id:1级_2级_3级
    private String sku;//京东Sku
    private String skuName;//京东Sku名称
    private String shopCode;//京东商家ID(店铺的唯一识别)
    private String productName;//商品属性名
    private String productValue;//商品属性值
    /**
     * 服务类型编码一共有7种,分别是:00(送货上门并安装),
     * 01(安装),02(测量),03(送货上门),04(送货上门安装
     * 并拆旧),05(站点自提),06(二次上门),07（拆旧）
     */
    private String serviceTypeCode;

    private Long appointDate = 0L;//订单指派时间
    private Integer brandId;//品牌id
    private String serviceItemName;//商城落地配服务名称

    private Integer orderStatus;
}
