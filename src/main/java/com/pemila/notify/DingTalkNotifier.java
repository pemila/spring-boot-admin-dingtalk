package com.pemila.notify;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.notify.AbstractStatusChangeNotifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 钉钉机器人通知定义
 * @author 月在未央
 * @date 2019/2/27 14:16
 */
@Component
public class DingTalkNotifier extends AbstractStatusChangeNotifier {

    private static final String DEFAULT_MESSAGE = "# #{instance.registration.name} (#{instance.id}) is **#{event.statusInfo.status}**";

    private final SpelExpressionParser parser = new SpelExpressionParser();
    private RestTemplate restTemplate = new RestTemplate();
    @Value("${spring.boot.admin.notify.dingtalk.webhookToken}")
    private String webHookToken;
    private String atMobiles;
    private String msgtype = "markdown";
    private String title = "服务告警";
    private Expression message;

    public DingTalkNotifier(InstanceRepository repositpry) {
        super(repositpry);
        this.message =  parser.parseExpression(DEFAULT_MESSAGE, ParserContext.TEMPLATE_EXPRESSION);
    }

    @Override
    protected Mono<Void> doNotify(InstanceEvent event, Instance instance) {
        return Mono.fromRunnable(() -> restTemplate.postForEntity(webHookToken, createMessage(event, instance), Void.class));
    }

    private HttpEntity<Map<String, Object>> createMessage(InstanceEvent event, Instance instance) {
        Map<String, Object> messageJson = new HashMap<>();
        HashMap<String, String> params = new HashMap<>();
        params.put("text", this.getMessage(event, instance));
        params.put("title", this.title);
        messageJson.put("atMobiles", this.atMobiles);
        messageJson.put("msgtype", this.msgtype);
        messageJson.put(this.msgtype, params);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return new HttpEntity<>(messageJson, headers);
    }

    private String getMessage(InstanceEvent event, Instance instance) {
        Map<String, Object> root = new HashMap<>();
        root.put("event", event);
        root.put("instance", instance);
        root.put("lastStatus", getLastStatus(event.getInstance()));
        StandardEvaluationContext context = new StandardEvaluationContext(root);
        context.addPropertyAccessor(new MapAccessor());
        return message.getValue(context, String.class);
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getWebHookToken() {
        return webHookToken;
    }

    public void setWebHookToken(String webHookToken) {
        this.webHookToken = webHookToken;
    }

    public String getAtMobiles() {
        return atMobiles;
    }

    public void setAtMobiles(String atMobiles) {
        this.atMobiles = atMobiles;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public Expression getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = this.parser.parseExpression(message, ParserContext.TEMPLATE_EXPRESSION);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
