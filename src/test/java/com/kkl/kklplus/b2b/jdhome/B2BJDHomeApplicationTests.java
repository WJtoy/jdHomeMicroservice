package com.kkl.kklplus.b2b.jdhome;

import com.kkl.kklplus.b2b.jdhome.controller.OrderAppointedController;
import com.kkl.kklplus.b2b.jdhome.mapper.OrderAppointedMapper;
import com.kkl.kklplus.b2b.jdhome.service.OrderAppointedService;
import com.kkl.kklplus.entity.jdhome.JdHomeOrderAppointed;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class B2BJDHomeApplicationTests {

    @Autowired
    private OrderAppointedController orderAppointedController;
    @Autowired
    private OrderAppointedMapper orderAppointedMapper;

    @Autowired
    private OrderAppointedService orderAppointedService;

    @Test
    public void test1(){
        JdHomeOrderAppointed jdHomeOrderAppointed = new JdHomeOrderAppointed();
        jdHomeOrderAppointed.setBookDate(1612504441834L);
        jdHomeOrderAppointed.setOperateTime(1612504441834L);
        jdHomeOrderAppointed.setOrderNo("213");
        jdHomeOrderAppointed.setB2bOrderId(1232L);
        jdHomeOrderAppointed.setMasterName("王");
        jdHomeOrderAppointed.setMasterPhone("13751302397");
        orderAppointedService.insert(jdHomeOrderAppointed);
    }
    @Test
    public void test2(){


    }

}
