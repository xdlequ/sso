package com.sso.Service.Impl;

import com.sso.Entity.Result;
import com.sso.Entity.User;
import com.sso.Service.UserService;
import com.sso.Util.HttpClientUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

/**
 * Created by ql on 2019/7/4.
 */
@Service
@PropertySource("classpath:db.properties")
public class UserServiceImpl implements UserService {
    @Value("${SSO_BASE_URL}")
    public String SSO_BASE_URL;

    @Value("${SSO_DOMAIN_BASE_USRL}")
    public String SSO_DOMAIN_BASE_USRL;

    @Value("${SSO_USER_TOKEN}")
    public String SSO_USER_TOKEN;

    @Value("${SSO_PAGE_LOGIN}")
    public String SSO_PAGE_LOGIN;

    @Override
    public User getUserByToken(String token) {
        try {
            //调用SSO系统的服务，根据token获取用户信息
            String json= HttpClientUtil.doGet(
                    SSO_BASE_URL+SSO_USER_TOKEN+token);
            System.out.println("json:"+json);
            //将json转化为相应的实体对象
            Result result=Result.formatToEntity(json,User.class);
            if (result!=null&&result.getStatus()==200){
                User user= (User) result.getData();
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
