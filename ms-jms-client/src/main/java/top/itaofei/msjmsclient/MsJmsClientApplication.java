package top.itaofei.msjmsclient;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.MessageConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

@SpringBootApplication
@EnableJms
public class MsJmsClientApplication {

  public static void main(String[] args) {
    SpringApplication.run(MsJmsClientApplication.class, args);
  }

//  @Autowired
//  private ConnectionFactory connectionFactory;


}
