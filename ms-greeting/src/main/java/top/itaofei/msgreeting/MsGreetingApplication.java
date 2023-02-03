package top.itaofei.msgreeting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsGreetingApplication {
  public static void main(String[] args) {
    SpringApplication.run(MsGreetingApplication.class, args);
  }

}
