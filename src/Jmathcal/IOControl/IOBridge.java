package Jmathcal.IOControl;

import java.io.File;

public interface IOBridge {

    public void outSendMessage(String msg);
    public String askForInput(String msg);
    public File getPropertiesLoc();

}
