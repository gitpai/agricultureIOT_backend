package com.zhangfuwen.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by dean on 3/13/17.
 */
public class Utils {
    public static String getString(Properties prop, String name) {
        return prop.getProperty(name);
    }
    public static int getInt(Properties prop, String name) {
        return Integer.parseUnsignedInt(prop.getProperty(name));
    }
    public static String getOnlyTagAttribute(Document doc, String tagName, String attr) {
        NodeList tagNodes = doc.getElementsByTagName(tagName);
        if(tagNodes == null || tagNodes.getLength() ==0) {
            return null;
        }
        Node attrNode = tagNodes.item(0).getAttributes().getNamedItem(attr);
        if(attrNode == null ) {
            return null;
        }
        return attrNode.getNodeValue();
    }

    public static boolean getBit(byte[] bytes, int bitNumber) throws IOException {
        int numberOfBytesRequired = bitNumber/8;
        if(bytes.length< numberOfBytesRequired) {
            throw new IOException("not enough bits");
        }
        //System.out.printf("%d %d %d\n", bitNumber, bytes.length,numberOfBytesRequired);
        return (bytes[numberOfBytesRequired] & (1<<bitNumber%8)) != 0;
    }

    public static String dataToHex(byte[] data){
        int index = 0;
        String ret ="data length:" + data.length+"\n";
        while(data.length - index > 0) {
            ret+= Integer.toHexString(data[index]);
            ret += " ";
            if(index%8==7){
                ret +="\n";
            }
            index++;

        }

        return ret;
    }
}
