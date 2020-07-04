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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
        return "login_new";
    }

//    @RequestMapping("/do_login")
//    @ResponseBody
//    public Result<Boolean> doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
//        logger.info(loginVo.toString());
//        //登录
//        quickSaleUserService.login(loginVo);
//        return Result.success(true);
//    }


    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(@ModelAttribute("userLoginDTO") LoginVo loginVo) {
//    public String doLogin(@ModelAttribute("userLoginDTO") LoginVo loginVo) {
        logger.info(loginVo.toString());
        String mobile = loginVo.getMobile();
        String plainText = loginVo.getPassword();

        // validation
        if (StringUtils.isEmpty(plainText))
            return Result.error(CodeMsg.PASSWORD_EMPTY);
        if (StringUtils.isEmpty(mobile))
            return Result.error(CodeMsg.MOBILE_EMPTY);
        if (!ValidatorUtil.isMobile(mobile))
            return Result.error(CodeMsg.MOBILE_ERROR);

        // go login
        CodeMsg msg = quickSaleUserService.login(loginVo);
        if (msg.getCode() == 0)
//            return "Su";
            return Result.success(true);
        else
//            return "Fa";
            return Result.error(msg);
    }
}
