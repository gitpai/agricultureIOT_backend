package com.zhangfuwen.gateway;

/**
 * Created by dean on 2017/3/17.
 */


import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "sensors", path = "sensors")
public interface CoilOrSensorRepository extends PagingAndSortingRepository<CoilOrSensor, Long> {

}