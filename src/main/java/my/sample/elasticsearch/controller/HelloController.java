package my.sample.elasticsearch.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @RequestMapping(value = "/")
    public String hello() {
        return "hello";
    }

    @RequestMapping(value = "/es")
    public String es(){
        return "success";
    }

}
