package com.zhangfuwen.repositories.user;

import com.zhangfuwen.models.user.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by dean on 3/26/17.
 */
public interface SysUserRepository extends JpaRepository<SysUser, Long> {
    SysUser findByUsername(String username);
}
