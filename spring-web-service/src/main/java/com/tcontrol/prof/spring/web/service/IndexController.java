package com.tcontrol.prof.spring.web.service;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class IndexController{
    
    public IndexController() {
    }
    
    @RequestMapping(value = "/")
    public String showIndex(ModelMap model) {
        model.addAttribute("message", "Welcome to Spring WEB Service POC");
        return "index";
    }
    
    @RequestMapping(value = "/sensors-tab")
    public String showSsensors(ModelMap model) {
        return "sensors";
    }
}
