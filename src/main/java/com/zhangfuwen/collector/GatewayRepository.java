package com.zhangfuwen.collector;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by dean on 4/3/17.
 */
public interface GatewayRepository extends PagingAndSortingRepository<Gateway, Long> {
}
