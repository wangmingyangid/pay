package org.wmy.pay.enums;

import com.lly835.bestpay.enums.BestPayTypeEnum;
import lombok.Getter;

/**
 * @author wmy
 * @create 2020-11-06 21:10
 *
 * 使用Getter注解是为了自动生成get方法
 * 参见：https://blog.csdn.net/sunayn/article/details/85252507
 */

@Getter
public enum PayPlatformEnum {
    //1.支付宝 2.微信
    ALIPAY(1),
    WX(2),
    ;

    Integer code;
    PayPlatformEnum(Integer code) {
        this.code = code;
    }

    //for 循环写法优于 if-else
    public static PayPlatformEnum getByBestPayTypeEnum(BestPayTypeEnum bestPayTypeEnum){
        for(PayPlatformEnum payPlatformEnum : PayPlatformEnum.values()){
            if(bestPayTypeEnum.getPlatform().name().equals(payPlatformEnum.name())){
                return payPlatformEnum;
            }
        }
        throw new RuntimeException("错误的支付平台"+bestPayTypeEnum.name());
    }
}
