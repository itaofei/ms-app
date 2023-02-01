# 1 概述

Spring Cloud Config具有中心化、版本控制、支持动态更新和语言独立等特性。其特点是：

* 提供服务端和客户端支持(Spring Cloud Config Server和Spring Cloud Config Client)；
* 集中式管理分布式环境下的应用配置；
* 基于Spring环境，实现了与Spring应用无缝集成；
* 可用于任何语言开发的程序；
* 默认实现基于Git仓库(也支持SVN)，从而可以进行配置的版本管理；

Spring Cloud Config的结构图：
![123](README\img.png)

# 2 配置规则详解
Config Server是如何与Git仓库中的配置文件进行匹配的呢？通常，我们会为一个项目建立类似如下的配置文件:

* mallweb.properties: 基础配置文件;
* mallweb-dev.properties: 开发使用的配置文件;
* mallweb-test.properties: 测试使用的配置文件;
* mallweb-prod.properties: 生产环境使用的配置文件;

当我们访问Config Server的端点时，就会按照如下映射关系来匹配相应的配置文件：

* /{application}/{profile}[/{label}]
* /{application}-{profile}.yml
* /{label}/{application}-{profile}.yml
* /{application}-{profile}.properties
* /{label}/{application}-{profile}.properties

上面的Url将会映射为格式为:{application}-{profile}.properties(yml)的配置文件。另外，label则对应Git上分支名称，是一个可选参数，如果没有则为默认的master分支。

而Config-Client的bootstrap.properties配置对应如下:

* spring.application.name <==> application;
* spring.cloud.config.profile <==> profile;
* spring.cloud.config.label <==> label.

## 2.1 Git仓库配置
Config Server默认使用的就是Git，所以配置也非常简单，如上面的配置
```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: http://
          username: username
          password: password
```

### 2.1.1 使用占位符
在服务端配置中我们也可以使用{application}、{profile}和{label}占位符，如下：
```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: http://github.com/user/{application}
          username: username
          password: password
```

这样，我们就可以为每一个应用客户端创建一个单独的仓库。

> 这里需要注意的是，如果Git的分支或标签中包含"/"时，在{label}参数中需要使用"(_)"替代，这个主要是避免与Http URL转义符处理的冲突。

### 2.1.2 使用模式匹配
我们也可以使用{application}/{profile}进行模式匹配，以便获取到相应的配置文件。配置示例如下：
```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/spring-cloud-sample/config-repo
          repos:
            team-a:
              pattern: team-a*
              uri: https://github.com/team-a/config-repo
            team-b:
              pattern: team-b*
              uri: https://github.com/team-b/config-repo
```

如果{application}/{profile}没有匹配到team-a或team-b任何资源，则使用spring.cloud.config.server.git.uri配置的默认URI。

### 2.1.3 搜索目录
当我们把配置文件存放在Git仓库中子目录中时，可以通过设置`search-path`来指定该目录。同样，`search-path`也支持上面的占位符。示例如下:
```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/spring-cloud-samples/config-repo
          searchPaths: foo,bar*
```
这样系统就会自动搜索foo的子目录，以及以bar开头的文件夹中的子目录。

### 2.1.4 代理
当Config-Server使用github访问配置库时，我们可以在`~/.git/config`下配置HTTP所使用的代理，也可以
使用JVM系统属性`-Dhttp.proxyHost`和`-Dhttp.proxyPort`来配置。

### 2.1.5 本地缓存
当Config-Server从Git(或SVN)中获取了配置信息后，将会在本地的文件系统中存储一份。
默认将存储在系统临时目录下，并且以`config-repo-`作为开头，在Linux系统中默认存储
的目录为`/tmp/config-repo-<randomid>`。Config-Server将配置信息存储在本地可以
有效的防止当Git仓库出现故障而无法访问的问题，当Config-Server无法访问到Git仓库时
就会读取之前存储在本地文件中的配置，然后将这些配置信息返回给Config-Client。

Spring Cloud 官方文档建议我们在Config-Server中指定本地文件路径，以避免出现不可预知
的错误。可以使用下面的属性配置来指定本地文件路径：
```yaml
## Git仓库
spring:
  cloud:
    config:
      server:
        git:
          basedir: tmp/
## Svn仓库
        svn:
          basedir: tmp/
```

# 3 安全保护
## 3.1 Config Server访问安全
对于我们存储在配置中心的一些配置内容，总会有一些是敏感信息，比如数据库连接的用户名和密码，你总不能直接裸奔吧，所以我们还是需要对Config-Server做一些安全控制。当然，对于Config-Server的安全控制有很多种，比如：物理网络限制、OAuth2授权等。但是，在这里因为我们使用的是SpringBoot，所以使用SpringSecurity会更容易也更简单。

这时候，我们只需要在Config-Server中增加如下依赖:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

此时，当我们启动Config-Server时，SpringSecurity会默认为我们生产一个访问密码，这种方式常常不是我们需要的，所以一般我们还需要在配置文件中配置用户名和密码，比如：
```yaml
spring:
 security:
  user:
    name: admin
    password: admin1234
```

这样，当我们需要访问Config-Server时就会弹出用户认证对话框。此时，对于Config-Client我们需要在配置文件中增加用户和访问口令的配置，如下：
```yaml
spring:
  cloud:
    config:
      username: admin
      password: admin1234
```

