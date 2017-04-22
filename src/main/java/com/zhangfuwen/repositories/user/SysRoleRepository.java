package com.zhangfuwen.repositories.user;

import com.zhangfuwen.models.user.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by dean on 3/26/17.
 */
public interface SysRoleRepository extends JpaRepository<SysRole, Long> {
}
