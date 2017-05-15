package com.agriculture.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.agriculture.models.CoilOrSensor;
import com.agriculture.models.ZigbeeNode;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "devices", path = "devices")
public interface CoilOrSensorRepository extends PagingAndSortingRepository<CoilOrSensor, Long> {
    List<CoilOrSensor> findAllByNode(@Param("node") ZigbeeNode node);
    List<CoilOrSensor> findByGatewayIdAndNodeAddrAndChannelAndCreatedAfter(
            @Param("gatewayId") Long gatewayId,
            @Param("nodeAddr") Byte nodeAddr,
            @Param("channel") Byte channel,
            @Param("since") Date since
    );
    List<CoilOrSensor> findByGatewayIdAndNodeAddrAndChannelAndCreatedBetween(
            @Param("gatewayId") Long gatewayId,
            @Param("nodeAddr") Byte nodeAddr,
            @Param("channel") Byte channel,
            @Param("start") Timestamp start,
            @Param("end") Timestamp end
    );

    List<CoilOrSensor> findTop30ByGatewayIdAndNodeAddrAndChannelOrderByCreatedDesc(
            @Param("gatewayId") Long gatewayId,
            @Param("nodeAddr") Byte nodeAddr,
            @Param("channel") Byte channel
    );



}