package com.training.studyfx.controller;
import com.training.studyfx.service.UserService;
import com.training.studyfx.model.User;
import com.training.studyfx.model.Message;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ChatViewController implements Initializable {

        @FXML
        private VBox messagesContainer;

        @FXML
        private TextField messageInput;

        @FXML
        private ScrollPane chatScrollPane;

        private String currentChatPartner = "John Doe"; // demo

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            // Load demo mess
            loadSampleMessages();


            if (messagesContainer != null && chatScrollPane != null) {
                messagesContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
                    if (chatScrollPane != null) {
                        chatScrollPane.setVvalue(1.0);
                    }
                });
            } else {
                System.err.println("Warning: messagesContainer or chatScrollPane is null in ChatViewController");
            }
        }

        private void sendMessage(String content) {
            UserService userService = UserService.getInstance();
            User currentUser = userService.getCurrentUser();
            if (currentUser != null) {
                String sender = currentUser.getUsername();
                Message message = new Message(content, sender, false);
                // Save message to database
                userService.saveMessage(message);
                // Display message in UI
                displayMessage(message);
            }
        }
        private void displayMessage(Message message) {
            if (message.isFromBot()) {
                addReceivedMessage(message.getContent());
            } else {
                addSentMessage(message.getContent());
            }
        }
        @FXML
        private void handleSendMessage() {
            String messageText = messageInput.getText().trim();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
                messageInput.clear();
            }
        }

        @FXML
        private void handleSendMessage(MouseEvent event) {
            handleSendMessage();
        }

        public void setChatPartner(String name) {
            this.currentChatPartner = name;
            messagesContainer.getChildren().clear();
            loadMessagesForPartner(name);
        }


       //**** CHƯA  UPDATE  MÀ MỚI CHỈ DEMO ******
       // ************************
        private void loadMessagesForPartner(String name) {

            loadSampleMessages();
        }

        private void loadSampleMessages() {
            addReceivedMessage("Chào m dạo này sao r");
            addSentMessage("Tao ổn lòi lìa");
            addReceivedMessage("OHHH");
        }

        private void addSentMessage(String messageText) {
            HBox messageBox = new HBox();
            messageBox.setAlignment(Pos.CENTER_RIGHT);
            messageBox.setPadding(new Insets(5, 5, 5, 10));

            Text text = new Text(messageText);
            TextFlow textFlow = new TextFlow(text);
            textFlow.setStyle("-fx-background-color: #0084ff; -fx-background-radius: 20px; -fx-text-fill: white; -fx-padding: 10px;");
            textFlow.setPadding(new Insets(10, 10, 10, 10));
            text.setStyle("-fx-fill: white;");

            messageBox.getChildren().add(textFlow);

            Label timeLabel = new Label(getCurrentTime());
            timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666666;");

            VBox messageContainer = new VBox(messageBox, timeLabel);
            messageContainer.setAlignment(Pos.CENTER_RIGHT);
            messageContainer.setSpacing(2);

            messagesContainer.getChildren().add(messageContainer);
        }

        private void addReceivedMessage(String messageText) {
            HBox messageBox = new HBox();
            messageBox.setAlignment(Pos.CENTER_LEFT);
            messageBox.setPadding(new Insets(5, 10, 5, 5));

            Text text = new Text(messageText);
            TextFlow textFlow = new TextFlow(text);
            textFlow.setStyle("-fx-background-color: #e4e6eb; -fx-background-radius: 20px; -fx-padding: 10px;");
            textFlow.setPadding(new Insets(10, 10, 10, 10));

            messageBox.getChildren().add(textFlow);

            Label timeLabel = new Label(getCurrentTime());
            timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666666;");

            VBox messageContainer = new VBox(messageBox, timeLabel);
            messageContainer.setAlignment(Pos.CENTER_LEFT);
            messageContainer.setSpacing(2);

            messagesContainer.getChildren().add(messageContainer);
        }

        private String getCurrentTime() {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            return now.format(formatter);
        }

        // demo
        private void simulateReceivedMessage(String originalMessage) {

            String response = "You said: " + originalMessage;


            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    javafx.application.Platform.runLater(() -> {
                        addReceivedMessage(response);
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }