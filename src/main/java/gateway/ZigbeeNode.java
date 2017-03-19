package gateway;

import javax.persistence.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by dean on 2017/3/17.
 */
@Entity
@Table(name = "t_zigbee_node")
class ZigbeeNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    /**
     * Zigbee node address
     * 0x00~0x40
     */
    @Column(name = "node_addr")
    byte nodeAddr;
    /**
     * User-defined zigbee node name or description string
     */
    @Column(name = "node_name")
    String nodeName;
    /**
     * Sensor Readouts
     */
    @OneToMany(targetEntity = CoilOrSensor.class, mappedBy = "node")
    List<CoilOrSensor> coilOrSensors;
    /**
     * whether zigbee not is online
     */
    @Column(name = "node_online")
    boolean online;
    @Transient
    boolean valid;
    @Column(name = "created")
    private Date created;

    /**
     * constructor for Spring JPA
     */
    public ZigbeeNode() {
    }

    /**
     * Constructor for collector
     *
     * @param deviceAddr
     * @param nodeName
     */
    public ZigbeeNode(byte deviceAddr, String nodeName) {
        this.nodeAddr = deviceAddr;
        this.nodeName = nodeName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CoilOrSensor> getCoilOrSensors() {
        return coilOrSensors;
    }

    public void setCoilOrSensors(List<CoilOrSensor> coilOrSensors) {
        this.coilOrSensors = coilOrSensors;
    }

    /**
     * Read Sensor readouts
     *
     * @param out output stream
     * @param in  input stream
     * @throws IOException
     */
    public void readSensorReadouts(OutputStream out, DataInputStream in) throws IOException {
        byte[] command = ModbusTCPPacket.NewCommandPacket(
                nodeAddr,
                FunctionCode.ReadNodeSensors.code,
                (new byte[]{0x00, 0x00, 0x00, 0x08})
        ).toByteArray();
        out.write(command);
        out.flush();
        ModbusTCPPacket response = ModbusTCPPacket.ReadResponsePacket(in);
        if (response.function != FunctionCode.ReadNodeSensors.code) {
            throw new IOException("Invalid response, function code " + response.function);
        }
        parseNodeSensors(response);
    }

    /**
     * response parser
     *
     * @param packet the resonse packet
     * @throws IOException
     */
    private void parseNodeSensors(ModbusTCPPacket packet) throws IOException {
        int byteCount = packet.data[0];
        if (byteCount / 8 != 4) {
            throw new IOException("invalid response byte count");
        }
        for (byte i = 0; i < 8; i++) {
            coilOrSensors.add(new CoilOrSensor(this, i, Arrays.copyOfRange(packet.data, i * 4 + 1, i * 4 + 5)));
        }
    }

    /**
     * Turn coil on and off
     *
     * @param out     output stream
     * @param in      input stream
     * @param channel a zigbee node has 32 or more channels
     * @param type    output type:A1~A8 B1~B8 ...
     * @param on      true for 1, false for 0
     * @throws IOException
     */
    public void writeCoil(OutputStream out, DataInputStream in, int channel, int type, boolean on) throws IOException {
        byte[] command;
        if (on) {
            command = ModbusTCPPacket.NewCommandPacket(nodeAddr, FunctionCode.WriteCoils.code,
                    (new byte[]{0x00, (byte) channel, 0x00, 0x02, 0x04, (byte) type, 0x40, (byte) 0xff, (byte) 0xff})).toByteArray();
        } else {
            command = ModbusTCPPacket.NewCommandPacket(nodeAddr, FunctionCode.WriteCoils.code,
                    (new byte[]{0x00, (byte) channel, 0x00, 0x02, 0x04, (byte) type, 0x40, (byte) 0x00, (byte) 0x00})).toByteArray();
        }
        out.write(command);
        out.flush();
        ModbusTCPPacket response = ModbusTCPPacket.ReadResponsePacket(in);
        if (response.function == FunctionCode.WriteCoils.code) {
            return;
        } else {
            throw new IOException("failed to write coil state");
        }

    }

    /**
     * write data to database
     *
     * @param entityManager ready configured entity manager
     * @throws IOException
     */
    void persist(EntityManager entityManager) throws IOException {
        Date persistDate = new Date();
        System.out.println("collecting "+Integer.toHexString(this.getNodeAddr()));
        this.created = persistDate;
        entityManager.getTransaction().begin();
        entityManager.persist(this);
        this.coilOrSensors.forEach(coilOrSensor -> {
            entityManager.persist(coilOrSensor);
        });
        entityManager.getTransaction().commit();
        System.out.println("done");
    }

    /**
     * Utility method for dumping sensor readouts
     */
    public void dumpSensors() {
        for (CoilOrSensor sensor : coilOrSensors) {
            System.out.println(nodeName + ":" + Integer.toHexString(nodeAddr) + "\t" + sensor.toString());
        }
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

}
