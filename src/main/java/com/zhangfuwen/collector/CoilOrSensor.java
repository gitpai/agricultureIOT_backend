package com.zhangfuwen.collector;

import javax.persistence.*;
import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import com.zhangfuwen.utils.Utils;

/**
 * Created by dean on 2017/3/17.
 */ //开关量或传感器
@Entity
@Table(name="t_sensor")
public class CoilOrSensor {
    public static Map<Byte,String> sensorTypeMap = new HashMap<Byte,String>();

    static {
        //TODO: table not complete
        sensorTypeMap = Utils.getSensorTypeMap();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="readout_id")
    Long id;
    @Column(name="channel")
    byte channel;
    @Column(name="sensor_type")
    byte sensorType;
    @Column(name="data_type")
    byte dataType;
    @Column(name="value")
    short value;

    @Column(name="node_addr")
    byte nodeAddr;

    @Column(name="gateway_id")
    Long gatewayId;

    @Column(name="created")
    Date created;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="id")
    ZigbeeNode node;

    public CoilOrSensor(){}

    public CoilOrSensor(ZigbeeNode node,byte channel, byte[] data) throws IOException {
        this.node = node;
        this.channel = channel;
        this.sensorType = data[0];
        this.dataType = data[1];
        this.value = 0;
        this.value += (short) ((short) (data[2] & 0xFF) << 8);
        this.value += (short) (data[3]&0xFF);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte getChannel() {
        return channel;
    }

    public void setChannel(byte channel) {
        this.channel = channel;
    }

    public byte getSensorType() {
        return sensorType;
    }

    public void setSensorType(byte sensorType) {
        this.sensorType = sensorType;
    }

    public byte getDataType() {
        return dataType;
    }

    public void setDataType(byte dataType) {
        this.dataType = dataType;
    }

    public short getValue() {
        return value;
    }

    public void setValue(short value) {
        this.value = value;
    }

    public String getSensorTypeString(){
        return sensorTypeMap.get(this.getSensorType());
    }

    public String getRealValue() {
        return Utils.toRealValue(this.dataType, this.value);

    }


    public String toString() {
        return "type\t:"+Integer.toHexString(sensorType) +", dataType:" +Integer.toHexString(this.dataType) +
                ", value\t:" + Integer.toHexString(value);
    }
}
