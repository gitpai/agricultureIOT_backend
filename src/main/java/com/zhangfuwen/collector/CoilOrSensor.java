package com.zhangfuwen.collector;

import javax.persistence.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dean on 2017/3/17.
 */ //开关量或传感器
@Entity
@Table(name="t_sensor")
public class CoilOrSensor {
    public static Map<Integer,String> sensorTypeMap = new HashMap<Integer,String>();

    static {
        //TODO: table not complete
        sensorTypeMap.put(0x01,"温度");
        sensorTypeMap.put(0x02,"湿度");
        sensorTypeMap.put(0x03,"照度");
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


    public String toString() {
        return "type\t:"+Integer.toHexString(sensorType) +", dataType:" +Integer.toHexString(this.dataType) +
                ", value\t:" + Integer.toHexString(value);
    }
}
