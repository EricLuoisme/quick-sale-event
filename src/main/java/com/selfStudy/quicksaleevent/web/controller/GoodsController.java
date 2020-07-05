package com.selfStudy.quicksaleevent.web.controller;

import com.alibaba.druid.util.StringUtils;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleUser;
import com.selfStudy.quicksaleevent.domain.model.User;
import com.selfStudy.quicksaleevent.redis.RedisService;
import com.selfStudy.quicksaleevent.service.GoodsService;
import com.selfStudy.quicksaleevent.service.QuickSaleUserService;
import com.selfStudy.quicksaleevent.vo.GoodsVO;
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
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    RedisService redisService;

    QuickSaleUserService quickSaleUserService;

    GoodsService goodsService;

    Logger log = LoggerFactory.getLogger(GoodsController.class);

    public GoodsController(RedisService redisService, QuickSaleUserService quickSaleUserService, GoodsService goodsService) {
        this.redisService = redisService;
        this.quickSaleUserService = quickSaleUserService;
        this.goodsService = goodsService;
    }


    @RequestMapping("/to_list")
    public String list(Model model, QuickSaleUser user) {
        model.addAttribute("user", user);

        // 1. query for all goods' info
        List<GoodsVO> goodsList = goodsService.listGoodsVo();
        log.info(goodsList.get(0).getGoodsImg());
        model.addAttribute("goodsList", goodsList);
        return "goods_list";
    }
}
