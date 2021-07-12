package com.kkl.kklplus.b2b.jdhome.mapper;

import com.kkl.kklplus.b2b.jdhome.entity.OrderFinishPic;
import com.kkl.kklplus.entity.b2bcenter.rpt.B2BOrderProcesslog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Auther wj
 * @Date 2021/2/4 16:35
 */
@Mapper
public interface OrderFinishPicMapper {


    Integer insert(OrderFinishPic orderFinishPic);

    void updateProcessFlag(OrderFinishPic orderFinishPic);
}
