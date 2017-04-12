package com.zhangfuwen.info;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.persistence.*;

/**
 * Created by dean on 4/8/17.
 */
@Entity
@Table(name = "t_nodeinfo")
public class NodeInfo {
    /**
     * Zigbee node address
     * 0x00~0x40
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "gateway_id")
    Long gatewayId;

    @Column(name = "node_addr")
    byte nodeAddr;
    /**
     * User-defined zigbee node name or description string
     */
    @Column(name = "node_name")
    String nodeName;

    @Column(name = "X")
    float X;

    @Column(name = "Y")
    float Y;
    @Column(name = "desc_string")
    String desc;
    @Column(name = "pic")
    String pic;

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

    public byte getNodeAddr() {
        return nodeAddr;
    }

    public void setNodeAddr(byte nodeAddr) {
        this.nodeAddr = nodeAddr;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public float getX() {
        return X;
    }

    public void setX(float x) {
        X = x;
    }

    public float getY() {
        return Y;
    }

    public void setY(float y) {
        Y = y;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
