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

/**
 * Created by dean on 3/13/17.
 */
public class Gateway implements Runnable {
    private static final byte ONLINE_STATUS_COUNT = 64;
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
                if(devMode) {
                    zigbeeNode.dumpSensors();
                }
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
        byte[] command = ModbusTCPPacket.NewCommandPacket((byte) 0xFF,
                FunctionCode.ReadOnlineStatus.code,
                (new byte[]{0x55, 0x55, 0, ONLINE_STATUS_COUNT})).toByteArray();
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
