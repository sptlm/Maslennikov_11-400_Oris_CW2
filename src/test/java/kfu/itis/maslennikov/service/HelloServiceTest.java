package kfu.itis.maslennikov.service;

import kfu.itis.maslennikov.service.impl.HelloService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HelloServiceTest {

    @Test
    void sayHelloShouldFormatName() {
        HelloService helloService = new HelloService();
        assertThat(helloService.sayHello("Bob")).isEqualTo("Hello, Bob");
    }
}