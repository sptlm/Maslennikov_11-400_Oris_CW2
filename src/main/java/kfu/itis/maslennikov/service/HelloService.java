package kfu.itis.maslennikov.service;

import org.springframework.stereotype.Service;

@Service
public class HelloService {
    public String sayHello(String name){
        return "Hello, %s".formatted(name);
    }
}
