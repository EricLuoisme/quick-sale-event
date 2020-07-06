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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    RedisService redisService;

    QuickSaleUserService quickSaleUserService;

    GoodsService goodsService;

    public GoodsController(RedisService redisService, QuickSaleUserService quickSaleUserService, GoodsService goodsService) {
        this.redisService = redisService;
        this.quickSaleUserService = quickSaleUserService;
        this.goodsService = goodsService;
    }


    @RequestMapping("/to_list")
    public String list(Model model, QuickSaleUser user) {
        List<GoodsVO> goodsList = goodsService.listGoodsVo(); // query for all goods' info
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsList);
        return "goods_list";
    }

    @RequestMapping("/to_detail/{goodsId}")
    public String detail(Model model, QuickSaleUser user, @PathVariable("goodsId") long goodsId) {
        GoodsVO goods = goodsService.getGoodsVoByGoodsId(goodsId);
        long start = goods.getStartDate().getTime();
        long end = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int quickSaleEventStatus = 0;
        int remainingSecond = 0;

        if (now < start) { // waiting for quick sale event
            quickSaleEventStatus = 0;
            remainingSecond = (int) ((start - now) / 1000); // counting down
        } else if (now > end) { // quick sale event expired
            quickSaleEventStatus = 2;
            remainingSecond = -1;
        } else { // quick sale event happening
            quickSaleEventStatus = 1;
            remainingSecond = 0;
        }
        model.addAttribute("user", user);
        model.addAttribute("goods", goods);
        model.addAttribute("quickSaleStatus", quickSaleEventStatus);
        model.addAttribute("remainSeconds", remainingSecond);

        return "goods_detail";
    }
}
