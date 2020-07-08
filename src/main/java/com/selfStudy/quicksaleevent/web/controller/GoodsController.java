package com.selfStudy.quicksaleevent.web.controller;

import com.alibaba.druid.util.StringUtils;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleUser;
import com.selfStudy.quicksaleevent.redis.GoodsKey;
import com.selfStudy.quicksaleevent.redis.RedisService;
import com.selfStudy.quicksaleevent.service.GoodsService;
import com.selfStudy.quicksaleevent.service.QuickSaleUserService;
import com.selfStudy.quicksaleevent.vo.GoodsDetailVo;
import com.selfStudy.quicksaleevent.vo.GoodsVo;
import com.selfStudy.quicksaleevent.web.result.Result;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    QuickSaleUserService userService; // auto wired by constructor

    RedisService redisService;

    GoodsService goodsService;

    ThymeleafViewResolver thymeleafViewResolver;

    ApplicationContext applicationContext;

    public GoodsController(QuickSaleUserService userService, RedisService redisService, GoodsService goodsService,
                           ThymeleafViewResolver thymeleafViewResolver, ApplicationContext applicationContext) {
        this.userService = userService;
        this.redisService = redisService;
        this.goodsService = goodsService;
        this.thymeleafViewResolver = thymeleafViewResolver;
        this.applicationContext = applicationContext;
    }

    @RequestMapping(value = "/to_list", produces = "text/html")
    public String list(HttpServletRequest request, HttpServletResponse response, Model model, QuickSaleUser user) {

        // 1. get Redis cache
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class); // page-level cache
        if (!StringUtils.isEmpty(html)) {
            out(response, html);
            return null;
        }

        // 2. no cache, get it from database
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsList);

        // 3. creating cache of html and put it into Redis
        WebContext ctx = new WebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsList, "", html);
        }
        out(response, html);
        return null;
    }

    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response,
                                        Model model, QuickSaleUser user, @PathVariable("goodsId") long goodsId) {

        // Try with front-end and back-end separation
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        long start = goods.getStartDate().getTime();
        long end = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int quickSaleEventStatus = 0;
        int remainSeconds = 0;

        if (now < start) { // waiting for quick sale event
            quickSaleEventStatus = 0;
            remainSeconds = (int) ((start - now) / 1000); // counting down
        } else if (now > end) { // quick sale event expired
            quickSaleEventStatus = 2;
            remainSeconds = -1;
        } else { // quick sale event happening
            quickSaleEventStatus = 1;
            remainSeconds = 0;
        }

        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setUser(user);
        vo.setQuickSaleEventStatus(quickSaleEventStatus);
        vo.setRemainSeconds(remainSeconds);
        return Result.success(vo);
    }

    @RequestMapping(value = "/to_detailOld/{goodsId}", produces = "text/html")
    @ResponseBody
    public String detailOld(HttpServletRequest request, HttpServletResponse response,
                            Model model, QuickSaleUser user, @PathVariable("goodsId") long goodsId) {

        // 1. get Redis cache
        String html = redisService.get(GoodsKey.getGoodsDetails, "" + goodsId, String.class); // page-level cache
        if (!StringUtils.isEmpty(html)) {
            return html;
        }

        // 2. still need to get goods' info from database
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

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

        // 3. creating cache of html and put it into Redis
        WebContext ctx = new WebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsDetails, "" + goodsId, html);
        }
        return html;
    }

    public static void out(HttpServletResponse res, String html) {
        /**
         * Eliminate page problem of Thymeleaf
         */
        res.setContentType("text/html");
        res.setCharacterEncoding("UTF-8");
        try {
            OutputStream out = res.getOutputStream();
            out.write(html.getBytes("UTF-8"));
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
