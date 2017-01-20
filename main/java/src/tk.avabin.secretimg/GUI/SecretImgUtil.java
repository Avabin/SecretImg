package tk.avabin.secretimg.GUI;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Created by Avabin on 09.11.2016.
 */
public final class SecretImgUtil {

    // Locale utilities
    private static final ObjectProperty<Locale> LOCALE;
    static {
        LOCALE = new SimpleObjectProperty<>(getDefaultLocale());
        LOCALE.addListener((observable, oldValue, newValue) -> Locale.setDefault(newValue));
    }
    public static List<Locale> getSupportedLocales() {
        return new ArrayList<>(Arrays.asList(Locale.ENGLISH, Locale.forLanguageTag("pl")));
    }

    public static Locale getDefaultLocale() {
        Locale sysDefault = Locale.getDefault();
        return getSupportedLocales().contains(sysDefault) ? sysDefault : Locale.ENGLISH;
    }
    public static Locale getLocale() {
        return LOCALE.get();
    }
    public static void setLocale(Locale locale) {
        System.out.println("Setting locale: "+ locale.toString());
        localeProperty().set(locale);
        Locale.setDefault(locale);
    }
    public static ObjectProperty<Locale> localeProperty() {
        return LOCALE;
    }
    public static String get(final String key, final Object... args) {
        ResourceBundle bundle = ResourceBundle.getBundle("bundles/LangBundle", getLocale());
        return MessageFormat.format(bundle.getString(key), args);
    }
    public static StringBinding createStringBinding(final String key, Object... args) {
        return Bindings.createStringBinding(() -> get(key, args), LOCALE);
    }
    public static StringBinding createStringBinding(Callable<String> func) {
        return Bindings.createStringBinding(func, LOCALE);
    }

    // JavaFX element which will change language dynamically
    public static Label labelForValue(Callable<String> func) {
        Label label = new Label();
        label.textProperty().bind(createStringBinding(func));
        return label;
    }
    public static Button buttonForKey(final String key, final Object... args) {
        Button button = new Button();
        button.textProperty().bind(createStringBinding(key, args));
        return button;
    }
    public static Label labelForKey(final String key, final Object... args) {
        Label label = new Label();
        label.textProperty().bind(createStringBinding(key, args));
        return label;
    }
    // File utils
    public static File createSettingsFile(String optionalPath) {
        String filename = "settings.properties";
        File settings;
        if(optionalPath != null) settings = new File(optionalPath + System.lineSeparator() + filename);
        else settings = new File(filename);
        if (!settings.exists()) try {
            settings.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return settings;
    }
    public static Properties loadPropertyFile(File propertyFile) {
            try {
                FileInputStream in = new FileInputStream(propertyFile);
                Properties prop =  new Properties();
                prop.load(in);
                in.close();
                return prop;
            } catch (FileNotFoundException e) {
                return null;
            } catch (IOException e) {
                return null;
            }
    }
    public static void writeProperty(File propertyFile, String key, String value) {
        try {
            FileWriter writer = new FileWriter(propertyFile);
            writer.flush();
            writer.write(key + "=" + value + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Popup utility
    public static Popup createPopup(final String message) {
        final Popup popup = new Popup();
        popup.setAutoFix(true);
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);
        Label label = new Label(message);
        label.setOnMouseReleased(e -> popup.hide());
        label.setStyle("" +
                "-fx-text-fill: white; " +
                "-fx-min-width: 250px; " +
                "-fx-min-height: 35px; " +
                "-fx-background-color: #ad0000;");
        label.setAlignment(Pos.BASELINE_CENTER);
        popup.getContent().add(label);
        return popup;
    }
    public static void showPopupMessage(final String message, final Stage stage) {
        final Popup popup = createPopup(message);
        popup.setOnShown(e -> {
            popup.setX(stage.getX() + stage.getWidth()/2 - popup.getWidth()/2);
            popup.setY(stage.getY() + stage.getHeight()/3f - popup.getHeight()/2);
        });
        popup.show(stage);
    }

    //Def gridPane conf
    public static GridPane defGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));
        return gridPane;
    }

    // Dynamically update stage title
    public static void primaryStageUpdateTitle(Stage primaryStage) {
        TextField titleField = new TextField();
        titleField.textProperty().addListener((obs, oldText, newText) ->
                primaryStage.setTitle(newText));
    }
    public static void primaryStageSetAndUpdateTitle(Stage primaryStage, String title) {
        primaryStage.setTitle(title);
        primaryStageUpdateTitle(primaryStage);
    }
    public static void setLocaleAndUpdate(Locale locale, Stage primaryStage) {
        SecretImgUtil.setLocale(locale);
        primaryStage.setTitle(SecretImgUtil.get("langChoose.title"));
        SecretImgUtil.primaryStageUpdateTitle(primaryStage);
    }
    // Change scene
    public static void changeStageScene(Stage stage, Scene scene) {
        stage.setScene(scene);
    }
    public static void changeStageScene(Stage stage, Scene scene, String title) {
        changeStageScene(stage, scene);
        stage.setTitle(title);
    }

    // ArrayList with all files to encrypt from selected directory

    // passphrase validation util
    public static boolean validatePassphrase(String passphrase) {
        return true;
    }
}

