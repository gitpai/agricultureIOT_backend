package gateway;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by dean on 3/13/17.
 */
//public class Settings {
//        public int PollPeriod;
//        public void Settings(String filename) {
//
//            Properties prop = new Properties();
//            InputStream input = null;
//
//            try {
//
//                input = new FileInputStream(filename);
//
//                // load a properties file
//                prop.load(input);
//
//                // get the properties value
//                Utils.getInt(prop, "PollPeriod");
//
//            } catch (IOException io) {
//                io.printStackTrace();
//            } finally {
//                if (input != null) {
//                    try {
//                        input.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//        }
//
//}
