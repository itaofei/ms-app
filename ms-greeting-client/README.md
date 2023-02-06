# 1 构建服务消费者（Eureka Client）
我们下面来构建一个服务消费者，该服务消费者将调用SERVICE-GREETING所提供的服务。
## 1.1 Dependency
```xml
<dependencies>
  <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
  </dependency>
</dependencies>
```
这里需要注意的是我们除了依赖`spring-cloud-starter-netflix-eureka-client`，还依赖了
`Spring Cloud`中的 另外一个子项目`spring-cloud-starter-loadbalancer`，该子项目
提供客户端负载均衡功能，可以自动 从Eureka服务器中获取服务提供者的地址列表，从而能够发起
相应的调用。这个后面我们将详细进行说明，这里先引入进来就可以了。

## 1.2 Bootstrap
```java
@SpringBootApplication
@EnableDiscoveryClient
public class MsGreetingClientApplication {

  public static void main(String[] args) {
    SpringApplication.run(MsGreetingClientApplication.class, args);
  }

  @Bean
  @LoadBalanced
  public RestTemplate restTemplate(RestTemplateBuilder builder) {

    return builder.build();
  }

}
```

## 1.3 服务调用
```java
@RestController
public class HelloController {
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hello() {
        return restTemplate.getForEntity("http://SERVICE-GREETING/hello", String.class).getBody();
    }
}
```
该服务调用时一个标准的`controller`，`hello()`方法将通过`restTemplate`调用
`SERVICE-HELLO/hello`服务并返回。这里如果`restTemplate`没有声明`@LoadBalancer`，
restTemplate在调用服务时，会报`Unknown host`的错误。

