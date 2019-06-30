package com.sso.Contorller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 *
 * @author ql
 * @date 2019/6/30
 */



@Controller
@RequestMapping("/view")
public class ViewController {

    @Autowired
    private RestTemplate restTemplate;

    private String LOGIN_INFO_ACCESS="http://login.codeshop.com:9000/login/info?token=";
    @GetMapping("/index")
    public String toIndex(@CookieValue(value = "Token",required = false)Cookie cookie,
                          HttpSession session){
        if (cookie!=null) {
            String token = cookie.getValue();
            if (!StringUtils.isEmpty(token)) {
                Map result = restTemplate.getForObject(LOGIN_INFO_ACCESS + token, Map.class);
                session.setAttribute("loginUser", result);
            }
        }
        return "index";
    }
    @GetMapping("/logout")
    public String logOut(@CookieValue(value = "Token") Cookie cookie,HttpServletResponse response,HttpSession session){
        if (cookie!=null){
            cookie.setMaxAge(0);
            session.setAttribute("loginUser",null);
        }
        return "redirect:http://www.codeshop.com:9010/view/index";
    }
}
