package tk.avabin.secretimg;

import javafx.application.Application;
import tk.avabin.secretimg.CUI.MainCUI;
import tk.avabin.secretimg.GUI.MainGUI;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by Avabin on 07.11.2016.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length >= 1 && Objects.equals(args[0], "nogui")) {
            if(args.length == 5 && (Objects.equals(args[4], "1") || Objects.equals(args[4], "0"))) {
                new MainCUI(args[1], args[2], args[3], Integer.parseInt(args[4]));
            } else {
                System.out.println("Invalid syntax! Use: \njava -jar  secretimg.jar nogui SOURCE_FILE_PATH DESTINATION_FILE_PATH PASSPHRASE MODE\n" +
                        "SOURCE_FILE_PATH      - path to your source file.\n" +
                        "DESTINATION_FILE_PATH - path to your destination (processed) file. Doesn't need to exist at this moment.\n" +
                        "PASSPHRASE            - password to encrypt/decrypt file. Must be the same as used to encrypt file.\n" +
                        "                        Must be more than 8 characters.\n" +
                        "MODE                  - 1 for encrypting file, 0 for decrypting file.");
            }
        }
        else {
            System.out.println("Launching with GUI. \nFor silent (No GUI) mode use \'nogui\' as first parameter.");
            Application.launch(MainGUI.class);
        }
    }
}
