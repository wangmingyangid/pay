package org.wmy.pay.service.impl;

import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.BestPayService;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wmy.pay.dao.PayInfoMapper;
import org.wmy.pay.enums.PayPlatformEnum;
import org.wmy.pay.pojo.PayInfo;
import org.wmy.pay.service.IPayService;

import java.math.BigDecimal;

/**
 * @author wmy
 * @create 2020-11-04 21:12
 */

@Service
@Slf4j
public class PayService implements IPayService {

    @Autowired
    BestPayService bestPayService;
    @Autowired
    PayInfoMapper payInfoMapper;

    @Override
    public PayResponse create(String orderId, BigDecimal amount,BestPayTypeEnum bestPayTypeEnum) {

        //1.写入数据库
        PayInfo payInfo = new PayInfo(Long.parseLong(orderId),
                PayPlatformEnum.getByBestPayTypeEnum(bestPayTypeEnum).getCode(),
                OrderStatusEnum.NOTPAY.name(),
                amount);
        payInfoMapper.insertSelective(payInfo);

        PayRequest payRequest = new PayRequest();
        //订单名称
        payRequest.setOrderName("4388070-最好的支付sdk");
        //订单号
        payRequest.setOrderId(orderId);
        //金额
        payRequest.setOrderAmount(amount.doubleValue());
        //支付方式
        payRequest.setPayTypeEnum(bestPayTypeEnum);

        PayResponse payResponse = bestPayService.pay(payRequest);
        log.info("发起支付 payResponse{}",payResponse);

        return payResponse;
    }

    /**
     *
     * 异步通知处理
     *
     *
     */
    @Override
    public String asyncNotify(String notifyData) {
        //1.签名校验
        PayResponse payResponse = bestPayService.asyncNotify(notifyData);
        log.info("异步通知 payResponse{}",payResponse);

        //2.金额校验（从数据库查金额）
        //出现比较严重的情况时，要发出告警
        PayInfo payInfo = payInfoMapper.selectByOrderNo(Long.parseLong(payResponse.getOrderId()));
        if(payInfo == null){
            //告警
            throw new RuntimeException("通过订单："+payResponse.getOrderId()+"查询到的结果时null");
        }
        //如果订单状态不是已支付
        if(!payInfo.getPlatformStatus().equals(OrderStatusEnum.SUCCESS.name())) {
            if(payInfo.getPayAmount().compareTo(BigDecimal.valueOf(payResponse.getOrderAmount())) != 0) {
                //告警
                throw new RuntimeException("异步通知中的金额和数据库中的不一致,orderNo"+payResponse.getOrderId());
            }
            //3.修改订单支付状态
            payInfo.setPlatformStatus(OrderStatusEnum.SUCCESS.name());
            //设置交易流水
            payInfo.setPlatformNumber(payResponse.getOutTradeNo());
            //此出更新时，数据库中的更新时间是不会变化的，因为我们是直接查出来的更新时间，然后又塞进去了，此时
            //mysql不会帮我们跟新时间了，所以可以删除掉mapper.xml中的if updateTime 这个判断，交由mysql帮我们管理更新时间
            payInfoMapper.updateByPrimaryKeySelective(payInfo);
        }

        //TODO 4.pay 系统发送消息都MQ，mall 系统接受MQ消息

        //5.告诉微信/支付宝不要在通知了
        if(payResponse.getPayPlatformEnum() == BestPayPlatformEnum.WX){
            return "<xml>\n" +
                    "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                    "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                    "</xml>";
        }else if (payResponse.getPayPlatformEnum() == BestPayPlatformEnum.ALIPAY){
            return "success";
        }
        throw new RuntimeException("异步通知出现错误");
    }

    @Override
    public PayInfo queryByOrderId(String orderId) {
        return payInfoMapper.selectByOrderNo(Long.parseLong(orderId));
    }
}
