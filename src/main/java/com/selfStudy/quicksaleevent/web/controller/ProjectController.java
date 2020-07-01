package com.selfStudy.quicksaleevent.web.controller;

import com.selfStudy.quicksaleevent.web.result.CodeMsg;
import com.selfStudy.quicksaleevent.web.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class ProjectController {

    // REST-api
    @RequestMapping("/hello")
    @ResponseBody
    public Result<String> hello() {
        return Result.success("Successful");
    }

    @RequestMapping("/helloError")
    @ResponseBody
    public Result<String> helloError() {
        return Result.error(CodeMsg.SERVER_ERROR);
    }

    // Thymeleaf
    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model) {
        model.addAttribute("name", "yikai");
        return "welcome-page";
    }
}
