package com.zhangfuwen.user;

/**
 * Created by dean on 3/26/17.
 */
public interface SecurityService {
    String findLoggedInUsername();

    void autologin(String username, String password);
}
