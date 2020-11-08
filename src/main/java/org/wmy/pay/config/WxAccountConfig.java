package org.wmy.pay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author wmy
 * @create 2020-11-07 10:33
 */

@Component
@ConfigurationProperties(prefix = "wx")
@Data
public class WxAccountConfig {
    private String appId;
    private String mchID;
    private String mchKey;
    private String notifyUrl;
    private String returnUrl;
}
