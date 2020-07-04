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
    public String home(Model model) {
        model.addAttribute("loginVo", new LoginVo());
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(@Valid @ModelAttribute("userLoginDTO") LoginVo loginVo) {
        logger.info(loginVo.toString());
        String mobile = loginVo.getMobile();
        String plainText = loginVo.getPassword();

        CodeMsg msg = quickSaleUserService.login(loginVo);
        if (msg.getCode() == 0)
            return Result.success(true);
        else
            return Result.error(msg);
    }
}
