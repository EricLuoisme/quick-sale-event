package com.selfStudy.quicksaleevent.web.controller;

import com.alibaba.druid.util.StringUtils;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleUser;
import com.selfStudy.quicksaleevent.redis.GoodsKey;
import com.selfStudy.quicksaleevent.redis.RedisService;
import com.selfStudy.quicksaleevent.service.GoodsService;
import com.selfStudy.quicksaleevent.service.QuickSaleUserService;
import com.selfStudy.quicksaleevent.vo.GoodsVo;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController extends BaseController {

    QuickSaleUserService userService; // autowired by constructor

    RedisService redisService;

    GoodsService goodsService;

    ThymeleafViewResolver thymeleafViewResolver;

    ApplicationContext applicationContext;

    public GoodsController(ThymeleafViewResolver thymeleafViewResolver, RedisService redisService, QuickSaleUserService userService, RedisService redisService1, GoodsService goodsService, ThymeleafViewResolver thymeleafViewResolver1, ApplicationContext applicationContext) {
        super(thymeleafViewResolver, redisService);
        this.userService = userService;
        this.redisService = redisService1;
        this.goodsService = goodsService;
        this.thymeleafViewResolver = thymeleafViewResolver1;
        this.applicationContext = applicationContext;
    }

    @RequestMapping(value = "/to_list", produces = "text/html")
    public String list(HttpServletRequest request, HttpServletResponse response, Model model, QuickSaleUser user) {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsList);
        return render(request, response, model, "goods_list", GoodsKey.getGoodsList, "");
    }

    @RequestMapping(value = "/to_detail/{goodsId}", produces = "text/html")
    @ResponseBody
    public String detail(HttpServletRequest request, HttpServletResponse response,
                         Model model, QuickSaleUser user, @PathVariable("goodsId") long goodsId) {

        // get Redis cache
        String html = redisService.get(GoodsKey.getGoodsDetails, "" + goodsId, String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        // creating the html cache manually
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

//         creating cache of html and put it into Redis
        WebContext ctx = new WebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsDetails, "" + goodsId, html);
        }
        return html;

//        return "goods_detail";
    }
}
