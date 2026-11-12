package com.DigiTwin.demo.Controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CalculatorController {
    @GetMapping("/api/calculator/add")
    public Map<String, Object> add(
            @RequestParam(defaultValue = "1") double first,
            @RequestParam(defaultValue = "1") double second) {
        return Map.of(
                "first", first,
                "second", second,
                "operation", "+",
                "result", first + second);
    }
}
