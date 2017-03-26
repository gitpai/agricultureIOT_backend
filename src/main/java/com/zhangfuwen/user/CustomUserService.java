package com.zhangfuwen.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Created by dean on 3/26/17.
 */
public class CustomUserService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(CustomUserService.class);
    @Autowired
    SysUserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        SysUser user = userRepository.findByUsername(s);
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        logger.debug("reqeusted user name %s",s);
        logger.debug("checked out username:%s, password:%s", user.getUsername(), user.getPassword());
        return user;
    }
}