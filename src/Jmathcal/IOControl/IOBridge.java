package Jmathcal.IOControl;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Scanner;



public interface IOBridge {
    public void outSendMessage(String msg);
    public String askForInput(String msg);
    public HashMap<String, File> getPropertiesLoc();
    
    public final static File configPath = new File("config/calculator/flattenExpr.xml");
    public final static File greekLetPath = new File("config/calculator/greekAlphabet.xml");

    public final static IOBridge DFLT_BRIDGE = new IOBridge(){
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
            HashMap<String, File> reVal = new HashMap<String, File>();
            reVal.put("configPath", configPath);
            reVal.put("greekLetPath", greekLetPath);
            return reVal;
        }
    };
}
