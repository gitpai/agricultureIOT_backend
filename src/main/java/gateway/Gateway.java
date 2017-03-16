package gateway;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.persistence.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.Socket;
import java.util.*;

// 一个网关可连最多64个Zigbee节点
// 一个Zigbee节点最多接32个传感器或线圈
@Entity
@Table(name="T_ZIGBEE_NODE")
class ZigbeeNode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="NODE_ADDR")
    byte deviceAddr;

    @Column(name="NODE_NAME")
    String nodeName;

    @OneToMany(targetEntity = CoilOrSensor.class, mappedBy="node")
    List<CoilOrSensor> coilOrSensors;

    boolean online;
    boolean valid;

    public ZigbeeNode(byte deviceAddr, String nodeName) {
        this.coilOrSensors = new ArrayList<>();
        this.deviceAddr = deviceAddr;
        this.nodeName = nodeName;
    }


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }


    public byte getDeviceAddr() {
        return deviceAddr;
    }

    public void setDeviceAddr(byte deviceAddr) {
        this.deviceAddr = deviceAddr;
    }
    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public List<CoilOrSensor> getCoilOrSensors() {
        return coilOrSensors;
    }

    public void setCoilOrSensors(List<CoilOrSensor> coilOrSensors) {
        this.coilOrSensors = coilOrSensors;
    }

    public void dumpSensors() {
        for(CoilOrSensor sensor : coilOrSensors) {
            System.out.println(nodeName+":"+Integer.toHexString(deviceAddr)+"\t"+sensor.toString());
        }
    }

    /**
     * 读取某个节点个的所有传感器或线圈值,只读前8个，因为一般节点只有几个传感器
     */
    public void readNodeSensors(OutputStream out, DataInputStream in) throws IOException {
        byte[] command = ModbusTCPPacket.NewCommandPacket(deviceAddr, FunctionCode.ReadNodeSensors.code,
                (new byte[]{0x00, 0x00, 0x00, 0x08})).toByteArray();
        out.write(command);
        out.flush();
        ModbusTCPPacket response = ModbusTCPPacket.ReadResponsePacket(in);
        if (response.function != FunctionCode.ReadNodeSensors.code) {
            throw new IOException("Invalid response, function code " + response.function);
        }
        parseNodeSensors(response);
    }

    private void parseNodeSensors(ModbusTCPPacket packet) throws IOException {
        int byteCount = packet.data[0];
        if (byteCount / 8 != 4) {
            throw new IOException("invalid response byte count");
        }
        for (int i = 0; i < 8; i++) {
            coilOrSensors.add(new CoilOrSensor(this,Arrays.copyOfRange(packet.data, i * 4 + 1, i * 4 + 5)));
        }
    }

    /**
     * 写某个节点的某个线圈值
     * type : A1, A2....
     */
    public void writeCoil(OutputStream out, DataInputStream in, int channel, int type, boolean on) throws IOException {
        byte[] command;
        if (on) {
            command = ModbusTCPPacket.NewCommandPacket(deviceAddr, FunctionCode.WriteCoils.code,
                    (new byte[]{0x00, (byte) channel, 0x00, 0x02, 0x04, (byte) type, 0x40, (byte) 0xff, (byte) 0xff})).toByteArray();
        } else {
            command = ModbusTCPPacket.NewCommandPacket(deviceAddr, FunctionCode.WriteCoils.code,
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

    void persist() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("thePersistenceUnit");
        EntityManager entityManager = factory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(this);
        entityManager.getTransaction().commit();

    }
}

//开关量或传感器
@Entity
@Table(name="T_SENSOR")
class CoilOrSensor {
    @Id
    @GeneratedValue
    Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="NODE_ID")
    ZigbeeNode node;

    @Column(name="SENSOR_TYPE")
    byte sensorType;

    @Column(name="DATA_TYPE")
    byte dataType;

    @Column(name="VALUE")
    short value;

    public static Map<Integer,String> sensorTypeMap = new HashMap<Integer,String>();
    static {
        //TODO: table not complete
        sensorTypeMap.put(0x01,"温度");
        sensorTypeMap.put(0x02,"湿度");
        sensorTypeMap.put(0x03,"照度");
    }

    public CoilOrSensor(ZigbeeNode node, byte[] data) throws IOException {
        this.node = node;
        this.sensorType = data[0];
        this.dataType = data[1];
        this.value = 0;
        this.value += (short) ((short) (data[2]) << 8);
        this.value += (short) (data[3]);
    }
    public String toString() {
        return "type\t:"+sensorTypeMap.get(sensorType) +", dataType:" +Integer.toHexString(this.dataType) +
                ", value\t:" + Integer.toHexString(value);
    }
}

/**
 * Created by dean on 3/13/17.
 */
public class Gateway implements Runnable {
    OutputStream out = null;
    DataInputStream in = null;
    public Socket client = null;
    public List<ZigbeeNode> zigbeeNodes;
    boolean devMode;
    String host;
    int port;

