package com.selfStudy.quicksaleevent.web.controller;

import com.alibaba.druid.util.StringUtils;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleUser;
import com.selfStudy.quicksaleevent.domain.model.User;
import com.selfStudy.quicksaleevent.redis.RedisService;
import com.selfStudy.quicksaleevent.service.QuickSaleUserService;
import com.selfStudy.quicksaleevent.vo.LoginVo;
import com.selfStudy.quicksaleevent.web.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    RedisService redisService;

    QuickSaleUserService quickSaleUserService;

    public GoodsController(RedisService redisService, QuickSaleUserService quickSaleUserService) {
        this.redisService = redisService;
        this.quickSaleUserService = quickSaleUserService;
    }

    @RequestMapping("/to_list")
    public String list(Model model, QuickSaleUser user) {
        model.addAttribute("user", user);
        return "goods_list";
    }
}
