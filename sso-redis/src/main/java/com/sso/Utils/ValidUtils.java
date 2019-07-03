package com.sso.Utils;

import com.sso.Entity.User;
import org.springframework.util.DigestUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by ql on 2019/7/3.
 */
public class ValidUtils {

    public static void entryptPassword(User user){
        String salt= UUID.randomUUID().toString();
        String temPassword=salt+user.getPassword();
        String md5Password= DigestUtils.md5DigestAsHex(temPassword.getBytes());
        user.setSalt(salt);
        user.setPassword(md5Password);
    }

    public static boolean decryptPassword(User user,String plainPassword){
        String tempPassword=user.getSalt()+plainPassword;
        String md5Password=DigestUtils.md5DigestAsHex(tempPassword.getBytes());
        return user.getPassword().equals(md5Password);
    }

    public static String getCurrentTime(){
        TimeZone zone=TimeZone.getTimeZone("Asia/Shanghai");
        TimeZone.setDefault(zone);
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }
}
