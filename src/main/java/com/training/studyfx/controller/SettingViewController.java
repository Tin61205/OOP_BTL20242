package com.training.studyfx.controller;
import com.training.studyfx.App;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.training.studyfx.SocketManager;

public class SettingViewController implements Initializable {

    @FXML
    private ComboBox<String> themeSelector;

    @FXML
    private CheckBox notificationsToggle;

    @FXML
    private CheckBox soundToggle;

    @FXML
    private Slider fontSizeSlider;

    @FXML
    private Button saveButton;

    @FXML
    private Label saveConfirmationLabel;

    @FXML
    private Button logoutbutton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        themeSelector.getItems().addAll("Light", "Dark", "System Default");
        themeSelector.setValue("Light");
        notificationsToggle.setSelected(true);
        soundToggle.setSelected(true);
        fontSizeSlider.setValue(14.0);

        saveButton.setOnAction(e -> saveSettings());
        logoutbutton.setOnAction(e -> handleLogout());

    }

    private void saveSettings() {
        String theme = themeSelector.getValue();
        boolean notifications = notificationsToggle.isSelected();
        boolean sound = soundToggle.isSelected();
        double fontSize = fontSizeSlider.getValue();


        // Show confirmation
        saveConfirmationLabel.setText("Settings saved successfully!");
        saveConfirmationLabel.setVisible(true);

        // Apply settings to the UI
        applySettings(theme, fontSize);

        // Hide confirmation after 3 seconds
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                Platform.runLater(() -> saveConfirmationLabel.setVisible(false));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // ***********
    // &&&&&&&&&&&CHƯA UPDATE HIỆU ỨNG
    private void applySettings(String theme, double fontSize) {
        Scene scene = saveButton.getScene();
        if (scene != null) {

            scene.getStylesheets().clear();
            if ("Dark".equals(theme)) {
                scene.getStylesheets().add(getClass().getResource("/styles/dark.css").toExternalForm());
            } else {
                scene.getStylesheets().add(getClass().getResource("/styles/light.css").toExternalForm());
            }

            scene.getRoot().setStyle("-fx-font-size: " + fontSize + "px;");
        }
    }
    private void handleLogout() {
        try {
            if (SocketManager.getInstance().isConnected()) {
                SocketManager.getInstance().sendMessage(SocketManager.getInstance().getUsername() + " has left the chat");
            }
            SocketManager.getInstance().reset();
            App.setRoot("LoginView");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}