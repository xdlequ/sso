package com.sso.Controller;

import com.sso.Utils.LoginCache;
import com.sso.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;
import sun.swing.StringUIClientPropertyKey;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 跳转到登录页面的逻辑
 * @author ql
 * @date 2019/6/30
 */
@Controller
@RequestMapping("/view")
public class ViewController {

    @GetMapping("/login")
    public String toLogin(@RequestParam(required = false,defaultValue = "") String target,
                          HttpSession session, @CookieValue(required = false,value = "Token") Cookie cookie){
        if (StringUtils.isEmpty(target)){
            //未给出target的时候，直接跳转到主页
            target="http://www.codeshop.com:9010/view/index";
        }
        if (cookie!=null){
            String value=cookie.getValue();
            if (!StringUtils.isEmpty(value)){
                //已经登录的用户再次登录访问，直接重定向
                User user= LoginCache.loginCache.get(value);
                return "redirect:"+target;
            }
        }

        //TODO：需要做target是否合法的验证
        //重定向地址target
        session.setAttribute("target",target);
        return "login";
    }
}
