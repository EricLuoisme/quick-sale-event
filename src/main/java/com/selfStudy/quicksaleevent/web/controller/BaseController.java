package com.selfStudy.quicksaleevent.web.controller;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.selfStudy.quicksaleevent.redis.KeyPrefix;
import com.selfStudy.quicksaleevent.redis.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

@Controller
public class BaseController {

    // make page cache enable
    @Value("#{'${pageCache.enable}'}")
    private boolean pageCacheEnable;

    ThymeleafViewResolver thymeleafViewResolver;

    RedisService redisService;

    public BaseController(ThymeleafViewResolver thymeleafViewResolver, RedisService redisService) {
        this.thymeleafViewResolver = thymeleafViewResolver;
        this.redisService = redisService;
    }

    public String render(HttpServletRequest request, HttpServletResponse response,
                         Model model, String tplName, KeyPrefix prefix, String key) {
        if (!pageCacheEnable) {
            return tplName;
        }
        // try getting cache from Redis
        String html = redisService.get(prefix, key, String.class);
        if (!StringUtils.isEmpty(html)) {
            out(response, html);
            return null;
        }
        // creating the html cache manually
        WebContext ctx = new WebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process(tplName, ctx);
        if (!StringUtils.isEmpty(html)) {
            redisService.set(prefix, key, html);
        }
        out(response, html);
        return null;
    }

    public static void out(HttpServletResponse res, String html) {
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
