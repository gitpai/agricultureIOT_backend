package com.zhangfuwen.repositories;

import com.zhangfuwen.models.NodeInfo;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by dean on 4/8/17.
 */
public interface NodeInfoRepository extends PagingAndSortingRepository<NodeInfo, Byte> {
    List<NodeInfo> findByGatewayId(@Param("id") Long gatewayId);
    NodeInfo findTop1ByGatewayIdAndNodeAddr(@Param("gatewayid") Long gatewayId, @Param("nodeaddr") Byte nodeAddr);
}
