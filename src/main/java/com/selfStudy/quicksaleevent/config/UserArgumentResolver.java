package com.selfStudy.quicksaleevent.config;

import com.alibaba.druid.util.StringUtils;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleUser;
import com.selfStudy.quicksaleevent.service.QuickSaleUserService;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    QuickSaleUserService quickSaleUserService;

    public UserArgumentResolver(QuickSaleUserService quickSaleUserService) {
        this.quickSaleUserService = quickSaleUserService;
    }


    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> clazz = methodParameter.getParameterType();
        return clazz == QuickSaleUser.class;
    }


    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) throws Exception {

        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);

        String paramToken = request.getParameter(QuickSaleUserService.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request, QuickSaleUserService.COOKIE_NAME_TOKEN);

        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken))
            return null;

        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        QuickSaleUser user = quickSaleUserService.getByToken(response, token);
        return user;
    }

    private String getCookieValue(HttpServletRequest request, String cookieNameToken) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length <= 0)
            return null;
        for (Cookie ck : cookies) {
            if (ck.getName().equals(cookieNameToken))
                return ck.getValue();
        }
        return null;
    }
}
