package utils;

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
}
