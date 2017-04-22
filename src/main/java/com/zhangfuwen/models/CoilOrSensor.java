package com.zhangfuwen.models;

import javax.persistence.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import com.zhangfuwen.repositories.ThresholdInfoRepository;
import com.zhangfuwen.repositories.WarningRepository;
import com.zhangfuwen.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by dean on 2017/3/17.
 */ //开关量或传感器
@Entity
@Table(name = "t_sensor")
public class CoilOrSensor {
    private static final Logger logger = LoggerFactory.getLogger(CoilOrSensor.class);
    public static Map<Byte, String> sensorTypeMap = new HashMap<Byte, String>();

    static {
        //TODO: table not complete
        sensorTypeMap = Utils.getSensorTypeMap();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "readout_id")
    Long id;
    @Column(name = "channel")
    byte channel;
    @Column(name = "sensor_type")
    byte sensorType;
    @Column(name = "data_type")
    byte dataType;
    @Column(name = "value")
    short value;

    @Column(name = "node_addr")
    public byte nodeAddr;

    @Column(name = "gateway_id")
    public Long gatewayId;

    @Column(name = "created")
    public Timestamp created;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    ZigbeeNode node;

    @Transient
    public Gateway gateway;


    public CoilOrSensor() {
    }


    public void persistHook(ThresholdInfoRepository thresholdInfoRepository,
                            WarningRepository warningRepository) {
        ThresholdInfo info = thresholdInfoRepository.findOneByGatewayIdAndNodeAddrAndChannel(this.gatewayId, this.nodeAddr, this.channel);
        if (info != null) {
            logger.info("got threshold validators " + info.toString());
            logger.info("sensor is " + this.toString());

            Float value = Float.valueOf(this.getRealValue());
            if (value > info.getUpperLimit()) {
                Warning w = new Warning(info.getId(), Warning.WARN_TYPE_UPPER_LIMIT, Warning.WARN_STATUS_NEW, this.getId());
                warningRepository.save(w);
            } else if (value < info.getLowerLimit()) {
                Warning w = new Warning(info.getId(), Warning.WARN_TYPE_LOWER_LIMIT, Warning.WARN_STATUS_NEW, this.getId());
                warningRepository.save(w);
            }

        }
    }

    public CoilOrSensor(ZigbeeNode node, byte channel, byte[] data) throws IOException {
        this.node = node;
        this.channel = channel;
        this.sensorType = data[0];
        this.dataType = data[1];
        this.value = 0;
        this.value += (short) ((short) (data[2] & 0xFF) << 8);
        this.value += (short) (data[3] & 0xFF);
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

    public String getSensorTypeString() {
        return sensorTypeMap.get(this.getSensorType());
    }

    public String getRealValue() {
        return Utils.toRealValue(this.dataType, this.value);

    }

    @Override
    public String toString() {
        return "CoilOrSensor{" +
                "id=" + id +
                ", channel=" + channel +
                ", sensorType=" + sensorType +
                ", dataType=" + dataType +
                ", value=" + value +
                ", nodeAddr=" + nodeAddr +
                ", gatewayId=" + gatewayId +
                ", created=" + created +

                '}';
    }
}
