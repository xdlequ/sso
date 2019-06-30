package com.sso.Controller;

import com.sso.Utils.LoginCache;
import com.sso.pojo.User;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ql on 2019/6/30.
 */

@Controller
@RequestMapping("/login")
public class LoginController {

    private static Set<User> users;

    static{
        users=new HashSet<>();
        users.add(new User(0,"zhangsan","123456"));
        users.add(new User(1,"lisi","123456"));
        users.add(new User(2,"wangwu","123456"));
    }
    @PostMapping
    public String doLogin(User user,HttpSession session, HttpServletResponse response){
        String target= (String) session.getAttribute("target");
        //模拟数据库与登录用户信息校验的过程，找到第一个符合条件的用户
        Optional<User>first=users.stream().filter(users->users.getUsername().equals(user.getUsername()
        )&&users.getPassword().equals(user.getPassword())).findFirst();

        if (first.isPresent()){
            //登录成功
            String token= UUID.randomUUID().toString();
            //将token加入到cookie中
            Cookie cookie=new Cookie("Token",token);
            cookie.setMaxAge(6000);
            //设置cookie的域，防止跨域现象存在
            cookie.setDomain("codeshop.com");
            response.addCookie(cookie);
            //类似于redis，起到了暂时存储的作用
            LoginCache.loginCache.put(token,first.get());
        }else{
            session.setAttribute("msg","用户名或密码错误");
            return "login";
        }
        return "redirect:"+target;
    }

    /**
     * 给其他系统开放的接口，根据token获取用户登录信息
     * @param token
     * @return
     */
    @GetMapping("/info")
    @ResponseBody
    public ResponseEntity<User> getInfo(String token){
        if (!StringUtils.isEmpty(token)){
            User user=LoginCache.loginCache.get(token);
            return ResponseEntity.ok(user);
        }else{
            return new ResponseEntity<>((User) null, HttpStatus.BAD_REQUEST);
        }
    }
}
