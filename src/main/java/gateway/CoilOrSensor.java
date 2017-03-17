package gateway;

import javax.persistence.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dean on 2017/3/17.
 */ //开关量或传感器
@Entity
@Table(name="T_SENSOR")
class CoilOrSensor {
    @Id
    @GeneratedValue
    Long id;

    @Column(name="SENSOR_TYPE")
    byte sensorType;

    public Long getId() {
        return id;
    }

    public byte getSensorType() {
        return sensorType;
    }

    public byte getDataType() {
        return dataType;
    }

    public short getValue() {
        return value;
    }

    @Column(name="DATA_TYPE")
    byte dataType;

    @Column(name="VALUE")
    short value;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="NODE_ID")
    ZigbeeNode node;

    public static Map<Integer,String> sensorTypeMap = new HashMap<Integer,String>();
    static {
        //TODO: table not complete
        sensorTypeMap.put(0x01,"温度");
        sensorTypeMap.put(0x02,"湿度");
        sensorTypeMap.put(0x03,"照度");
    }

    public CoilOrSensor(){}
    public CoilOrSensor(ZigbeeNode node, byte[] data) throws IOException {
        this.node = node;
        this.sensorType = data[0];
        this.dataType = data[1];
        this.value = 0;
        this.value += (short) ((short) (data[2]) << 8);
        this.value += (short) (data[3]);
    }
    public String toString() {
        return "type\t:"+Integer.toHexString(sensorType) +", dataType:" +Integer.toHexString(this.dataType) +
                ", value\t:" + Integer.toHexString(value);
    }
}
