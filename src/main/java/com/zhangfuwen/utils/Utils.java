package com.zhangfuwen.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by dean on 3/13/17.
 */
public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static String getString(Properties prop, String name) {
        return prop.getProperty(name);
    }

    public static int getInt(Properties prop, String name) {
        return Integer.parseUnsignedInt(prop.getProperty(name));
    }

    public static String getOnlyTagAttribute(Document doc, String tagName, String attr) {
        NodeList tagNodes = doc.getElementsByTagName(tagName);
        if (tagNodes == null || tagNodes.getLength() == 0) {
            return null;
        }
        Node attrNode = tagNodes.item(0).getAttributes().getNamedItem(attr);
        if (attrNode == null) {
            return null;
        }
        return attrNode.getNodeValue();
    }

    public static boolean getBit(byte[] bytes, int bitNumber) throws IOException {
        int numberOfBytesRequired = bitNumber / 8;
        if (bytes.length < numberOfBytesRequired) {
            throw new IOException("not enough bits");
        }
        //System.out.printf("%d %d %d\n", bitNumber, bytes.length,numberOfBytesRequired);
        return (bytes[numberOfBytesRequired] & (1 << bitNumber % 8)) != 0;
    }

    public static String dataToHex(byte[] data) {
        int index = 0;
        String ret = "data length:" + data.length + "\n";
        while (data.length - index > 0) {
            ret += Integer.toHexString(data[index]);
            ret += " ";
            if (index % 8 == 7) {
                ret += "\n";
            }
            index++;

        }

        return ret;
    }

    public static Map<Byte, String> getSensorTypeMap() {
        Map<Byte, String> map = new HashMap<Byte, String>();
        String csvFile = "./sensorType.csv";
        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] tokens = line.split(cvsSplitBy);

                try {
                    map.put(Integer.decode(tokens[0]).byteValue(), tokens[1]);
                } catch (NumberFormatException e) {
                    logger.error("error while parsing sensorType.csv, line:" + line + ", message:" + e.getMessage());
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static String toRealValue(byte dataType, short value) {
        int realvalue;
        if (dataType > 0) {// something that maybe larger than 65536/2
            realvalue = (int) value;
        } else {
            realvalue = value;
        }
        int points;
        if(dataType>0) {
            points = dataType;
        }else{
            points = dataType&(byte)0x7F;
        }
        System.out.println((double)points);
        System.out.println((double)realvalue);
        double doubleValue = ((double)realvalue) / (int)Math.pow((double)10, (double)points);
        NumberFormat ddf1= NumberFormat.getNumberInstance() ;

        ddf1.setMaximumFractionDigits(points);
        String s= ddf1.format(doubleValue) ;
        return formatDecimalWithZero(doubleValue,points);

    }

    /**
     * 按四舍五入保留指定小数位数，位数不够用0补充
     * @param o 格式化前的小数
     * @param newScale 保留小数位数
     * @return 格式化后的小数
     */
    public static String formatDecimalWithZero(Object o, int newScale) {
        return String.format("%." + newScale + "f", o);
    }
}
