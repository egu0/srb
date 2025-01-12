# 尚融宝后台

在根 pom.xml 中以下邊這種方式引入 spring-boot-starter-test（排除 junit-vintage-engine）

```xml

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
        <exclusions>
            <exclusion>
                <groupId>org.junit.vintage</groupId>
                <artifactId>junit-vintage-engine</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
</dependencies>
```

使用這種方式，在編寫測試用例時無需使用 @RunWith 註解，並且所使用的 @Test 註解來自
org.junit.jupiter.api 包