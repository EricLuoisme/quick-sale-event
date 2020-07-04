package com.selfStudy.quicksaleevent.web.controller;

import com.alibaba.druid.util.StringUtils;
import com.selfStudy.quicksaleevent.redis.RedisService;
import com.selfStudy.quicksaleevent.vo.LoginVo;
import com.selfStudy.quicksaleevent.web.result.CodeMsg;
import com.selfStudy.quicksaleevent.web.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {

    RedisService redisService;

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping("/to_login")
    public String home(Model model) {
//        return "login";
        model.addAttribute("loginVo", new LoginVo());
        return "login_new";
    }

//    @RequestMapping("/do_login")
//    public Result<Boolean> doLogin(LoginVo loginVo) {
//        logger.info(loginVo.toString());
//        // do validation
//        return null;
//    }

    @PostMapping("/do_login")
    public Result<Boolean> doLogin(@ModelAttribute("userLoginDTO") LoginVo loginVo) {

        logger.info(loginVo.toString());
        String mobile = loginVo.getMobile();
        String plainText = loginVo.getPassword();

        // TODO 1. validation
        if (StringUtils.isEmpty(plainText))
            return Result.error(CodeMsg.PASSWORD_EMPTY);
        if (StringUtils.isEmpty(mobile))
            return Result.error(CodeMsg.MOBILE_EMPTY);

        // TODO 2. MD5 encoding
        return Result.success(true);
    }

}
