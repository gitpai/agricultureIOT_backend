package com.agriculture.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.agriculture.models.Gateway;

/**
 * Created by dean on 4/3/17.
 */
public interface GatewayRepository extends PagingAndSortingRepository<Gateway, Long> {
}
