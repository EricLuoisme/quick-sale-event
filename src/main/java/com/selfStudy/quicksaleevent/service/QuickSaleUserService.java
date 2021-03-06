package com.selfStudy.quicksaleevent.service;

import com.alibaba.druid.util.StringUtils;
import com.selfStudy.quicksaleevent.dao.QuickSaleUserDao;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleUser;
import com.selfStudy.quicksaleevent.exception.GlobalException;
import com.selfStudy.quicksaleevent.redis.QuickSaleUserKey;
import com.selfStudy.quicksaleevent.redis.RedisService;
import com.selfStudy.quicksaleevent.utils.MD5Util;
import com.selfStudy.quicksaleevent.utils.UUIDUtil;
import com.selfStudy.quicksaleevent.vo.LoginVo;
import com.selfStudy.quicksaleevent.web.result.CodeMsg;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class QuickSaleUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    QuickSaleUserDao quickSaleUserDao;

    RedisService redisService; //injected by constructor

    public QuickSaleUserService(QuickSaleUserDao quickSaleUserDao, RedisService redisService) {
        this.quickSaleUserDao = quickSaleUserDao;
        this.redisService = redisService;
    }


    public QuickSaleUser getById(long id) {
        /**
         * Get user object from mysql database by userID (mobile number)
         */
        // 1. Try getting user from Redis
        QuickSaleUser user = redisService.get(QuickSaleUserKey.getById, "" + id, QuickSaleUser.class);
        if (user != null)
            return user;

        // 2. No cache, then search it from database
        user = quickSaleUserDao.getById(id);
        if (user != null)
            redisService.set(QuickSaleUserKey.getById, "" + id, user); // add cache
        return user;
    }

    public boolean updatePassword(String token, Long id, String passwordNew) {
        /**
         * Update User password
         */
        // 1. get the user object
        QuickSaleUser user = getById(id);
        if (user == null)
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);

        // 2. update database
        QuickSaleUser toBeUpdate = new QuickSaleUser(); // work as only updated stuff setted into it
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(passwordNew, user.getSalt()));
        quickSaleUserDao.update(toBeUpdate);

        // 3. Clean the Redis cache for the old user object
        redisService.delete(QuickSaleUserKey.getById, "" + id);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(QuickSaleUserKey.token, token, user);
        return true;
    }


    public QuickSaleUser getByToken(HttpServletResponse response, String token) {
        /**
         * Instead of just find user from communicate with mysql, we can use cookie to
         * set (key, value) pairs and find user object from Redis
         */
        if (StringUtils.isEmpty(token))
            return null;
        QuickSaleUser user = redisService.get(QuickSaleUserKey.token, token, QuickSaleUser.class); // object-level cache
        if (user != null)
            addCookie(response, token, user); // extend cookie's expire date after user login
        return user;
    }


    public String login(HttpServletResponse response, LoginVo loginVo) {
        /**
         * User login logic
         */
        if (loginVo == null)
            throw new GlobalException(CodeMsg.SERVER_ERROR);

        // 1. check mobile exists in database
        QuickSaleUser quickSaleUser = getById(Long.parseLong(loginVo.getMobile()));
        if (quickSaleUser == null)
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);

        // 2. validate password (the second MD5)
        String dbRealPass = quickSaleUser.getPassword();
        String dbSalt = quickSaleUser.getSalt();
        String calcPass = MD5Util.formPassToDBPass(loginVo.getPassword(), dbSalt);
        if (!calcPass.equals(dbRealPass))
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);

        // 3. Create Cookie
        String token = UUIDUtil.uuid();
        addCookie(response, token, quickSaleUser); // also add token into Redis
        return token;
    }

    private void addCookie(HttpServletResponse response, String token, QuickSaleUser quickSaleUser) {
        /**
         * For adding cookie to the browser, also add token, object pair into Redis
         */
        redisService.set(QuickSaleUserKey.token, token, quickSaleUser); // set it into Redis
        // (key, value) = (QuickSaleUserKey:tk + token, quickSaleUser) then
        // server can search by this key, to know which who are visiting the site now
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(QuickSaleUserKey.token.expireSeconds()); // we also have expire time for the key name
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
