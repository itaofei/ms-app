package top.itaofei.msjmsserver;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import javax.script.ScriptEngine;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyMessageListener implements MessageListener {

  @SneakyThrows
  @Override
  public void onMessage(Message message) {

    if (message instanceof TextMessage) {
      TextMessage textMessage = (TextMessage) message;
      try {
        String correlationId = textMessage.getJMSCorrelationID();
        String result = "Server respond: " + textMessage.getText();

        jmsTemplate.send("server", session -> {
          TextMessage response = session.createTextMessage(result);
          response.setJMSCorrelationID(correlationId);
          return response;
        });

      } catch (JMSException e) {
        // Handle the exception
      }
    } else {
      // Handle other types of messages, such as ObjectMessage or BytesMessage
      log.error("Unsupported message type.");
    }
  }

  @Autowired
  private JmsTemplate jmsTemplate;

}
