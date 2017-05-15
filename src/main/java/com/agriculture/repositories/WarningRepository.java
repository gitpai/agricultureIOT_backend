package com.agriculture.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agriculture.models.Warning;

import java.util.List;

/**
 * Created by dean on 4/8/17.
 */
public interface WarningRepository extends JpaRepository<Warning, Long> {
    //List<Warning> findWarnStatusOutdatedByOrderByCreatedDesc();
    List<Warning> findClosedIsNullByOrderByCreatedDesc();
    List<Warning> findClosedIsNotNullByOrderByCreatedDesc();
}
