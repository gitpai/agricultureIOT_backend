package com.zhangfuwen.models;

import com.zhangfuwen.collector.FunctionCode;
import com.zhangfuwen.collector.ModbusTCPPacket;
import com.zhangfuwen.repositories.CoilOrSensorRepository;
import com.zhangfuwen.repositories.ThresholdInfoRepository;
import com.zhangfuwen.repositories.WarningRepository;
import com.zhangfuwen.repositories.ZigbeeNodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.zhangfuwen.utils.Utils;

import javax.persistence.*;
import java.io.*;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by dean on 3/13/17.
 */
@Entity
@Table(name = "t_gateway")
public class Gateway {
    public static final int MAX_NODES = 64;
    public static final int MAX_CHANNELS_PER_NODE = 32;
    private static final Logger logger = LoggerFactory.getLogger(Gateway.class);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "name")
    String name;
    @Column(name = "ip")
    String host;
    @Column(name = "port")
    int port;
    @Column(name = "max_nodes")
    int maxNodes;
    @Column(name = "max_channels")
    int maxChannelsPerNode;

    @Column(name="poll_interval")
    public int interval;
    @Transient
    public Timestamp lastCollected=null;

    @Column(name="X")
    public float X;
    @Column(name = "Y")
    public float Y;
    @Column(name="desc_string")
    public String desc;
    @Column(name="pic")
    public String pic;

    @OneToMany(targetEntity = ZigbeeNode.class,mappedBy = "gateway")
    List<ZigbeeNode> nodes;

    @Transient
    Map<Byte, String> zigbeeNodeNames;
    @Transient
    OutputStream out = null;
    @Transient
    DataInputStream in = null;
    @Transient
    Socket client = null;

    public Gateway() {
    }

    public Gateway(String name, String host, int port, Map<Byte, String> zigbeeNodeNames) throws IOException {
        this.name = name;
        this.host = host;
        this.port = port;
        this.maxNodes = MAX_NODES;
        this.maxChannelsPerNode = MAX_CHANNELS_PER_NODE;
        this.zigbeeNodeNames = zigbeeNodeNames;
    }

    public Gateway(String name,
                   String host,
                   int port,
                   int maxNodes,
                   int maxChannelsPerNode,
                   Map<Byte, String> zigbeeNodeNames) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.maxChannelsPerNode = maxChannelsPerNode;
        this.maxNodes = maxNodes;
        this.zigbeeNodeNames = zigbeeNodeNames;
    }

    /**
     * init database and com.zhangfuwen.collector connections
     *
     * @return
     */
    public void init() throws IOException {
        client = new Socket(host, port);
        out = new DataOutputStream(client.getOutputStream());
        in = new DataInputStream(client.getInputStream());
    }

    /**
     * destructor to close socket
     *
     * @throws IOException
     */
    protected void finalize() throws IOException {
        if (out != null) {
            out.close();
        }
        if (in != null) {
            in.close();
        }
        if (client != null) {
            client.close();
        }
    }

    public void collectAndPersist(boolean dummy,
                                  ZigbeeNodeRepository zigbeeNodeRepository,
                                  CoilOrSensorRepository coilOrSensorRepository,
                                  ThresholdInfoRepository thresholdInfoRepository,
                                  WarningRepository warningRepository
                                  ) throws IOException
    {
        byte[] online;
        if (dummy) {
            online = new byte[]{0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0x80};
        } else {
            online = readOnlineStatus();
        }
        //System.out.print(Utils.dataToHex(online));
        for (byte i = 0; i < this.maxNodes; i++) {
            byte deviceAddr = (byte) (i + 1);
            boolean isOnline = Utils.getBit(online, i);

            if (isOnline) {
                logger.info(String.format("found zigbee device 0x%02x online, collecting readouts...", deviceAddr));
                String deviceName = this.zigbeeNodeNames!=null?this.zigbeeNodeNames.get(new Byte(deviceAddr)):null;
                if (deviceName == null) {
                    deviceName = "TBD";
                }
                ZigbeeNode zigbeeNode = new ZigbeeNode(deviceAddr, deviceName);
                zigbeeNode.online = isOnline;
                if (dummy) {
                    this.readNodeDummy(zigbeeNode);
                } else {
                    this.readNode(zigbeeNode);
                }
                // fill up gateway id and node addrS
                zigbeeNode.gateway = this;
                for (CoilOrSensor sensor : zigbeeNode.coilOrSensors ) {
                    sensor.gatewayId = getId();
                    sensor.nodeAddr = zigbeeNode.getNodeAddr();
                }

                if (zigbeeNode.valid) {
                    logger.info(String.format("readout collected, " + zigbeeNode.toString()));
                    zigbeeNode.persist(zigbeeNodeRepository, coilOrSensorRepository, thresholdInfoRepository, warningRepository);
                } else {
                    logger.error("readout collection failure, " + zigbeeNode.toString());
                }
            }

        }

    }


    /**
     * read zigbee node devices. if a channel fails,
     * data won't be updated to database and an error log is printed.
     */
    public void readNode(ZigbeeNode zigbeeNode) {
        zigbeeNode.valid = false;
        try {
            zigbeeNode.coilOrSensors = new ArrayList<>();
            readSensorReadouts(zigbeeNode);
            zigbeeNode.valid = true;
        } catch (IOException e) {
            zigbeeNode.valid = false;
            logger.error("failed to read zigbee node:" + zigbeeNode.toString());
            e.printStackTrace();
        }
    }

    /**
     * Read Sensor readouts
     *
     * @throws IOException
     */
    public void readSensorReadouts(ZigbeeNode node) throws IOException {
        byte[] command = ModbusTCPPacket.NewCommandPacket(
                node.nodeAddr,
                FunctionCode.ReadNodeSensors.code,
                (new byte[]{0x00, 0x00, 0x00, (byte) (this.maxChannelsPerNode * 2)})
        ).toByteArray();
        out.write(command);
        out.flush();
        ModbusTCPPacket response = ModbusTCPPacket.ReadResponsePacket(in);
        if (response.function != FunctionCode.ReadNodeSensors.code) {
            throw new IOException("Invalid response, function code " + response.function);
        }
        node.parseNodeSensors(response);
    }


    private byte[] readOnlineStatus() throws IOException {
        // 0x5555: status register, 0x08:number of devices
        byte[] command = ModbusTCPPacket.NewCommandPacket((byte) 0xFF, FunctionCode.ReadOnlineStatus.code, (new byte[]{0x55, 0x55, 0, 8})).toByteArray();
        out.write(command);
        out.flush();
        ModbusTCPPacket response = ModbusTCPPacket.ReadResponsePacket(in);
        if (response.function == FunctionCode.ReadOnlineStatus.code) {
            int count = response.data[0]; // count should be 8
            if (count != this.maxNodes / 8) {
                new IOException("invalid reponse data length");
            }
            return Arrays.copyOfRange(response.data, 1, 1 + this.maxNodes / 8); // return 8 bytes
        } else if (response.function == FunctionCode.Error.code) {
            throw new IOException("Read online status error, code " + FunctionCode.Error.code);
        }
        throw new IOException("Invalid reponse function code" + response.function);
    }

    /**
     * a dummy method to emulate readNodes
     *
     * @throws IOException
     */
    public void readNodeDummy(ZigbeeNode zigbeeNode) {
        try {
            Random rand =new Random();
            int i;
            i=rand.nextInt(100);
            zigbeeNode.coilOrSensors = new ArrayList<>();
            zigbeeNode.coilOrSensors.add(new CoilOrSensor(zigbeeNode, (byte) 0, new byte[]{
                    (byte) 0xA1, (byte) 0x81, 0x00, (byte)i
            }));
            i=rand.nextInt(100);
            zigbeeNode.coilOrSensors.add(new CoilOrSensor(zigbeeNode, (byte) 1, new byte[]{
                    (byte) 0xA2, (byte) 0x81, 0x00, (byte)i
            }));
            i=rand.nextInt(100);
            zigbeeNode.coilOrSensors.add(new CoilOrSensor(zigbeeNode, (byte) 2, new byte[]{
                    (byte) 0xA1, (byte) 0x81, 0x00, (byte)i
            }));
            i=rand.nextInt(100);
            zigbeeNode.coilOrSensors.add(new CoilOrSensor(zigbeeNode, (byte) 3, new byte[]{
                    (byte) 0xA1, (byte) 0x81, 0x00, (byte)i
            }));
            i=rand.nextInt(100);
            zigbeeNode.coilOrSensors.add(new CoilOrSensor(zigbeeNode, (byte) 4, new byte[]{
                    (byte) 0xA1, (byte) 0x81, 0x00, (byte)i
            }));
            zigbeeNode.valid = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMaxNodes() {
        return maxNodes;
    }

    public void setMaxNodes(int maxNodes) {
        this.maxNodes = maxNodes;
    }

    public int getMaxChannelsPerNode() {
        return maxChannelsPerNode;
    }

    public void setMaxChannelsPerNode(int maxChannelsPerNode) {
        this.maxChannelsPerNode = maxChannelsPerNode;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
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

    @Override
    public String toString() {
        return "Gateway{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", maxNodes=" + maxNodes +
                ", maxChannelsPerNode=" + maxChannelsPerNode +
                ", interval=" + interval +
                ", X=" + X +
                ", Y=" + Y +
                ", desc='" + desc + '\'' +
                ", pic='" + pic + '\'' +
                ", nodes=" + nodes +
                ", zigbeeNodeNames=" + zigbeeNodeNames +
                '}';
    }
}
