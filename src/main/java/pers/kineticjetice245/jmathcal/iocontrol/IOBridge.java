package pers.kineticjetice245.jmathcal.iocontrol;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import pers.kineticjetice245.jmathcal.gui.AlertBox;

public interface IOBridge {
    public void outSendMessage(String msg);
    public String askForInput(String msg);
    public HashMap<String, String> getPropertiesLoc();
    /**
     * Default launch info path
     */
    public final static String LAUNCH_INFO_PATH = "pers/kineticjetice245/jmathcal/config/launchInfo.xml";

    public final static IOBridge DFLT_BRIDGE = new IOBridge(){

        private Properties LAUNCH_INFO_PROP = getLaunchProperties();

        private HashMap<String, String> LAUNCH_INFO_MAP = propertiesToHashMap(LAUNCH_INFO_PROP);

        private Properties getLaunchProperties() {
            InputStream fis = null;
            Properties launchInfo = new Properties();
            try {
                ClassLoader classloader = Thread.currentThread().getContextClassLoader();
                fis = classloader.getResourceAsStream(IOBridge.LAUNCH_INFO_PATH);
                launchInfo.loadFromXML(fis);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                AlertBox.display("Error", "The calculator can not find the required files to launch!");
            } catch (IOException e) {
                e.printStackTrace();
                AlertBox.display("Error", "The calculator can not find the required files to launch!");
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return launchInfo;            
        }

        private HashMap<String, String> propertiesToHashMap(Properties properties) {
            HashMap<String, String> reVal = new HashMap<String, String>();
            Set<String> keySet = LAUNCH_INFO_PROP.stringPropertyNames();
            Iterator<String> keySetIterator = keySet.iterator();
            while(keySetIterator.hasNext()) {
                String currentKey = keySetIterator.next();
                reVal.put(currentKey, LAUNCH_INFO_PROP.get(currentKey).toString());
            }
            return reVal;
        }

        @Override
        public void outSendMessage(String msg) {
            System.out.println(msg);
        }

        @Override
        public String askForInput(String msg) {
            System.out.println(msg);
            InputStreamReader inputStream = new InputStreamReader(System.in) {
                @Override
                public void close() throws IOException {
                }
            };
            Scanner sc = new Scanner(inputStream);
            String input = sc.nextLine();
            sc.close();
            return input;
        }

        @Override
        public HashMap<String, String> getPropertiesLoc() {
            return LAUNCH_INFO_MAP;
        }
    };

    public static InputStream assignStream(String filePath) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        return classloader.getResourceAsStream(filePath);
    }
}
