package com.zhangfuwen.user;

/**
 * Created by dean on 3/26/17.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
class UserServiceImpl implements UserService {
    @Autowired
    private SysUserRepository userRepository;
    @Autowired
    private SysRoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void save(SysUser user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        //user.setRoles(new ArrayList<SysRole>(roleRepository.findAll()));
        userRepository.save(user);
    }

    @Override
    public List<SysUser> listUsers(){
        return userRepository.findAll();
    }
    @Override
    public boolean deleteByUsername(String username) {
        userRepository.delete(userRepository.findByUsername(username).getId());
        return true;
    }

    @Override
    public SysUser findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}

