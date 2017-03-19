package gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utils.Utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * Created by dean on 3/13/17.
 */
public class Gateway {
    public static final int MAX_NODES = 64;
    public static final int MAX_CHANNELS_PER_NODE = 32;
    private static final Logger logger = LoggerFactory.getLogger(Gateway.class);
    String name;
    String host;
    int port;
    int maxNodes;
    int maxChannelsPerNode;

    Map<Byte, String> zigbeeNodeNames;

    OutputStream out = null;
    DataInputStream in = null;
    Socket client = null;

    public Gateway(String name, String host, int port, Map<Byte,String> zigbeeNodeNames) throws IOException {
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
                   Map<Byte,String> zigbeeNodeNames) throws IOException {
        this.name = name;
        this.host = host;
        this.port = port;
        this.maxChannelsPerNode = maxChannelsPerNode;
        this.maxNodes = maxNodes;
        this.zigbeeNodeNames = zigbeeNodeNames;
    }

    /**
     * init database and gateway connections
     *
     * @return
     */
    public void init() {
        //client = new Socket(host, port);
        //out = new DataOutputStream(client.getOutputStream());
        //in = new DataInputStream(client.getInputStream());
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

    public void collectAndPersist(boolean dummy, EntityManager entityManager) throws IOException {
        byte[] online;
        if(dummy) {
            online = new byte[]{0x01,0x00,0x00,0x00,0x00,0x00,0x00,(byte)0x80};
        }else {
            online = readOnlineStatus();
        }

        for(byte i = 0;i< this.maxNodes;i++) {
            byte deviceAddr = (byte)(i+1);
            boolean isOnline = Utils.getBit(online,i);
            if(isOnline) {
                logger.info(String.format("found zigbee device 0x%02x online, collecting readouts...",deviceAddr));
                String deviceName = (String)this.zigbeeNodeNames.get(new Byte(deviceAddr));
                if(deviceName==null) {
                    deviceName="TBD";
                }
                ZigbeeNode zigbeeNode = new ZigbeeNode(deviceAddr, deviceName);
                zigbeeNode.online = isOnline;
                if (dummy) {
                    this.readNodeDummy(zigbeeNode);
                } else {
                    this.readNode(zigbeeNode);
                }

                if (zigbeeNode.valid) {
                    logger.info(String.format("readout collected, " + zigbeeNode.toString()));
                    zigbeeNode.persist(entityManager);
                } else {
                    logger.error("readout collection failure, " + zigbeeNode.toString());
                }
            }

        }

    }


    /**
     * read zigbee node sensors. if a channel fails,
     * data won't be updated to database and an error log is printed.
     */
    public void readNode(ZigbeeNode zigbeeNode) {
        zigbeeNode.valid = false;
        try {
            zigbeeNode.coilOrSensors= new ArrayList<>();
            zigbeeNode.readSensorReadouts(out, in);
            zigbeeNode.valid = true;
        } catch (IOException e) {
            zigbeeNode.valid = false;
            logger.error("failed to read zigbee node:" + zigbeeNode.toString());
            e.printStackTrace();
        }
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

    /**
     * a dummy method to emulate readNodes
     *
     * @throws IOException
     */
    public void readNodeDummy(ZigbeeNode zigbeeNode) {
        try {
            zigbeeNode.coilOrSensors= new ArrayList<>();
            zigbeeNode.coilOrSensors.add(new CoilOrSensor(zigbeeNode, (byte) 0, new byte[]{
                    (byte) 0xA1, (byte) 0x81, 0x00, 0x44
            }));

            zigbeeNode.coilOrSensors.add(new CoilOrSensor(zigbeeNode, (byte) 1, new byte[]{
                    (byte) 0xA2, (byte) 0x81, 0x00, 0x34
            }));
            zigbeeNode.coilOrSensors.add(new CoilOrSensor(zigbeeNode, (byte) 2, new byte[]{
                    (byte) 0xA1, (byte) 0x81, 0x00, 0x24
            }));
            zigbeeNode.coilOrSensors.add(new CoilOrSensor(zigbeeNode, (byte) 3, new byte[]{
                    (byte) 0xA1, (byte) 0x81, 0x00, 0x14
            }));
            zigbeeNode.coilOrSensors.add(new CoilOrSensor(zigbeeNode, (byte) 4, new byte[]{
                    (byte) 0xA1, (byte) 0x81, 0x00, 0x04
            }));
            zigbeeNode.valid = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Gateway{" +
                "name='" + name + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", maxNodes=" + maxNodes +
                ", maxChannelsPerNode=" + maxChannelsPerNode +
                ", zigbeeNodeNames=" + zigbeeNodeNames +
                '}';
    }
}
