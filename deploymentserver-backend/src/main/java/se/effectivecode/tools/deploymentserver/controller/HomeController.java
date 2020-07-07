package se.effectivecode.tools.deploymentserver.controller;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Viggo Ahl, Effective Code AB
 */
@Controller
@RequestMapping("/taurus")
@ComponentScan(basePackages = "se.effectivecode.tools.deploymentserver")
public class HomeController {

    @GetMapping
    public String home() {
        return "forward:/index.html";
    }
}