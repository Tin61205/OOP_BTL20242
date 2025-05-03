package com.training.studyfx.controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;



// ************* CHƯA FIX *************
// **************************


public class ChatListItem extends HBox {

            private ChatViewController chatViewController;

            public ChatListItem(String name, String message, boolean isOnline, ChatViewController chatViewController) {
                this.chatViewController = chatViewController;

                setAlignment(Pos.CENTER_LEFT);
                setSpacing(10);
                setPadding(new Insets(8));
                setStyle("-fx-background-color: transparent;");

                // Avatar with online indicator
                StackPane avatarContainer = new StackPane();

                Circle avatar = new Circle(20);
                avatar.setFill(Color.rgb(240, 240, 240));

                // First letter of name as avatar text
                Text initial = new Text(name.substring(0, 1).toUpperCase());
                initial.setFont(Font.font("System", FontWeight.BOLD, 14));

                avatarContainer.getChildren().addAll(avatar, initial);

                // Online indicator
                if (isOnline) {
                    Circle onlineIndicator = new Circle(6);
                    onlineIndicator.setFill(Color.rgb(76, 217, 100));
                    onlineIndicator.setStroke(Color.WHITE);
                    onlineIndicator.setStrokeWidth(2);
                    StackPane.setAlignment(onlineIndicator, Pos.BOTTOM_RIGHT);
                    avatarContainer.getChildren().add(onlineIndicator);
                }

                // User info
                VBox userInfo = new VBox(3);
                Label nameLabel = new Label(name);
                nameLabel.setStyle("-fx-font-weight: bold;");
                Label messageLabel = new Label(message);
                messageLabel.setStyle("-fx-text-fill: #707070; -fx-font-size: 12px;");
                messageLabel.setWrapText(true);
                userInfo.getChildren().addAll(nameLabel, messageLabel);

                getChildren().addAll(avatarContainer, userInfo);

                // Hover effect
                setOnMouseEntered(e ->
                    setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 5px;"));
                setOnMouseExited(e ->
                    setStyle("-fx-background-color: transparent;"));
                setOnMousePressed(e ->
                    setStyle("-fx-background-color: #e8e8e8; -fx-background-radius: 5px;"));

                setOnMouseClicked(e -> {
                    System.out.println("Selected: " + name);
                    // Update the chat view with the selected chat partner
                    if (this.chatViewController != null) {
                        this.chatViewController.setChatPartner(name);
                    } else {
                        System.out.println("ChatViewController is null");
                    }
                });
            }


            public ChatListItem(String name, String message, boolean isOnline) {
                this(name, message, isOnline, null);
            }
        }