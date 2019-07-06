package com.sso.Service;

import com.sso.Entity.User;

/**
 * Created by ql on 2019/7/4.
 */
public interface UserService {
    User getUserByToken(String token);
}
