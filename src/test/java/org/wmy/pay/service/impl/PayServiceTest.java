package org.wmy.pay.service.impl;

import com.lly835.bestpay.enums.BestPayTypeEnum;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.wmy.pay.PayApplicationTests;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * @author wmy
 * @create 2020-11-04 21:29
 */
public class PayServiceTest extends PayApplicationTests {

    @Autowired
    PayService payService;

    @Test
    public void create() {
        //BigDecimal.valueOf(0.01)
        //new BigDecimal("0.01")  千万不要new BigDecimal(0.01)，精度会有问题
        payService.create("wmy01",BigDecimal.valueOf(0.01),BestPayTypeEnum.WXPAY_NATIVE);

    }
}