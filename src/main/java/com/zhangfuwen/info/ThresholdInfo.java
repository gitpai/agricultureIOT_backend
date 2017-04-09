package com.zhangfuwen.info;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by dean on 4/8/17.
 */
@Entity
@Table(name="t_thresholdinfo")
public class ThresholdInfo {
    @Id
    @Column(name = "id")
    Long id;

    @Column(name = "gateway_id")
    Long gatewayId;

    @Column(name="node_addr")
    Byte nodeAddr;

    @Column(name="channel")
    Byte channel;

    @Column(name="upper_limit")
    int upperLimit;

    @Column(name="lower_limit")
    int lowerLimit;
}
