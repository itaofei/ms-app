package top.itaofei.msgreeting;

import com.netflix.appinfo.EurekaInstanceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloEndpoint {

  @RequestMapping(method = RequestMethod.GET, value = "/hello")
  public String hello() {
    this.logger.info("/hello, instanceId:{}, host:{}", eurekaInstanceConfig.getInstanceId(),
        eurekaInstanceConfig.getHostName(false));
    return "Hello, Spring Cloud! My port is " + serverPort;
  }

  @Autowired
  private EurekaInstanceConfig eurekaInstanceConfig;

  @Value("${server.port}")
  private int serverPort = 0;

  protected Logger logger = LoggerFactory.getLogger(HelloEndpoint.class);

}
