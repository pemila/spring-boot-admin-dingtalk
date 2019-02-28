package com.pemila.notify;

import de.codecentric.boot.admin.server.config.AdminServerNotifierAutoConfiguration;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 扩展AdminServerNotifierAutoConfiguration类
 * 加入钉钉通知的配置
 * @author 月在未央
 * @date 2019/2/27 17:51
 */
@Configuration
@AutoConfigureAfter({MailSenderAutoConfiguration.class})
public class CustomNotifierAutoConfiguration extends AdminServerNotifierAutoConfiguration {

    @Configuration
    @ConditionalOnProperty(prefix = "spring.boot.admin.notify.dingtalk", name = {"webhookToken"})
    @AutoConfigureBefore({NotifierTriggerConfiguration.class,CompositeNotifierConfiguration.class})
    public static class DingTalkNotifierConfiguration {
        @Bean
        @ConditionalOnMissingBean
        @ConfigurationProperties(prefix = "spring.boot.admin.notify.dingtalk")
        public DingTalkNotifier dingTalkNotifier(InstanceRepository repository) {
            return new DingTalkNotifier(repository);
        }
    }
}
