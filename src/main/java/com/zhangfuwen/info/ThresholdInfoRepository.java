package com.zhangfuwen.info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by dean on 4/8/17.
 */
public interface ThresholdInfoRepository extends JpaRepository<ThresholdInfo, Long> {
}
