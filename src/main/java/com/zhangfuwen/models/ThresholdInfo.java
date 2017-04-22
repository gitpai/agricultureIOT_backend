package com.zhangfuwen.models;

import javax.persistence.*;

/**
 * Created by dean on 4/8/17.
 */
@Entity
@Table(name="t_thresholdinfo")
public class ThresholdInfo {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(Long gatewayId) {
        this.gatewayId = gatewayId;
    }

    public Byte getNodeAddr() {
        return nodeAddr;
    }

    public void setNodeAddr(Byte nodeAddr) {
        this.nodeAddr = nodeAddr;
    }

    public Byte getChannel() {
        return channel;
    }

    public void setChannel(Byte channel) {
        this.channel = channel;
    }

    public int getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(int upperLimit) {
        this.upperLimit = upperLimit;
    }

    public int getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(int lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    @Override
    public String toString() {
        return "ThresholdInfo{" +
                "id=" + id +
                ", gatewayId=" + gatewayId +
                ", nodeAddr=" + nodeAddr +
                ", channel=" + channel +
                ", upperLimit=" + upperLimit +
                ", lowerLimit=" + lowerLimit +
                '}';
    }
}
