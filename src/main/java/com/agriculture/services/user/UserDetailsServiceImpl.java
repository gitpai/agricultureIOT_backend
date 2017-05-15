package com.agriculture.services.user;

import com.agriculture.models.user.SysUser;
import com.agriculture.repositories.user.SysUserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by dean on 3/26/17.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    @Autowired
    SysUserRepository userRepository;
    @Override
    @Transactional( readOnly = true)
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        logger.info("reqeusted user name "+s);
        SysUser user = userRepository.findByUsername(s);
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        logger.info(String.format("checked out username:%s, password:%s", user.getUsername(), user.getPassword()));
        return user;
    }
}