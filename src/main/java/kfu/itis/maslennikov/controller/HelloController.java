package kfu.itis.maslennikov.controller;

import kfu.itis.maslennikov.service.HelloService;
import kfu.itis.maslennikov.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HelloController {
    private final UserService userService;
    private final HelloService helloService;

    public HelloController(HelloService helloService, UserService userService) {
        this.helloService = helloService;
        this.userService = userService;
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(required = false, name="name") String name){
        return helloService.sayHello(name);
    }
}
