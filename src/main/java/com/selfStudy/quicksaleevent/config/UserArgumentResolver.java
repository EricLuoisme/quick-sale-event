package com.selfStudy.quicksaleevent.config;

import com.alibaba.druid.util.StringUtils;
import com.selfStudy.quicksaleevent.access.UserContext;
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
    /**
     * By using this resolver, we do not need to extract user from HTTP request
     * or Cookies or Redis in every web controller
     */

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
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        // Interceptor would perform operations first,
        // which means we can get these created thread-wise variables, no longer need to retrieve from Redis
        return UserContext.getUser();
    }
}
