package com.sso.Controller;

import com.sso.Entity.Result;
import com.sso.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ql
 * @date 2019/7/4
 */

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/login")
    @ResponseBody
    public Result userLogin(HttpServletResponse response, HttpServletRequest request,
                            String password,String account){
        Result result= null;
        try {
            result = userService.userLogin(account,password,request,response);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return Result.build(500,"服务器内部错误");
        }
    }

    @RequestMapping(value = "/logout/{token}")
    public String logout(@PathVariable("token")String token){
        userService.logout(token);
        return "index";
    }

    @RequestMapping(value = "/token/{token}")
    @ResponseBody
    public Object getUserByToken(@PathVariable String token){
        Result result=null;
        try {
            result=userService.queryUserByToken(token);

        } catch (Exception e) {
            e.printStackTrace();
            result=Result.build(500,"服务器内部错误");
        }
        return result;
    }
}
