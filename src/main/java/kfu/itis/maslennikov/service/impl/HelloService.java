package kfu.itis.maslennikov.service.impl;

import kfu.itis.maslennikov.aop.Benchmarkable;
import kfu.itis.maslennikov.aop.Metricable;
import org.springframework.stereotype.Service;

@Service
public class HelloService {
    @Metricable
    @Benchmarkable
    public String sayHello(String name){
        return "Hello, %s".formatted(name);
    }
}
