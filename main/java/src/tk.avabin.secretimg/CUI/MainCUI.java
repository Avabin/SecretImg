package tk.avabin.secretimg.CUI;

import tk.avabin.secretimg.logic.ImageProcessor;

import java.io.FileNotFoundException;

/**
 * Created by Avabin on 07.11.2016.
 */
public class MainCUI {
    public static final int ENCRYPT = 1;
    private ImageProcessor imageProcessor;

    public MainCUI(String pathToSrcFile, String pathtoDstFile, String pass, int mode) {
        imageProcessor = new ImageProcessor();
        if(mode == ENCRYPT) {
            try {
                imageProcessor.encrypt(pathToSrcFile, pass, pathtoDstFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            imageProcessor.decrypt(pathToSrcFile, pass, pathtoDstFile);
        }

    }
}
