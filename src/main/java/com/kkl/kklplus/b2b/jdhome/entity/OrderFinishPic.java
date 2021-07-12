package com.kkl.kklplus.b2b.jdhome.entity;

import com.kkl.kklplus.entity.b2b.common.B2BBase;
import lombok.Data;

@Data
public class OrderFinishPic extends B2BBase<OrderFinishPic>{
    private String orderNo;
    private String beforeCompletion1;
    private String beforeCompletion2;
    private String beforeCompletion3;
    private String beforeCompletion4;
    private String afterCompletion1;
    private String afterCompletion2;
    private String afterCompletion3;
    private String afterCompletion4;
}
