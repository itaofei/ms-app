package top.itaofei.msgreetingclient;

import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class HelloController {

  @RequestMapping(value = "/hello", method = RequestMethod.GET)
  public String hello() {

    System.out.println(context);

    return restTemplate.getForEntity("http://SERVICE-GREETING/hello", String.class).getBody();
  }

  @Autowired
  private ApplicationContext context;

  @Autowired
  private RestTemplate restTemplate;

}
