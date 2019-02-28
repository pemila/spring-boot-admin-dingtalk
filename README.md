# spring-boot-admin-dingtalk
spring-boot-admin使用钉钉机器人发送服务状态变动通知

# 功能依赖
项目基于spring-boot-admin 2.0.1,并注册至eureka服务注册中心，获取其他所有服务的状态。

# 钉钉机器人扩展
### 创建钉钉通知类

  DingTalkNotifier.class继承AbstractStatusChangeNotifier用于获取服务状态变动。

### 配置通知

  由于spring-boot-admin内置的通知配置类AdminServerNotifierAutoConfiguration未给出自定义接口,因此创建CustomNotifierAutoConfiguration.class继承AdminServerNotifierAutoConfiguration类并配置spring自动加载。

### 配置钉钉机器人

  在application.yml中设置机器人的webhookToken
