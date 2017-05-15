package com.agriculture.services.user;

import com.agriculture.models.user.SysRole;
import com.agriculture.repositories.user.SysRoleRepository;

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
