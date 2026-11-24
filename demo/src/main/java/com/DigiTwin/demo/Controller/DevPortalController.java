package com.DigiTwin.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DevPortalController {
    @GetMapping("/dev")
    public String devPortal() { return "forward:/dev/index.html"; }
}
