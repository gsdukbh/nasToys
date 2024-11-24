package top.werls.springboottemplate.config;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.outbound.Mqttv5PahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaderMapper;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;

/**
 * mqtt 配置
 *
 * @author JiaWei Lee
 * @since on 24 11月 2024
 * @version 1
 */
@Configuration
public class MqttConfig {
  @Resource private ConfigProperties configProperties;

  // MQTT 客户端工厂
  @Bean
  public MqttPahoClientFactory mqttClientFactory() throws Exception{

    // 创建忽略证书的 TrustManager
    TrustManager[] trustAllCerts = new TrustManager[]{
        new X509TrustManager() {

          @Override
          public void checkClientTrusted(X509Certificate[] chain, String authType)
              throws CertificateException {

          }

          @Override
          public void checkServerTrusted(X509Certificate[] chain, String authType)
              throws CertificateException {

          }

          @Override
          public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
          }
        }
    };
    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, trustAllCerts, new java.security.SecureRandom());



    DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
    MqttConnectOptions options = new MqttConnectOptions();
    options.setServerURIs(new String[] {"ssl://localhost:18833"});
    options.setUserName(configProperties.getMqtt().getUsername());
    options.setPassword(configProperties.getMqtt().getPassword().toCharArray());
    options.setCleanSession(true);
    options.setSocketFactory(sslContext.getSocketFactory());
    options.setSSLHostnameVerifier((hostname, session) -> true);
    factory.setConnectionOptions(options);
    return factory;
  }

//  @Bean
  public IntegrationFlow mqttOutFlow() {
    MqttConnectionOptions options = new MqttConnectionOptions();
    options.setServerURIs(new String[] {"ssl://localhost:18833"});
    options.setUserName(configProperties.getMqtt().getUsername());
    options.setPassword(configProperties.getMqtt().getPassword().getBytes());
    options.setCleanStart(true);


    Mqttv5PahoMessageHandler messageHandler = new Mqttv5PahoMessageHandler(options, "mqttv5SIout");
    MqttHeaderMapper mqttHeaderMapper = new MqttHeaderMapper();
    mqttHeaderMapper.setOutboundHeaderNames("some_user_header", MessageHeaders.CONTENT_TYPE);
    messageHandler.setHeaderMapper(mqttHeaderMapper);
    messageHandler.setAsync(true);
    messageHandler.setAsyncEvents(true);
    //        messageHandler.setConverter(new );

    return f -> f.handle(messageHandler);
  }

  // 定义输出通道
  @Bean
  public MessageChannel mqttOutputChannel() {
    return new DirectChannel();
  }

  // 定义输入通道
  @Bean
  public MessageChannel mqttInputChannel() {
    return new DirectChannel();
  }

  // 配置 MQTT v5 消息发布
  @Bean
  @ServiceActivator(inputChannel = "mqttOutputChannel")
  public MessageHandler mqttOutbound(MqttPahoClientFactory mqttClientFactory) {
    var clienid = configProperties.getMqtt().getClientId();
    MqttPahoMessageHandler messageHandler =
        new MqttPahoMessageHandler(clienid + "_outbound", mqttClientFactory);

    messageHandler.setAsync(true);
    return messageHandler;
  }

  // 配置 MQTT v5 消息订阅
  @Bean
  public MqttPahoMessageDrivenChannelAdapter inbound(MqttPahoClientFactory mqttClientFactory) {
    String[] topics = configProperties.getMqtt().getDefaultTopic(); // 需要订阅的主题
    var clienid = configProperties.getMqtt().getClientId();
    MqttPahoMessageDrivenChannelAdapter adapter =
        new MqttPahoMessageDrivenChannelAdapter(clienid + "_inbound", mqttClientFactory, topics);
    adapter.setQos(configProperties.getMqtt().getQos());
    adapter.setOutputChannel(mqttInputChannel());
    return adapter;
  }

  // 处理订阅消息
  @Bean
  @ServiceActivator(inputChannel = "mqttInputChannel")
  public MessageHandler handler() {
    return message -> {
      System.out.println("Received message: " + message.getPayload());
    };
  }
}
