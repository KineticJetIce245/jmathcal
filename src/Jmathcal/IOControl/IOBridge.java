package Jmathcal.IOControl;

import java.io.File;
import java.util.HashMap;

public interface IOBridge {

    public void outSendMessage(String msg);
    public String askForInput(String msg);
    public HashMap<String, File> getPropertiesLoc();

}
