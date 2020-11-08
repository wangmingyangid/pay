package org.wmy.pay.service;

import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;
import org.wmy.pay.pojo.PayInfo;

import java.math.BigDecimal;

/**
 * @author wmy
 * @create 2020-11-04 21:10
 */
public interface IPayService {
    //创建或发起支付
    PayResponse create(String orderId, BigDecimal amount, BestPayTypeEnum bestPayTypeEnum);

    //异步通知处理
    String asyncNotify(String notifyData);

    //查询支付记录
    PayInfo queryByOrderId(String orderId);
}