    public Gateway(String filePath) throws IOException {
        this.parseConfig(filePath);
        //client = new Socket(host, port);
        //out = new DataOutputStream(client.getOutputStream());
        //in = new DataInputStream(client.getInputStream());
    }


    protected void finalize() throws IOException {
        out.close();
        client.close();

    }

    void parseConfig(String filePath) {
        try {

            File fXmlFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            devMode = doc.getDocumentElement().getAttribute("devMode").equals("true");
            host = doc.getDocumentElement().getAttribute("host");
            port = Integer.parseInt(doc.getDocumentElement().getAttribute("port"));
            parseNodes(doc);
            if (devMode) {
                for (ZigbeeNode zigbeeNode : zigbeeNodes) {
                    System.out.println(zigbeeNode.nodeName + "\t:" + zigbeeNode.deviceAddr);
                    System.out.println("host\t:" + host);
                    System.out.println("port\t:" + port);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseNodes(Document doc) {
        NodeList nList = doc.getElementsByTagName("node");
        zigbeeNodes = new ArrayList<>();
        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i);
            String nodeName = node.getAttributes().getNamedItem("nodeName").getNodeValue();
            String nodeAddr = node.getAttributes().getNamedItem("deviceAddr").getNodeValue();
            zigbeeNodes.add(new ZigbeeNode((byte) Integer.decode(nodeAddr).byteValue(), nodeName));
        }
    }


    @Override
    public void run() {
        try {
            System.out.println("running");
            this.readNodesDummy();
            this.zigbeeNodes.forEach(zigbeeNode -> {
                zigbeeNode.persist();
            });
/*
            byte[] online = readOnlineStatus();
            if(devMode) {
                System.out.println("onlineStatus:" + Integer.toBinaryString(online[0]) +
                        Integer.toBinaryString(online[1]) +
                        Integer.toBinaryString(online[2]) +
                        Integer.toBinaryString(online[3]) +
                        Integer.toBinaryString(online[4]) +
                        Integer.toBinaryString(online[5]) +
                        Integer.toBinaryString(online[6]) +
                        Integer.toBinaryString(online[7]));
            }
            for (ZigbeeNode zigbeeNode : zigbeeNodes) {
                zigbeeNode.readNodeSensors(out,in);
                if(devMode) {
                    zigbeeNode.dumpSensors();
                }
            }
            */
        } catch (IOException e) {
            System.out.println(" gateway failed, reason: " + e.getMessage());
        }
    }

    public void readNodesDummy() throws IOException {
        zigbeeNodes.forEach(zigbeeNode -> {
            try {
                zigbeeNode.coilOrSensors.add(new CoilOrSensor(zigbeeNode, new byte[]{
                        (byte) 0xA1, (byte) 0x81, 0x00, 0x44
                }));

                zigbeeNode.coilOrSensors.add(new CoilOrSensor(zigbeeNode, new byte[]{
                        (byte) 0xA1, (byte) 0x81, 0x00, 0x34
                }));
                zigbeeNode.coilOrSensors.add(new CoilOrSensor(zigbeeNode, new byte[]{
                        (byte) 0xA1, (byte) 0x81, 0x00, 0x24
                }));
                zigbeeNode.coilOrSensors.add(new CoilOrSensor(zigbeeNode, new byte[]{
                        (byte) 0xA1, (byte) 0x81, 0x00, 0x14
                }));
                zigbeeNode.coilOrSensors.add(new CoilOrSensor(zigbeeNode, new byte[]{
                        (byte) 0xA1, (byte) 0x81, 0x00, 0x04
                }));
                if(devMode) {
                    System.out.println(zigbeeNode);
                }

            }catch (IOException e) {

            }
        });
    }
    public void readNodes() throws IOException {
        zigbeeNodes.forEach( zigbeeNode -> {
            zigbeeNode.valid = false;
            try {
                zigbeeNode.readNodeSensors(out, in);
                zigbeeNode.valid = true;
            }catch (IOException e) {
                zigbeeNode.valid = false;
            }
        });
    }


    private byte[] readOnlineStatus() throws IOException {
        // 0x5555: status register, 0x08:number of sensors
        byte[] command = ModbusTCPPacket.NewCommandPacket((byte) 0xFF, FunctionCode.ReadOnlineStatus.code, (new byte[]{0x55, 0x55, 0, 8})).toByteArray();
        out.write(command);
        out.flush();
        ModbusTCPPacket response = ModbusTCPPacket.ReadResponsePacket(in);
        if (response.function == FunctionCode.ReadOnlineStatus.code) {
            int count = response.data[0]; // count should be 8
            if (count != 8) {
                new IOException("invalid reponse data length");
            }
            return Arrays.copyOfRange(response.data, 1, 10); // return 8 bytes
        } else if (response.function == FunctionCode.Error.code) {
            throw new IOException("Read online status error, code " + FunctionCode.Error.code);
        }
        throw new IOException("Invalid reponse function code" + response.function);
    }

}
