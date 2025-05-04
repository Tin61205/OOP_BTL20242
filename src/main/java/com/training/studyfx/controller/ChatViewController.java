package com.training.studyfx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.io.*;
import java.net.Socket;


public class ChatViewController {
    @FXML private ScrollPane scrollPane;
    @FXML private TextArea chatArea;
    @FXML private TextField messageField;
    @FXML private TextField usernameField;

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public void initialize() {
        // Initialize any necessary components
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
            new Thread(this::listenForMessages).start();

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

                appendToChat(username + ": " + message);

                messageField.clear();
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
                appendToChat(messageFromServer);
            }
        } catch (IOException e) {
            appendToChat("Disconnected from server");
            closeEverything();
        }
    }

    private void appendToChat(String message) {
        javafx.application.Platform.runLater(() -> {
            chatArea.appendText(message + "\n");
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