package com.selfStudy.quicksaleevent.web.controller;

import com.selfStudy.quicksaleevent.domain.model.QuickSaleUser;
import com.selfStudy.quicksaleevent.redis.RedisService;
import com.selfStudy.quicksaleevent.service.GoodsService;
import com.selfStudy.quicksaleevent.service.QuickSaleUserService;
import com.selfStudy.quicksaleevent.vo.GoodsVO;
import com.selfStudy.quicksaleevent.web.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    /**
     * For pressure testing on getting user information
     */

    RedisService redisService;

    QuickSaleUserService quickSaleUserService;

    public UserController(RedisService redisService, QuickSaleUserService quickSaleUserService) {
        this.redisService = redisService;
        this.quickSaleUserService = quickSaleUserService;
    }


    @RequestMapping("/info")
    @ResponseBody
    public Result<QuickSaleUser> info(Model model, QuickSaleUser user) {
        model.addAttribute("user", user);
        return Result.success(user);
    }


}
