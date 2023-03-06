package top.itaofei.msjmsclient;

import jakarta.jms.TextMessage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SendController {

  @SneakyThrows
  @RequestMapping(value = "/hello", method = RequestMethod.GET)
  public String hello(
      @RequestParam(name = "correlationId") String correlationId,
      @RequestParam(name = "message") String message) {

    jmsTemplate.send("client", session -> {
      TextMessage textMessage = session.createTextMessage(message);
      textMessage.setJMSCorrelationID(correlationId);
      return textMessage;
    });

    jmsTemplate.setReceiveTimeout(10000);
    TextMessage response =
        (TextMessage) jmsTemplate.receiveSelected("server",
            "JMSCorrelationID = '" + correlationId + "'");

    return "Rev:" + response.getText();
  }

  @Autowired
  private ApplicationContext context;

  @Autowired
  private JmsTemplate jmsTemplate;

}
