package Jmathcal.Number;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


public class Test {
    public static void main(String[] args) {
        Properties keyWords = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("config/calculator/flattenExpr.xml");
            keyWords.loadFromXML(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println(keyWords.getProperty("sin"));
    }
}
