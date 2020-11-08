package org.wmy.pay.controller;

import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.wmy.pay.dao.PayInfoMapper;
import org.wmy.pay.pojo.PayInfo;
import org.wmy.pay.service.impl.PayService;

import javax.xml.ws.Response;
import java.math.BigDecimal;
import java.util.HashMap;

/**
 * @author wmy
 * @create 2020-11-05 19:17
 */

@Controller
@RequestMapping("/pay")
@Slf4j
public class PayController {

    @Autowired
    private PayService payService;
    @Autowired
    private WxPayConfig wxPayConfig;

    @GetMapping("/create")
    public ModelAndView create(@RequestParam("orderId") String orderId,
                               @RequestParam("amount") BigDecimal amount,
                               @RequestParam("payType")BestPayTypeEnum bestPayTypeEnum){

        //对支付方式进行校验，本系统只支持微信NATIVE支付和支付宝网页支付
        if(BestPayTypeEnum.WXPAY_NATIVE != bestPayTypeEnum &&
                BestPayTypeEnum.ALIPAY_PC != bestPayTypeEnum){
            throw new RuntimeException("不支持的支付方式");
        }

        PayResponse payResponse = payService.create(orderId, amount,bestPayTypeEnum);

        //支付方式不同，渲染就不同；WXPAY_NATIVE 使用codeUrl, ALIPAY_PC 使用body
        HashMap<String,String> map = new HashMap<>();
        if(BestPayTypeEnum.WXPAY_NATIVE == bestPayTypeEnum){
            //从支付响应中拿到二维码/订单号/跳转地址
            map.put("codeUrl",payResponse.getCodeUrl());
            map.put("orderId",orderId);
            map.put("returnUrl",wxPayConfig.getReturnUrl());
            return new ModelAndView("createForWxNative",map);
        }
        //从支付响应中拿到表单数据
        map.put("body",payResponse.getBody());
        return new ModelAndView("createForAliPayPc",map);


    }

    @PostMapping("/notify")
    @ResponseBody
    public String asyncNotify(@RequestBody String notifyData){
        return payService.asyncNotify(notifyData);
    }

    @GetMapping("queryByOrderId")
    @ResponseBody
    public PayInfo queryByOrderId(@RequestParam String orderId) {
        return payService.queryByOrderId(orderId);
    }
}
