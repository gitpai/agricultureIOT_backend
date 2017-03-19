package utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
}
