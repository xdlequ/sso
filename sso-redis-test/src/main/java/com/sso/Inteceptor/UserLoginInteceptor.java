package com.sso.Inteceptor;

import com.sso.Entity.User;
import com.sso.Service.UserService;
import com.sso.Util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义登录拦截器，用于判断用户是否已经在sso系统登录过
 * Created by ql on 2019/7/4.
 */
@Component
public class UserLoginInteceptor implements HandlerInterceptor{
    @Autowired
    private UserService userService;
    public static final String COOKIE_NAME="USER_TOKEN";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String token= CookieUtil.getCookieValue(request,COOKIE_NAME);
        User user=userService.getUserByToken(token);
        if (StringUtils.isEmpty(token)&&user==null){
            //跳转到sso系统，请求登录 把用户请求的url作为参数传递给登录页面。
            response.sendRedirect("http://localhost:8081/login?redirect="+request.getRequestURL());

            //返回false，代表请求被拦截
            return false;
        }
        //再次请求返回时，已经获得user，将user信息放入request中，拦截器放行
        request.setAttribute("user",user);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
