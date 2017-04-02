package com.zhangfuwen.webapp.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dean on 3/27/17.
 */
@Service
public class SysRoleService {
    @Autowired
    SysRoleRepository sysRoleRepository;
    SysRole getRoleById(Long id) {
        return sysRoleRepository.getOne(id);
    }
}
