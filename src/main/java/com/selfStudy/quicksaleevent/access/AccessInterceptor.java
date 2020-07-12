package com.selfStudy.quicksaleevent.access;

import com.selfStudy.quicksaleevent.domain.model.QuickSaleUser;
import com.selfStudy.quicksaleevent.redis.AccessKey;
import com.selfStudy.quicksaleevent.redis.RedisService;
import com.selfStudy.quicksaleevent.service.QuickSaleUserService;


import java.io.OutputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.selfStudy.quicksaleevent.web.result.CodeMsg;
import com.selfStudy.quicksaleevent.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;

@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

    /**
     * An interceptor, to check and handle what to do when we meet @AccessLimit annotation
     */

    QuickSaleUserService userService;

    RedisService redisService;

    public AccessInterceptor(QuickSaleUserService userService, RedisService redisService) {
        this.userService = userService;
        this.redisService = redisService;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {

            // 1. get handler and set user object into thread-wise variable
            QuickSaleUser user = getUser(request, response); // get user object
            UserContext.setUser(user); // set a thread-wise variable
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null) {
                return true; // no use of @AccessLimit annotation then let it go
            }

            // 2. take care of using the @AccessLimit
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if (needLogin) {
                if (user == null) {
                    render(response, CodeMsg.SESSION_ERROR); // response error msg to page
                    return false; // cannot go into the page if user is empty
                }
                key += "_" + user.getId(); // the User Token we set into Redis is this key
            } else {
                // do nothing if user's login is not necessary (so far)
            }

            // 3. check how many time does the user log in
            AccessKey ak = AccessKey.withExpire(seconds);
            Integer count = redisService.get(ak, key, Integer.class);
            if (count == null) {
                redisService.set(ak, key, 1); // first login
            } else if (count < maxCount) {
                redisService.incr(ak, key); // increase login time
            } else {
                render(response, CodeMsg.ACCESS_LIMIT_REACHED); // max login time reach
                return false;
            }
        }
        return true;
    }

    private QuickSaleUser getUser(HttpServletRequest request, HttpServletResponse response) {
        /**
         * For get User info from Token and Cookie, then use these to extract user object from Redis cache
         */
        String paramToken = request.getParameter(QuickSaleUserService.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request, QuickSaleUserService.COOKIE_NAME_TOKEN);
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        return userService.getByToken(response, token);
    }

    private void render(HttpServletResponse response, CodeMsg cm) throws Exception {
        /**
         * For outputting msg to page
         */
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(Result.error(cm)); // get error msg
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

    private String getCookieValue(HttpServletRequest request, String cookiName) {
        /**
         * Get User's Cookie
         */
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length <= 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookiName)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
