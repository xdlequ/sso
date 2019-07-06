package com.sso.Service;

import com.sso.Dao.JedisClient;
import com.sso.Dao.UserRepository;
import com.sso.Entity.Result;
import com.sso.Entity.User;
import com.sso.Utils.CookieUtil;
import com.sso.Utils.JsonUtil;
import com.sso.Utils.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 *
 * @author ql
 * @date 2019/7/3
 */
@Service
@Transactional
@PropertySource(value = "classpath:redis.properties")
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JedisClient jedisClient;

    @Value("${REDIS_USER_SESSION_KEY}")
    private String REDIS_USER_SESSION_KEY;
    @Value("${SSO_SESSION_EXPIRE}")
    private Integer SSO_SESSION_EXPIRE;

    public Result registerUser(User user){
        // 检查用户名是否注册，一般在前端验证的时候处理，
        // 因为注册不存在高并发的情况，这里再加一层查询是不影响性能的
        if (userRepository.findByAccout(user.getAccount())!=null){
            return Result.build(400,"该用户名已被注册");
        }
        //数据库方面存储用户
        userRepository.save(user);
        //注册成功之后选择发送邮件激活，现在一般使用短信验证码。
        return Result.build(200,"注册成功");
    }

    public Result userLogin(String account, String password,
                            HttpServletRequest request, HttpServletResponse response){
        //判断账户是否正确
        User user=userRepository.findByAccout(account);
        if (!ValidUtils.decryptPassword(user,user.getPlainPassword())){
          //验证不通过
            return Result.build(400,"用户名或密码不正确");
        }
        //生成token
        String token= UUID.randomUUID().toString();
        //清空salt和密码，避免泄露
        String userPassword=user.getPassword();
        String salt=user.getSalt();
        user.setPassword(null);
        user.setSalt(null);
        //将用户信息写入redis
        jedisClient.set(REDIS_USER_SESSION_KEY+":"+token, JsonUtil.objectToJson(user));
        //user已经是持久化对象了，被保存在了session缓存当中，
        // 若user又重新修改了属性值，那么在提交事务时，
        // 此时 hibernate对象就会拿当前这个user对象和保存在session缓存中
        // 的user对象进行比较，如果两个对象相同，则不会发送update语句，否则，如果两个对象不同，则会发出update语句。
        user.setPassword(userPassword);
        user.setSalt(salt);
        //设置session的过期时间
        jedisClient.expire(REDIS_USER_SESSION_KEY+":"+token,SSO_SESSION_EXPIRE);
        //添加写cookie的逻辑，cookie的有效期是关闭浏览器就失效
        CookieUtil.setCookie(request,response,"USER_TOKEN",token);
        //返回token
        return Result.ok(token);
    }

    public void logout(String token){
        jedisClient.del(REDIS_USER_SESSION_KEY+":"+token);
    }

    public Result queryUserByToken(String token) {
        //根据token从redis中查询用户信息
        String json=jedisClient.get(REDIS_USER_SESSION_KEY);
        if (!StringUtils.isEmpty(json)){
            return Result.build(400,"已过期，请重新登录");
        }
        //更新过期时间
        jedisClient.expire(REDIS_USER_SESSION_KEY+":"+token,SSO_SESSION_EXPIRE);
        //返回用户信息
        return Result.ok(JsonUtil.JsonToEntity(json,User.class));
    }


}
