package com.selfStudy.quicksaleevent.web.controller;

import com.selfStudy.quicksaleevent.domain.model.User;
import com.selfStudy.quicksaleevent.redis.RedisService;
import com.selfStudy.quicksaleevent.redis.UserKey;
import com.selfStudy.quicksaleevent.service.UserService;
import com.selfStudy.quicksaleevent.web.result.CodeMsg;
import com.selfStudy.quicksaleevent.web.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class ProjectController {

    UserService userService;

    RedisService redisService; // injected by constructor

    public ProjectController(UserService userService, RedisService redisService) {
        this.userService = userService;
        this.redisService = redisService;
    }

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

    // Database connection test
    @RequestMapping("db/get")
    @ResponseBody
    public Result<User> dbGet() {
        User user = userService.getById(1);
        return Result.success(user);
    }

    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbTx() {
        userService.tx();
        return Result.success(true);
    }

    @RequestMapping("redis/get")
    @ResponseBody
    public Result<User> redisGet() {
        User user = redisService.get(UserKey.getById, "" + 1, User.class);
        return Result.success(user);
    }

    @RequestMapping("redis/set")
    @ResponseBody
    public Result<Boolean> redisSet() {
        User user = new User();
        user.setId(1);
        user.setName("1111");
        redisService.set(UserKey.getById, "" + 1, user); // UserKey:id1
        return Result.success(true);
    }
}
