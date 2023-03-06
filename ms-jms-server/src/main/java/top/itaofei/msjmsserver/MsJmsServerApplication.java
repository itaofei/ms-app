package top.itaofei.msjmsserver;

import jakarta.jms.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

@SpringBootApplication
@EnableJms
public class MsJmsServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(MsJmsServerApplication.class, args);
  }

  @Autowired
  private ConnectionFactory connectionFactory;

  @Autowired
  private MyMessageListener messageListener;

  @Bean
  public DefaultMessageListenerContainer myMessageListenerContainer() {
    DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.setDestinationName("client");
    container.setMessageListener(messageListener);
    return container;
  }

}
