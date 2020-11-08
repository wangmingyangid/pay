package org.wmy.pay.config;

import com.lly835.bestpay.config.AliPayConfig;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.service.BestPayService;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wmy
 * @create 2020-11-05 21:20
 */

@Configuration
public class BestPayConfig {
    @Autowired
    private WxAccountConfig wxAccountConfig;
    @Autowired
    private AlipayAccountConfig alipayAccountConfig;

    /**
     *
     * @param wxPayConfig 会自动从容器中注入，spring 会下执行创建 WxPayConfig 的代码
     * @return
     */
    @Bean
    public BestPayService create(WxPayConfig wxPayConfig){
        //对支付宝支付进行配置
        AliPayConfig aliPayConfig = new AliPayConfig();
        aliPayConfig.setAppId(alipayAccountConfig.getAppId());
        //设置商户应用私钥：用于支付
        aliPayConfig.setPrivateKey(alipayAccountConfig.getPrivateKey());
        //设置支付宝公钥：用于通知
        aliPayConfig.setAliPayPublicKey(alipayAccountConfig.getPublicKey());
        //设置异步通知地址
        aliPayConfig.setNotifyUrl(alipayAccountConfig.getNotifyUrl());
        //设置跳转链接
        aliPayConfig.setReturnUrl(alipayAccountConfig.getReturnUrl());

        //核心
        BestPayServiceImpl bestPayService = new BestPayServiceImpl();
        bestPayService.setWxPayConfig(wxPayConfig);
        bestPayService.setAliPayConfig(aliPayConfig);

        return bestPayService;
    }

    /**
     * 把微信配置提出来的目的是，在payController中会用到，避免写重复的代码
     * @return
     */
    @Bean
    public WxPayConfig wxPayConfig() {
        WxPayConfig wxPayConfig = new WxPayConfig();

        //对微信支付进行配置，可参考WxPayConfig
        //公众帐号appId
        wxPayConfig.setAppId(wxAccountConfig.getAppId());
        //商户号
        wxPayConfig.setMchId(wxAccountConfig.getMchID());
        //商户密钥
        wxPayConfig.setMchKey(wxAccountConfig.getMchKey());
        //接受支付平台异步通知的；必须设置成外网可以访问的url（内网或家庭公网是不可以的，云服务器是可以的）
        //比如，微信给这个地址发送通知
        wxPayConfig.setNotifyUrl(wxAccountConfig.getNotifyUrl());
        //设置跳转地址
        wxPayConfig.setReturnUrl(wxAccountConfig.getReturnUrl());

        return wxPayConfig;
    }
}
