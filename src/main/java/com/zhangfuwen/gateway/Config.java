package com.zhangfuwen.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.zhangfuwen.utils.Utils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dean on 3/18/17.
 */
public class Config {
    Logger logger = LoggerFactory.getLogger(Config.class);
    private boolean devMode;
    private int interval;
    private Gateway gateway;

    public int getInterval(){
        return interval;
    }

    public boolean isDevMode() {
        return devMode;
    }

    public Gateway getGateway() {
        return gateway;
    }


    Document doc = null;
    private static Config config = null;

    private Config(){
        loadXml();
        parseAppConfig();
        parseGatewayConfig();
    }
    public static Config getInstance(){
        if(config == null) {
            config = new Config();
        }
        return config;
    }

    /**
     * open XML file
     */
    public void loadXml(){
        File fXmlFile = new File("config.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try{
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    void parseAppConfig() {
        devMode = Utils.getOnlyTagAttribute(doc, "com/zhangfuwen/app","devMode").equals("true");
        String intervalString = Utils.getOnlyTagAttribute(doc, "com/zhangfuwen/app","interval");
        interval = Integer.parseInt(intervalString);
    }

    /**
     * parse com.zhangfuwen.gateway and zigbee devices related configuration options
     */
    void parseGatewayConfig() {
        try {
            String name = Utils.getOnlyTagAttribute(doc, "com/zhangfuwen/gateway","name");
            String host = Utils.getOnlyTagAttribute(doc, "com/zhangfuwen/gateway","host");
            String portString = Utils.getOnlyTagAttribute(doc, "com/zhangfuwen/gateway","port");
            String maxNodesString = Utils.getOnlyTagAttribute(doc,"spec","max_nodes");
            String maxChannelsPerNodeString = Utils.getOnlyTagAttribute(doc,"spec","max_channels_per_node");
            int port = Integer.parseInt(portString);
            int maxNodes;
            try {
                 maxNodes = Integer.parseInt(maxNodesString);
            }catch (NumberFormatException nfe) {
                maxNodes = Gateway.MAX_NODES;
            }
            int maxChannelsPerNode;
            try {
                maxChannelsPerNode = Integer.parseInt(maxChannelsPerNodeString);
            }catch (NumberFormatException nfe) {
                maxChannelsPerNode = Gateway.MAX_CHANNELS_PER_NODE;
            }
            Map<Byte,String> zigbeeNodes = parseZigbeeNodes();
            gateway = new Gateway(name,host,port,maxNodes, maxChannelsPerNode,zigbeeNodes);
            logger.debug(gateway.toString());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    /**
     * parse zigbee nodes
     */
    private Map<Byte,String> parseZigbeeNodes() {
        Map<Byte,String> zigbeeNodeNames = new HashMap<>();
        NodeList nList = doc.getElementsByTagName("node");
        for (int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);
            String nodeName = node.getAttributes().getNamedItem("nodeName").getNodeValue();
            String nodeAddr = node.getAttributes().getNamedItem("nodeAddr").getNodeValue();
            zigbeeNodeNames.put(new Byte((byte)Integer.parseUnsignedInt(nodeAddr)), nodeName);
        }
        return zigbeeNodeNames;
    }


}
