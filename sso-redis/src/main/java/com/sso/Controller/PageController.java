package com.sso.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by ql on 2019/7/4.
 */
@Controller
public class PageController {

    @RequestMapping("/login")
    public String showLogin(String redirect,Model model){
        model.addAttribute("redirect",redirect);
        return "login";
    }
}
