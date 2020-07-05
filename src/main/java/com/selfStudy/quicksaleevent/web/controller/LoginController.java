package com.selfStudy.quicksaleevent.web.controller;

import com.alibaba.druid.util.StringUtils;
import com.selfStudy.quicksaleevent.redis.RedisService;
import com.selfStudy.quicksaleevent.service.QuickSaleUserService;
import com.selfStudy.quicksaleevent.utils.MD5Util;
import com.selfStudy.quicksaleevent.utils.ValidatorUtil;
import com.selfStudy.quicksaleevent.vo.LoginVo;
import com.selfStudy.quicksaleevent.web.result.CodeMsg;
import com.selfStudy.quicksaleevent.web.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {

    RedisService redisService;

    QuickSaleUserService quickSaleUserService;

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    public LoginController(RedisService redisService, QuickSaleUserService quickSaleUserService) {
        this.redisService = redisService;
        this.quickSaleUserService = quickSaleUserService;
    }

    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
        logger.info(loginVo.toString());
        // login
        quickSaleUserService.login(response, loginVo);
        return Result.success(true);
    }
}
