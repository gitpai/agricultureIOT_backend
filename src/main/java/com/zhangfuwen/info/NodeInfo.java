package com.zhangfuwen.info;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by dean on 4/8/17.
 */
@Entity
@Table(name="t_nodeinfo")
public class NodeInfo {
    /**
     * Zigbee node address
     * 0x00~0x40
     */
    @Id
    @Column(name="id")
    Long id;

    @Column(name="gateway_id")
    Long gatewayId;

    @Column(name = "node_addr")
    byte nodeAddr;
    /**
     * User-defined zigbee node name or description string
     */
    @Column(name = "node_name")
    String nodeName;

    @Column(name="X")
    float X;

    @Column(name = "Y")
    float Y;
    @Column(name="desc_string")
    String desc;
    @Column(name="pic")
    String pic;
}
