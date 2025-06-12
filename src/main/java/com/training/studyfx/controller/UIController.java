package com.training.studyfx.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.training.studyfx.App;
import com.training.studyfx.model.User;
import com.training.studyfx.service.UserService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class UIController implements Initializable {
    @FXML
    private Circle avt;

    @FXML
    private AnchorPane mainContentArea;

    @FXML
    private AnchorPane bot_area;

    @FXML
    private HBox list;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Load user avatar into the circle
        loadUserAvatar();
        String targetTab = App.getTargetTab(); // lấy tab cần chuyển đến
        App.clearNextTab(); // xoá sau khi dùng

        try {
            if ("setting".equalsIgnoreCase(targetTab)) {
                handleSettingClick(null); // gọi luôn hàm load SettingView
            } else {
                loadView("AboutView"); // mặc định: load AboutView thay vì ProfileView
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAboutClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/training/studyfx/AboutView.fxml"));
            Parent aboutView = loader.load();

            if (bot_area != null) bot_area.getChildren().clear();
            if (mainContentArea != null) mainContentArea.getChildren().clear();
            if (list != null) list.getChildren().clear();

            if (bot_area != null) bot_area.getChildren().add(aboutView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChatClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/training/studyfx/ChatView.fxml"));
            Parent chatView = loader.load();

            if (bot_area != null) bot_area.getChildren().clear();
            if (mainContentArea != null) mainContentArea.getChildren().clear();
            if (list != null) list.getChildren().clear();

            if (bot_area != null) bot_area.getChildren().add(chatView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChatbotClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/training/studyfx/ChatbotView.fxml"));
            Parent chatbotView = loader.load();

            if (bot_area != null) bot_area.getChildren().clear();
            if (mainContentArea != null) mainContentArea.getChildren().clear();
            if (list != null) list.getChildren().clear();

            if (bot_area != null) bot_area.getChildren().add(chatbotView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSettingClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/training/studyfx/ProfileSettingView.fxml"));
            Parent settingView = loader.load();

            if (bot_area != null) bot_area.getChildren().clear();
            if (mainContentArea != null) mainContentArea.getChildren().clear();
            if (list != null) list.getChildren().clear();

            if (bot_area != null) bot_area.getChildren().add(settingView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String viewName) throws IOException {
        mainContentArea.getChildren().clear();
        bot_area.getChildren().clear();

        HBox chatView = (HBox) mainContentArea.lookup("#Chat_View");
        if (chatView != null) {
            chatView.getChildren().clear();
            chatView.getChildren().add(App.getView(viewName));
        } else {
            bot_area.getChildren().add(App.getView(viewName));
        }
    }

    private void loadUserAvatar() {
        if (avt == null) return;

        User currentUser = UserService.getInstance().getCurrentUser();

        try {
            Image image;
            String imagePath = (currentUser != null && currentUser.getProfileImagePath() != null)
                    ? currentUser.getProfileImagePath()
                    : "/images/default-profile.png";

            if (imagePath.startsWith("/")) {
                image = new Image(getClass().getResourceAsStream(imagePath));
            } else {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    image = new Image(imageFile.toURI().toString());
                } else {
                    image = new Image(getClass().getResourceAsStream("/images/default-profile.png"));
                }
            }

            avt.setFill(new ImagePattern(image));

        } catch (Exception e) {
            System.err.println("Error loading avatar image: " + e.getMessage());
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-profile.png"));
            avt.setFill(new ImagePattern(defaultImage));
        }
    }
}
