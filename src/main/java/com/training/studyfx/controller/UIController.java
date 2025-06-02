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
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Load user avatar into the circle
        loadUserAvatar();
        try {

            loadView("ProfileView");
        } catch (IOException e) {
            e.printStackTrace();
        }
     }


    @FXML
    private void handleProfileClick(MouseEvent event) {
        System.out.println("Profile Clicked!");
        try {
            // Load the Chatbot view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/training/studyfx/ProfileView.fxml"));
            Parent ProfileView = loader.load();

            bot_area.getChildren().clear();
            mainContentArea.getChildren().clear();
            list.getChildren().clear();
            // mainContentArea.getChildren().add(chatbotView);
            bot_area.getChildren().add(ProfileView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChatClick() {

        try {

            //  load ChatView in the main content area
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/training/studyfx/ChatView.fxml"));
            Parent chatView = loader.load();

            bot_area.getChildren().clear();
            mainContentArea.getChildren().clear();
            list.getChildren().clear();
            //mainContentArea.getChildren().add(chatView);
            bot_area.getChildren().add(chatView);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @FXML
    private void handleChatbotClick(MouseEvent event) {
        System.out.println("Chatbot Clicked!");
        try {
            // Load the Chatbot view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/training/studyfx/ChatbotView.fxml"));
            Parent chatbotView = loader.load();

            bot_area.getChildren().clear();
            mainContentArea.getChildren().clear();
            list.getChildren().clear();
           // mainContentArea.getChildren().add(chatbotView);
            bot_area.getChildren().add(chatbotView);



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

            @FXML
    private void handleSettingClick(MouseEvent event) {
                System.out.println("Setting Clicked!");
                try {

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/training/studyfx/SettingView.fxml"));
                    Parent chatbotView = loader.load();

                    bot_area.getChildren().clear();
                    mainContentArea.getChildren().clear();
                    list.getChildren().clear();
                    // mainContentArea.getChildren().add(chatbotView);
                    bot_area.getChildren().add(chatbotView);

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
            // Fallback if Chat_View is not found
            bot_area.getChildren().add(App.getView(viewName));
        }
    }


    @FXML
    private HBox list;



    private void loadUserAvatar() {
        if (avt == null) return;

        User currentUser = UserService.getInstance().getCurrentUser();

        try {
            Image image;
            String imagePath = (currentUser != null && currentUser.getProfileImagePath() != null) ?
                    currentUser.getProfileImagePath() : "/images/default-profile.png";

            // Check if it's a resource path or file path
            if (imagePath.startsWith("/")) {
                // Resource path
                image = new Image(getClass().getResourceAsStream(imagePath));
            } else {
                // File path
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    image = new Image(imageFile.toURI().toString());
                } else {
                    // Fallback to default
                    image = new Image(getClass().getResourceAsStream("/images/default-profile.png"));
                }
            }

            // Set the image as fill for the circle
            avt.setFill(new ImagePattern(image));

        } catch (Exception e) {
            System.err.println("Error loading avatar image: " + e.getMessage());
            // Set default image
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-profile.png"));
            avt.setFill(new ImagePattern(defaultImage));
        }
    }


}
