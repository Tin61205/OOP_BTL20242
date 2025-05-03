package com.training.studyfx.controller;
import com.training.studyfx.service.UserService;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfileViewController implements Initializable {

    private String profileImagePath = "/images/default-profile.png";

        @FXML
        private ImageView profileImage;

        @FXML
        private TextField nameField;

        @FXML
        private TextField emailField;

        @FXML
        private TextField statusField;

        @FXML
        private Label saveConfirmationLabel;

        @FXML
        private Button changePhotoButton;

        @FXML
        private Button saveButton;

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            // Check if UI components are properly loaded
            if (nameField == null || emailField == null || statusField == null) {
                System.err.println("Error: UI fields are null in ProfileViewController");
                return;
            }

            // Load user profile data
            loadUserProfile();

            if (changePhotoButton != null) {
                changePhotoButton.setOnAction(e -> changeProfilePhoto());
            }

            if (saveButton != null) {
                saveButton.setOnAction(e -> saveProfile());
            }
        }

        private void loadUserProfile() {
            // In a real app, load from database/storage
            nameField.setText("User Name");
            emailField.setText("user@example.com");
            statusField.setText("Available");
        }

        private void changeProfilePhoto() {
            if (profileImage == null) return;

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Profile Image");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );

            File selectedFile = fileChooser.showOpenDialog(profileImage.getScene().getWindow());
            if (selectedFile != null) {
                try {
                    Image image = new Image(selectedFile.toURI().toString());
                    profileImage.setImage(image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    private void saveProfile() {
        String status = statusField.getText().trim();
        UserService userService = UserService.getInstance();


        userService.updateUserProfile(status, profileImagePath);


        saveConfirmationLabel.setVisible(true);


    }
    }