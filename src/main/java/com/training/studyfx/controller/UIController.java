package com.training.studyfx.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.training.studyfx.App;
import com.training.studyfx.service.UserService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class UIController implements Initializable {
    @FXML
    private HBox Profile;

    @FXML
    private HBox Chat;

    @FXML
    private HBox Chatbot;

    @FXML
    private HBox Setting;
    @FXML
    private AnchorPane mainContentArea;

    @FXML
    private AnchorPane bot_area;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {

            loadView("ProfileView");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //setupSearch();
        //setupFilterButtons();
     }

    @FXML
    private void handleLogout() {

        Scene currentScene = mainContentArea.getScene();
        Stage stage = (Stage) currentScene.getWindow();

        // Logout the user
        UserService.getInstance().logout();

        try {
            // Load the login scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/training/studyfx/LoginView.fxml"));
            Scene loginScene = new Scene(loader.load(), 400, 500);

            // Set the new scene on the stage
            stage.setScene(loginScene);
            stage.centerOnScreen();
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
            // mainContentArea.getChildren().add(chatbotView);
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

    private void loadView(String viewName) throws IOException {         // Load main content area
        mainContentArea.getChildren().clear();
            // Find the Chat_View HBox and add the view to it
        HBox chatView = (HBox) mainContentArea.lookup("#Chat_View");
      if (chatView != null) {
            chatView.getChildren().clear();
            chatView.getChildren().add(App.getView(viewName));
        } else {
            // Fallback if Chat_View is not found
            mainContentArea.getChildren().add(App.getView(viewName));
        }
    }
    @FXML
    private TextField searchField;

    @FXML
    private Button allChatsBtn;

    @FXML
    private Button savedChatsBtn;

    private void setupFilterButtons() {
    allChatsBtn.setOnAction(event -> {
        setFilterButtonSelected(allChatsBtn);
        System.out.println("Showing all chats");
        // Here you would add code to show all chats when you're ready
    });
    savedChatsBtn.setOnAction(event -> {
        setFilterButtonSelected(savedChatsBtn);
        System.out.println("Showing saved chats");
        // Here you would add code to show saved chats when you're ready
    });
}
    private void setFilterButtonSelected(Button selectedButton) {
    // Reset styling for all filter buttons
     allChatsBtn.getStyleClass().remove("button-selected");
     savedChatsBtn.getStyleClass().remove("button-selected");
     // Add selected styling to the clicked button
     selectedButton.getStyleClass().add("button-selected");
}
    @FXML
    private HBox list;

    private void loadListView() throws IOException {

        list.getChildren().clear();


        FXMLLoader loader = new FXMLLoader(getClass().getResource("ListView.fxml"));
        Pane listViewPane = loader.load();

        // Add the ListView to the list HBox
        list.getChildren().add(listViewPane);

    }



}
