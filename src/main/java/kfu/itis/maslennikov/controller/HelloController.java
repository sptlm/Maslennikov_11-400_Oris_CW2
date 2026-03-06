package kfu.itis.maslennikov.controller;

import kfu.itis.maslennikov.dto.UserDto;
import kfu.itis.maslennikov.service.HelloService;
import kfu.itis.maslennikov.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDto> users(@RequestParam(required = false, name="name") String name){
        return userService.findAll();
    }
}
