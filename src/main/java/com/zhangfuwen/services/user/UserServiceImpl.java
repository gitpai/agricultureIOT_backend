package com.zhangfuwen.services.user;

/**
 * Created by dean on 3/26/17.
 */

import com.zhangfuwen.repositories.user.SysRoleRepository;
import com.zhangfuwen.repositories.user.SysUserRepository;
import com.zhangfuwen.models.user.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
    public boolean resetPassword(String username, String password)
    {
        SysUser user = userRepository.findByUsername(username);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
        return true;
    }
    @Override
    public boolean varifyPassword(String old, String encPassword){
        return bCryptPasswordEncoder.matches(old, encPassword);
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
