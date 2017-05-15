package com.agriculture.repositories.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agriculture.models.user.SysUser;

/**
 * Created by dean on 3/26/17.
 */
public interface SysUserRepository extends JpaRepository<SysUser, Long> {
    SysUser findByUsername(String username);
}
