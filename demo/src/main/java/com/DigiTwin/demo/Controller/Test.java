package com.DigiTwin.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Test {
    @GetMapping("/test")
    public String testPage() { return "forward:/test.html"; }
    @GetMapping("/login")
    public String loginPage() { return "forward:/login.html";}
    @GetMapping("/register")
    public String registerPage() { return "forward:/register.html";}
}
