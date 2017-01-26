package tk.avabin.secretimg.GUI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tk.avabin.secretimg.logic.ImageProcessor;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;

public class MainGUI extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception {

        ImageProcessor imageProcessor = new ImageProcessor();
        final String[] pass = new String[1];
        final boolean[] passphraseValid = {false};
        final ArrayList<File>[] files = new ArrayList[]{new ArrayList<>()};
        // Files
        File propertyFile = SecretImgUtil.createSettingsFile(null);
        Properties properties = SecretImgUtil.loadPropertyFile(propertyFile);

        // Locales
        Locale polishLoc = new Locale("pl", "PL"),
               englishLoc = new Locale("en", "EN");

        // Panes
        GridPane localeGridPane = SecretImgUtil.defGridPane(),
                 grid = SecretImgUtil.defGridPane();
        BorderPane mainPane = new BorderPane();

        // Buttons
        Button polishLocale = SecretImgUtil.buttonForKey("langChoose.polish"),
               englishLocale = SecretImgUtil.buttonForKey("langChoose.english"),
               applyButton = SecretImgUtil.buttonForKey("langChoose.apply"),
               enterPassButton = SecretImgUtil.buttonForKey("passphrase.submit"),
               chooseDirButton = SecretImgUtil.buttonForKey("main.chooseDir"),
               chooseFileButton = SecretImgUtil.buttonForKey("main.chooseFile"),
               encryptButton = SecretImgUtil.buttonForKey("main.encrypt"),
                decryptButton = SecretImgUtil.buttonForKey("main.decrypt"),
                resetButton = SecretImgUtil.buttonForKey("main.reset");


        // Text fields and labels
        Label passphraseLabel = SecretImgUtil.labelForKey("passphrase.label");
              TextArea fileTextArea = new TextArea();
        PasswordField passphraseTextField = new PasswordField();

        // Boxes
        HBox localeBox = new HBox(10),
                mainButtonsBox = new HBox(10);

        //Scenes
        Scene languageChoosing = new Scene(localeGridPane, 400, 200),
              getPassphraseScene = new Scene(grid, 400, 200),
                mainScene = new Scene(mainPane, 600, 420);

        primaryStage.setResizable(false);

        // *************************
        // *Language choosing Scene*
        // *************************;

        polishLocale.setMinWidth(100);
        polishLocale.setOnAction(event -> SecretImgUtil.setLocaleAndUpdate(polishLoc, primaryStage));
        englishLocale.setMinWidth(100);
        englishLocale.setOnAction(event -> SecretImgUtil.setLocaleAndUpdate(englishLoc, primaryStage));
        applyButton.setMinWidth(65);
        applyButton.setOnAction(event -> {
            SecretImgUtil.changeStageScene(primaryStage, getPassphraseScene, SecretImgUtil.get("passphrase.title"));
            SecretImgUtil.writeProperty(propertyFile, "lang", SecretImgUtil.get("language"));
        });

        localeBox.setAlignment(Pos.BOTTOM_CENTER);
        localeBox.getChildren().add(polishLocale);
        localeBox.getChildren().add(englishLocale);
        localeBox.getChildren().add(applyButton);

        localeGridPane.add(localeBox, 1, 1);

        primaryStage.setTitle(SecretImgUtil.get("langChoose.title"));

        // ***************************
        // *Entering passphrase Scene*
        // ***************************


        passphraseTextField.focusedProperty().addListener((arg, oldValue, newValue) -> {
            if (!newValue) {
                if (!(passphraseTextField.getText().length() < 8)) {
                    if (!SecretImgUtil.validatePassphrase(passphraseTextField.getText())) {
                        SecretImgUtil.showPopupMessage(SecretImgUtil.get("passphrase.invalid"), primaryStage);
                    } else {
                        passphraseValid[0] = true;
                        pass[0] = passphraseTextField.getText();
                    }
                } else {
                    SecretImgUtil.showPopupMessage(SecretImgUtil.get("passphrase.tooShort"), primaryStage);
                }
            }
        });
        passphraseTextField.alignmentProperty().setValue(Pos.BASELINE_LEFT);
        passphraseTextField.setMaxWidth(150);
        passphraseLabel.setAlignment(Pos.CENTER_LEFT);
        enterPassButton.setMinWidth(150);
        enterPassButton.setAlignment(Pos.CENTER_LEFT);
        enterPassButton.alignmentProperty().setValue(Pos.BASELINE_CENTER);
        enterPassButton.setOnAction(event -> {
            if (passphraseValid[0]) SecretImgUtil.changeStageScene(primaryStage, mainScene, SecretImgUtil.get("main.title"));
        });

