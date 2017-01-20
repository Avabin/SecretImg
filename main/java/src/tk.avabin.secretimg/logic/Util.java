package tk.avabin.secretimg.logic;

import java.io.*;
import java.nio.file.Files;

/**
 * Created by Avabin on 07.11.2016.
 */
public class Util {
    public static void writeToFile(byte[] array, File dst) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(dst));
        outputStream.flush();
        for (byte b:
             array) {
            outputStream.writeByte(b);
        }
        outputStream.close();
    }
}
