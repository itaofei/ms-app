package top.itaofei.msjmsclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class JmsShellCommand {

  @ShellMethod("Send message")
  public String send(
      @ShellOption(help = "The destination of message", value={"-d", "--destination"}) String destination,
      @ShellOption(help = "The message", value = {"-m", "--message"}) String message) {

    jmsTemplate.convertAndSend(destination, message);
    return destination + message;
  }

  @ShellMethod("Stop the application")
  public void stop() {
    System.exit(0);
  }

  @Autowired
  private ConfigurableApplicationContext context;

  @Autowired
  private JmsTemplate jmsTemplate;

}