        grid.add(passphraseLabel, 0, 0);
        grid.add(passphraseTextField, 1, 0);
        grid.add(enterPassButton, 1, 1);

        //************************
        //*Main application scene*
        //************************

        fileTextArea.setEditable(false);

        chooseDirButton.setOnAction(event -> {
            chooseDirButton.setDisable(true);
            chooseFileButton.setDisable(true);
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle(SecretImgUtil.get("main.chooseDir"));
            File dir = directoryChooser.showDialog(primaryStage.getOwner());
            files[0].addAll(SecretImgUtil.getFilesFromDir(dir));
            for (File file :
                    files[0]) {
                fileTextArea.appendText(file.getAbsolutePath() + "\n");
            }
            encryptButton.setDisable(false);
            decryptButton.setDisable(false);
        });

        encryptButton.setOnAction(event -> {
            long progress[] = {0L};
            ProcessWorker worker = new ProcessWorker(files[0], true, progress);
            worker.setPass(pass[0]);
            encryptButton.setDisable(true);
            decryptButton.setDisable(true);
            worker.start();
            System.out.println("Worker started");
            fileTextArea.clear();
            Thread runnable = new Thread(() -> {
                int index = 0;
                while (true) {
                    if (System.currentTimeMillis() % 750 == 0) {
                        fileTextArea.setText("Encrypting:    " + String.valueOf(progress[0]) + "/" + files[0].size() + "" +
                                "   " + files[0].get(index).getName());
                        index = (int) progress[0];
                    }
                    if (!worker.isAlive()) {
                        fileTextArea.setText("Encrypting: " + String.valueOf(progress[0]) + "/" + (files[0].size()) + "" +
                                "   " + files[0].get(index).getName());

                        encryptButton.setDisable(false);
                        decryptButton.setDisable(false);
                        files[0] = new ArrayList<>();
                        fileTextArea.setText(SecretImgUtil.get("main.done"));
                        break;
                    }
                }
            });
            runnable.start();
        });

        decryptButton.setOnAction(event -> {
            long progress[] = {0L};
            ProcessWorker worker = new ProcessWorker(files[0], false, progress);
            encryptButton.setDisable(true);
            decryptButton.setDisable(true);
            worker.setPass(pass[0]);
            fileTextArea.clear();
            Thread runnable = new Thread(() -> {
                int index = 0;
                while (true) {
                    if (System.currentTimeMillis() % 750 == 0) {
                        fileTextArea.setText("Decrypting:    " + String.valueOf(progress[0]) + "/" + files[0].size() + "" +
                                "   " + files[0].get(index).getName());
                        index = (int) progress[0];
                    }
                    if (!worker.isAlive()) {
                        fileTextArea.setText("Decrypting: " + String.valueOf(progress[0]) + "/" + (files[0].size()) + "" +
                                "   " + files[0].get(index).getName());

                        encryptButton.setDisable(false);
                        decryptButton.setDisable(false);
                        files[0] = new ArrayList<>();
                        fileTextArea.setText("DONE");
                        break;
                    }
                }
            });
            runnable.start();
            worker.start();
        });

        chooseFileButton.setOnAction(event -> {
            chooseDirButton.setDisable(true);
            chooseFileButton.setDisable(true);
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(SecretImgUtil.get("main.chooseFile"));
            files[0].addAll(fileChooser.showOpenMultipleDialog(primaryStage.getOwner()));
            for (File file :
                    files[0]) {
                fileTextArea.appendText(file.getAbsolutePath() + "\n");
            }
            encryptButton.setDisable(false);
            decryptButton.setDisable(false);
            chooseDirButton.setDisable(false);
            chooseFileButton.setDisable(false);
        });

        resetButton.setOnAction(event -> {
            encryptButton.setDisable(false);
            decryptButton.setDisable(false);
            files[0] = new ArrayList<>();
            fileTextArea.clear();
        });

        mainButtonsBox.setAlignment(Pos.CENTER);
        mainButtonsBox.getChildren().addAll(chooseDirButton, chooseFileButton, encryptButton, decryptButton, resetButton);
        mainPane.setPadding(new Insets(10, 10, 10, 10));
        mainPane.centerProperty().setValue(fileTextArea);
        mainPane.bottomProperty().setValue(mainButtonsBox);

        String lang = null;
        if (properties != null) {
            lang = properties.getProperty("lang");
        }
        if(lang == null) {
            primaryStage.setScene(languageChoosing);
        } else {
            String langcode = lang.substring(0, 2);
            String countrycode = lang.substring(3, 5);
            SecretImgUtil.setLocale(new Locale(langcode, countrycode));
            SecretImgUtil.changeStageScene(primaryStage, getPassphraseScene, SecretImgUtil.get("passphrase.title"));
        }
        primaryStage.show();

    }
}
