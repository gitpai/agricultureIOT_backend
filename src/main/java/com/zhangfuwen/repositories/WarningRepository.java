package com.zhangfuwen.repositories;

import com.zhangfuwen.models.Warning;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by dean on 4/8/17.
 */
public interface WarningRepository extends JpaRepository<Warning, Long> {
    //List<Warning> findWarnStatusOutdatedByOrderByCreatedDesc();
    List<Warning> findClosedIsNullByOrderByCreatedDesc();
    List<Warning> findClosedIsNotNullByOrderByCreatedDesc();
}
