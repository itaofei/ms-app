# 1 构建服务提供者（Eureka Client）
编写一个简单的Eureka Client，该客户端提供一个简单的服务， 就是调用/hello服务端点(EndPoint)
时返回一个字符串Hello, Spring Cloud!。
## 1.1 Dependency
```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

## 1.2 Bootstrap
启动类增加`@EnableDiscoveryClient`注解。
```java
@EnableDiscoveryClient
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

## 1.3 Api
```java
@RestController
public class HelloEndpoint {
    protected Logger logger = LoggerFactory.getLogger(HelloEndpoint.class);

    @Autowired
    private EurekaInstanceConfig eurekaInstanceConfig;
    @Value("${server.port}")
    private int serverPort = 0;

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hello() {
        this.logger.info("/hello, instanceId:{}, host:{}", eurekaInstanceConfig.getInstanceId(), eurekaInstanceConfig.getHostName(false));
        return "Hello, Spring Cloud! My port is " + String.valueOf(serverPort);
    }
}
```
该服务仅提供一个`/hello`服务端点，调用该服务后将返回一个字符串`Hello, Spring Cloud!`。

## 1.4 Configuration
```yaml
spring:
  application:
    name: SERVICE-HELLO
eureka:
  client:
    service-url:
      defaultZone: http://localhost:18761/eureka
```
**说明：** 这里`spring.application.name`必须要设置，服务消费者将通过该名称调用所提供的服务。
`eureka.client.service-url`也必须设置，表示我们要向那些Eureka服务器进行服务注册，这里可以
声明多个Eureka服务器，具体我们将在后面关于Eureka高可用相关章节中进行详细说明。


# 2 Swagger UI integration 
> For the integration between spring-boot and swagger-ui, add the library to the list
> of your project dependencies (No additional configuration is needed)

```xml
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.0.2</version>
</dependency>
```

> This will automatically deploy swagger-ui to a spring-boot application:
> Documentation will be available in HTML format, using the official swagger-ui jars
> 
> The Swagger UI page will then be available at `http://server:port/context-path/swagger
> -ui.html`and the OpenAPI description will be available at the following url for json
> format:`http://server:port/context-path/v3/api-docs` or yaml format:`http://server:port
> /context-path/v3/api-docs.yaml`

