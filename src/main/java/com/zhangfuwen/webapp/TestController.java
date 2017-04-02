package com.zhangfuwen.webapp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by dean on 3/26/17.
 */
@Controller
public class TestController {
    @RequestMapping("/test")
    @ResponseBody
    public String hello() {
        return "hello world <img src=\"assets/img/a5.png\">";
    }
}
