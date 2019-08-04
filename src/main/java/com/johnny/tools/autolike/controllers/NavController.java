package com.johnny.tools.autolike.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
public class NavController {

    @GetMapping(value = "index")
    public String goToPdfListPage(ModelAndView modelAndView) throws Exception {
        return "index";
    }
}
