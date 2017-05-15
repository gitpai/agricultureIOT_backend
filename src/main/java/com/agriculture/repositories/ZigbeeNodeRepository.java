package com.agriculture.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.agriculture.models.Gateway;
import com.agriculture.models.ZigbeeNode;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "nodes", path = "nodes")
public interface ZigbeeNodeRepository extends PagingAndSortingRepository<ZigbeeNode, Long> {
    List<ZigbeeNode> findTop1ByOrderByIdDesc();
    List<ZigbeeNode> findByNodeAddr(@Param("addr") Byte nodeAddr);
    //List<ZigbeeNode> findTop20ByNodeAddrOOrderByIdDesc(@Param("addr") Byte nodeAddr);
    List<ZigbeeNode> findTop1ByNodeAddrOrderByIdDesc(@Param("addr") Byte nodeAddr);
    List<ZigbeeNode> findTop1ByNodeAddrAndGatewayOrderByIdDesc(
            @Param("addr") Byte nodeAddr,
            @Param("gateway") Gateway gateway);
}