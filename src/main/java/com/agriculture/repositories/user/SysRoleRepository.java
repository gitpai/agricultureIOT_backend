package com.agriculture.repositories.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agriculture.models.user.SysRole;

/**
 * Created by dean on 3/26/17.
 */
public interface SysRoleRepository extends JpaRepository<SysRole, Long> {
}
