package tk.avabin.secretimg.logic;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by Avabin on 27.10.2016.
 */
public class ImageProcessor {
    private Cipher cipher;

    public ImageProcessor(){
        try {
            cipher = Cipher.getInstance("DES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public void encrypt(String pathToSourceImage, String pass, String pathToDestImage) throws FileNotFoundException {
        File src = new File(pathToSourceImage);
        File dst = new File(pathToDestImage);
        if (!src.exists()) {
            System.out.println("Source file doesn't exists!");
            throw new FileNotFoundException();
        }
        try {
            System.out.println("Encrypting file \n" + src.getAbsolutePath());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(new SecretKeySpec(pass.getBytes("UTF-8"), 0, pass.length(),"DES"));

            byte[] inputBuffer = Files.readAllBytes(src.toPath());

            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(inputBuffer);

            Util.writeToFile(encrypted, dst);
            System.out.println("File saved as: \n" + dst.getAbsolutePath());

        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }


    }

    public void decrypt(String pathToSourceImage, String pass, String pathToDestImage) {
        File src = new File(pathToSourceImage);
        File dst = new File(pathToDestImage);
        try {
            System.out.println("Decrypting file: \n" + src.getAbsolutePath());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(new SecretKeySpec(pass.getBytes("UTF-8"), 0, pass.length(),"DES"));

            byte[] encrypted = Files.readAllBytes(src.toPath());

            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decrypted = cipher.doFinal(encrypted);

            Util.writeToFile(decrypted, dst);
            System.out.println("File saved as: \n" + dst.getAbsolutePath());

        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }


    }
}
