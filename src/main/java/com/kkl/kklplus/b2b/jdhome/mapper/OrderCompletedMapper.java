package com.kkl.kklplus.b2b.jdhome.mapper;

import com.kkl.kklplus.b2b.jdhome.entity.OrderCompleted;
import com.kkl.kklplus.b2b.jdhome.entity.OrderFinishPic;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Auther wj
 * @Date 2021/2/4 15:43
 */
@Mapper
public interface OrderCompletedMapper {

    Integer insert(OrderCompleted orderCompleted);
    void updateProcessFlag(OrderCompleted orderCompleted);


}
