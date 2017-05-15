package com.agriculture.services.user;

import java.util.List;

import com.agriculture.models.user.SysUser;

/**
 * Created by dean on 3/26/17.
 */
public interface UserService {
    void save(SysUser user);
    boolean deleteByUsername(String username);

    SysUser findByUsername(String username);
    List<SysUser> listUsers();
    boolean resetPassword(String username, String password);
    boolean varifyPassword(String old, String newPassword);
}
