package my.sample.elasticsearch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @RequestMapping(value = "/")
    @ResponseBody
    public String hello() {
        return "hello";
    }

    @RequestMapping(value = "form")
    public String form() {
        return "form";
    }
}
