# 1 Swagger UI integration 
> For the integration between spring-boot and swagger-ui, add the library to the list
> of your project dependencies (No additional configuration is needed)

```xml
<dependencies>
  <dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.0.2</version>
  </dependency>
</dependencies>
```

> This will automatically deploy swagger-ui to a spring-boot application:
> Documentation will be available in HTML format, using the official swagger-ui jars
> 
> The Swagger UI page will then be available at `http://server:port/context-path/swagger
> -ui.html`and the OpenAPI description will be available at the following url for json
> format:`http://server:port/context-path/v3/api-docs` or yaml format:`http://server:port
> /context-path/v3/api-docs.yaml`

