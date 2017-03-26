package com.zhangfuwen.app;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HomeController
 */
@Controller
public class HomeController {
    @RequestMapping("/home")
    public String index() {
        //model.addAttribute("msg", "hello");
        return "index";
    }
    @RequestMapping("/login")
    public String login() {
        return "login";
    }

}