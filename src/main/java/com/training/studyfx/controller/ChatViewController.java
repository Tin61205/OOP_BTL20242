package com.training.studyfx.controller;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.*;
import java.net.Socket;

public class ChatViewController {
    @FXML private ScrollPane scrollPane;
    @FXML private TextArea chatArea;
    @FXML private TextField messageField;
    @FXML private TextField usernameField;
    @FXML private VBox chatContainer;
    @FXML private Text emptyStateText;

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public void initialize() {
        scrollPane.setFitToWidth(true);
        scrollPane.setVvalue(1.0);
        chatContainer.getStylesheets().add(getClass().getResource("/styles/ui.css").toExternalForm());
    }

    @FXML
    private void connectToServer() {
        try {
            this.username = usernameField.getText().trim();
            if (username.isEmpty()) {
                appendToChat("Please enter a username");
                return;
            }

            this.socket = new Socket("localhost", 1234);
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send username to server
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            appendToChat("Connected to server as " + username);

            // Start thread to listen for messages
            new Thread(this::listenForMessages).start();  // Khởi động thread lắng nghe tin nhắn từ server

        } catch (IOException e) {
            appendToChat("Error connecting to server: " + e.getMessage());
            closeEverything();
        }
    }

    @FXML
    private void sendMessage() {
        try {
            if (socket == null || !socket.isConnected()) {
                appendToChat("Not connected to server. Please enter your username to join the chat");
                return;
            }
            String message = messageField.getText().trim();
            if (!message.isEmpty() && bufferedWriter != null) {
                bufferedWriter.write(username + ": " + message);
                bufferedWriter.newLine();
                bufferedWriter.flush();

                //appendToChat(username + ": " + message);  // Hiển thị tin nhắn của mình trên giao diện

                //messageField.clear();
            }
        } catch (IOException e) {
            appendToChat("Error sending message: " + e.getMessage());
            closeEverything();
        }
    }

    private void listenForMessages() {
        try {
            String messageFromServer;
            while (socket.isConnected() && (messageFromServer = bufferedReader.readLine()) != null) {
                appendToChat(messageFromServer);  // Hiển thị tin nhắn nhận từ server
            }
        } catch (IOException e) {
            appendToChat("Disconnected from server");
            closeEverything();
        }
    }

    private void appendToChat(String message) {
        javafx.application.Platform.runLater(() -> {
            // Hide empty state text when first message appears
            if (emptyStateText.isVisible()) {
                emptyStateText.setVisible(false);
            }

            Label messageLabel = new Label(message);
            messageLabel.setWrapText(true);

            HBox messageBox = new HBox();

            if (message.startsWith(username)) {
                messageLabel.getStyleClass().add("mess-global");
                messageBox.setAlignment(Pos.CENTER_RIGHT);
            } else {
                messageLabel.getStyleClass().add("other-global");
                messageBox.setAlignment(Pos.CENTER_LEFT);
            }

            messageBox.getChildren().add(messageLabel);
            chatContainer.getChildren().add(messageBox);

            // Auto-scroll to bottom
            scrollPane.setVvalue(1.0);
        });
    }

    private void closeEverything() {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
