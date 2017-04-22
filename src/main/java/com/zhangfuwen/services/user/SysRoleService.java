package com.zhangfuwen.services.user;

import com.zhangfuwen.repositories.user.SysRoleRepository;
import com.zhangfuwen.models.user.SysRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dean on 3/27/17.
 */
@Service
public class SysRoleService {
    @Autowired
    public SysRoleRepository sysRoleRepository;
    public SysRole getRoleById(Long id) {
        return sysRoleRepository.getOne(id);
    }
}
