package com.tcontrol.prof.spring.web.service;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class IndexController{
    
    public IndexController() {
    }
    
    @RequestMapping(value = "/")
    public String printHello(ModelMap model) {
        model.addAttribute("message", "Welcome to Spring WEB Service POC");
        return "index";
    }
}