## 3.2 加密与解密
访问安全是对整体的控制，多数情况下我们还需要对敏感内容加密后存储，比如之前所说的数据库访问的用户名称和登录口令。很幸运，Spring Cloud Config为我们提供相应的支持。

Spring Cloud Config提供了两种加解密方式: 1)对称加密; 2)非对称加密。在描述如何使用之前，我们先看看一些使用前提。

### 3.2.1 安装JCE(Java Cryptography Extension)
Spring Cloud Config所提供的加解密依赖JCE，因为，JDK中没有默认提供，所以我们需要先安装JCE。安装方法也比较简单，就是下载相应的Jar包，然后把这些包替换$JDK_HOME/jar/lib/security相应的文件

OpenJdk在9及以上的版本默认已支持JCE。

### 3.2.2 加解密端点
另外，Spring Cloud Config还提供了两个端点进行加密和解密，如下：

* /encrypt: 加密端点，使用格式如下: curl $CONFIG_SERVER/encrypt -d 所要加密的内容
* /decrypt: 解密端点，使用格式如下: curl $CONFIG_SERVER/decrypt -d 所要解密的内容

> 注意：当你测试中所加解密中包含特殊字符时，需要进行URL编码，这时候你需要使用--data-urlencode而不是-d.

### 3.2.3 对称加密
对称加解密的配置非常简单。我们只需要在配置文件中增加加解密所使用的密钥即可，如：
```yaml
encrypt:
  key: ms-app-key
```

### 3.2.4 非对称加密
非对称加密相对于对称加密来说复杂了一些，首先我们需要借助Java的keytool生成密钥对，然后创建Key Store并复制到服务器目录下。

1. 使用keytool生成Key Store，命令如下：
```shell
keytool -genkeypair -alias ms-app-key1 -keyalg RSA -dname "CN=ms-app-1,OU=TwoStepsFromJava,O=Organization,L=city,S=province,C=china" -keypass 123456 -keystore ms-app-1.jks -storepass 123456
```
2. 将生成的`jks`拷贝到`config server`的`resource`目录下。
3. 修改配置文件
```yaml
encrypt:
  key-store:
    location: ms-app-1.jks
    alias: ms-app-key1
    password: 123456
    secret: 123456
```

# 4 高可用配置

## 4.1 整合Eureka
看到这里，可能有些童鞋已经发现，我们在Config-Client中配置`config.uri`时使用的
具体的地址，那么是否可以使用之前的Eureka呢？答案是肯定，我们可以把Config-Server
和其它微服务一样作为一个服务基本单元。我们只需要进行如下修改即可。

### 4.1.1 Config-Server改造
在pom.xml中增加依赖：
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-eureka</artifactId>
</dependency>
```

在配置文件中配置我们服务的名称，及之前我们所编写Eureka服务器的地址：
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:18761/eureka
```

启动类：增加`@EnableDiscoveryClient`注解。
```java
@SpringBootApplication
@EnableConfigServer
@EnableDiscoveryClient
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

### 4.1.2 Config-Client改造
在pom.xml中增加如下依赖：
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-eureka</artifactId>
</dependency>
```

配置文件修改，注意这里的配置文件为：`bootstrap.yml`
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:18761/eureka
spring:
  cloud:
    config:
      discovery:
        enable: true
        service-id: ms-config-server
```

修改启动类：增加`@EnableDiscoveryClient`注解。
```java
@EnableDiscoveryClient
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## 4.2 快速失败与响应
### 4.2.1 开启Config-Server启动加载
默认情况下，只有当客户端请求时服务端才会从配置的Git仓库中进行加载，
我们可以通过设置`clone-on-start`，让服务端在启动时就会加载。
```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/itaofei/ms-app-configs
          username: itaofei
          password: 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
          #clone-on-start: true
          repos:
            team-a:
              pattern: reg*
              uri: https://github.com/itaofei/ms-app-reg-configs
              clone-on-start: true
              username: itaofei
              password: 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
```
上面的配置，对于team-a的则在Config-Server启动时就会加载相应的配置，而对于其它
则不会。当然，我们可以通过设置`spring.cloud.config.server.git.clone-on-start`
的值来进行全局配置。

### 4.2.2 开启Config-Client快速失败
在一些情况下，我们希望启动一个服务时无法连接到服务端能够快速返回失败，那么可以通过
下面的配置来设置:
```yaml
spring:
  cloud:
    config:
      fail-fast: true
```

### 4.2.3 设置Config-Client重试
如果在启动时Config-Server碰巧不可以使用，你还想后面再进行重试，那么我们开始开启
Config-Client的重试机制。首先，我们需要配置：
```yaml
spring:
  cloud:
    config:
      fail-fast: true
```
然后，我们需要在我们的依赖中增加：
```xml
<dependencys>
  <dependency>
    <groupId>org.springframework.retry</groupId>
    <artifactId>spring-retry</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
  </dependency>
</dependencys>
```
这样，我们就可以为Config-Client开启了重试机制，当启动连接Config-Server失败时，
Config-Client会继续尝试连接Config-Server，默认会尝试连接6次，时间间隔初始
为1000毫秒，后面每次尝试连接会按照1.1倍数增加尝试连接时间的间隔，如果最后还不
能够连接到Config-Server才会返回错误。我们可以通过在配置文件中复写
`spring.cloud.config.retry.*`来进行相关配置。

> 如果你想全权控制重试机制，可以实现一个类型为:`RetryOperationsInterceptor`的类，
> 并把bean的id设置为:`configServerRetryInterceptor`。

