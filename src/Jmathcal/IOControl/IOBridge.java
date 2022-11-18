package Jmathcal.IOControl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import Jmathcal.GUI.AlertBox;

public interface IOBridge {
    public void outSendMessage(String msg);
    public String askForInput(String msg);
    public HashMap<String, File> getPropertiesLoc();
    /**
     * Default launch info path
     */
    public final static File LAUNCH_INFO_PATH = new File("config/calculator/launchInfo.xml");

    public final static IOBridge DFLT_BRIDGE = new IOBridge(){

        private static Properties LAUNCH_INFO_PROP = getLaunchProperties();

        private static HashMap<String, File> LAUNCH_INFO_MAP = propertiesToHashMap(LAUNCH_INFO_PROP);

        private static Properties getLaunchProperties() {
            FileInputStream fis = null;
            Properties launchInfo = new Properties();
            try {
                fis = new FileInputStream(IOBridge.LAUNCH_INFO_PATH);
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

        private static HashMap<String, File> propertiesToHashMap(Properties properties) {
            HashMap<String, File> reVal = new HashMap<String, File>();
            Set<String> keySet = LAUNCH_INFO_PROP.stringPropertyNames();
            Iterator<String> keySetIterator = keySet.iterator();
            while(keySetIterator.hasNext()) {
                String currentKey = keySetIterator.next();
                reVal.put(currentKey, new File(LAUNCH_INFO_PROP.get(currentKey).toString()));
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
        public HashMap<String, File> getPropertiesLoc() {
            return LAUNCH_INFO_MAP;
        }
    };
}
