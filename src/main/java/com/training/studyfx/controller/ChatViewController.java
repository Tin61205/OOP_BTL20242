package com.training.studyfx.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;

import java.io.*;
import java.net.Socket;

public class ChatViewController {
    @FXML private ScrollPane scrollPane;
    @FXML private TextField messageField;
    @FXML private TextField usernameField;
    @FXML private VBox chatContainer;
    @FXML private Text emptyStateText;

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    @FXML
    private Button emojiButton;

    // Khai báo biến cho popup emoji
    private Popup emojiPopup;
    private GridPane emojiGrid;

    public void initialize() {
        scrollPane.setFitToWidth(true);
        scrollPane.setVvalue(1.0);
        chatContainer.getStylesheets().add(getClass().getResource("/styles/ui.css").toExternalForm());


        initEmojiPopup();

    }

    @FXML
    private void showEmojiPicker() {
        if (emojiPopup.isShowing()) {
            emojiPopup.hide();
        } else {
            // Hiển thị popup dưới nút emoji
            emojiPopup.show(emojiButton.getScene().getWindow(),
                    emojiButton.localToScreen(emojiButton.getBoundsInLocal()).getMinX(),
                    emojiButton.localToScreen(emojiButton.getBoundsInLocal()).getMaxY());
        }
    }
    // Phương thức khởi tạo popup emoji
    private void initEmojiPopup() {
        emojiPopup = new Popup();
        emojiPopup.setAutoHide(true);

        emojiGrid = new GridPane();
        emojiGrid.setPadding(new Insets(10));
        emojiGrid.setHgap(5);
        emojiGrid.setVgap(5);
        emojiGrid.setStyle("-fx-background-color: white; -fx-border-color: gray; -fx-border-width: 1;");

        // Danh sách emoji
        String[] emojis = {
                "😊", "😂", "😍", "🥰", "😎", "😇", "🤔", "😐", "😒", "😢",
                "👍", "👌", "👏", "🙏", "🎉", "🔥", "❤️", "💯", "✅", "⭐"
        };

        int col = 0;
        int row = 0;

        for (String emoji : emojis) {
            Button emojiBtn = new Button(emoji);
            emojiBtn.setStyle("-fx-background-color: transparent;");
            emojiBtn.setOnAction(e -> insertEmoji(emoji));

            emojiGrid.add(emojiBtn, col, row);

            col++;
            if (col > 4) { // 5 emoji mỗi hàng
                col = 0;
                row++;
            }
        }

        emojiPopup.getContent().add(emojiGrid);
    }

    // Phương thức chèn emoji vào text input
    private void insertEmoji(String emoji) {
        messageField.setText(messageField.getText() + emoji);
        emojiPopup.hide();
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
            bufferedWriter.write(username+" has joined the chat");
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
            if (emptyStateText.isVisible()) {
                emptyStateText.setVisible(false);
            }
            Label messageLabel = new Label(message);
            messageLabel.setWrapText(true);

            messageLabel.setStyle("-fx-font-family: 'Segoe UI Emoji', 'Apple Color Emoji', 'Noto Color Emoji', sans-serif;");


            HBox messageBox = new HBox();

            if (message.contains("has joined the chat")) {
                messageLabel.getStyleClass().add("join-notification");
                messageBox.setAlignment(Pos.CENTER);
            }
            else if (message.startsWith(username)) {
                messageLabel.getStyleClass().add("mess-global");
                messageBox.setAlignment(Pos.CENTER_RIGHT);
            }
            else {
                messageLabel.getStyleClass().add("other-global");
                messageBox.setAlignment(Pos.CENTER_LEFT);
            }

            messageBox.getChildren().add(messageLabel);
            chatContainer.getChildren().add(messageBox);

            // Auto-scroll to bottom
            scrollPane.setVvalue(1.0);
        });
    }
    /*
    private void appendToChat(String message) {
        javafx.application.Platform.runLater(() -> {
            if (emptyStateText.isVisible()) {
                emptyStateText.setVisible(false);
            }

            Label messageLabel = new Label(message);
            messageLabel.setWrapText(true);

            // Thêm style hỗ trợ emoji cho label
            //messageLabel.setStyle("-fx-font-family: 'Segoe UI Emoji', 'Apple Color Emoji', 'Noto Color Emoji', sans-serif;");

            HBox messageBox = new HBox();
            messageBox.setPadding(new Insets(5, 10, 5, 10)); // Thêm padding để tin nhắn trông đẹp hơn

            if (message.contains("has joined the chat")) {
                messageLabel.getStyleClass().add("join-notification");
                messageBox.setAlignment(Pos.CENTER);
                // Style cho thông báo tham gia
                messageLabel.setStyle(messageLabel.getStyle() + "-fx-text-fill: #757575; -fx-font-style: italic;");
            }
            else if (message.startsWith(username)) {
                messageLabel.getStyleClass().add("mess-global");
                messageBox.setAlignment(Pos.CENTER_RIGHT);
                // Style cho tin nhắn của người dùng hiện tại
                messageLabel.setStyle(messageLabel.getStyle() + "-fx-background-color: #e3f2fd; -fx-background-radius: 15px; -fx-padding: 8px 12px;");
            }
            else {
                messageLabel.getStyleClass().add("other-global");
                messageBox.setAlignment(Pos.CENTER_LEFT);
                // Style cho tin nhắn của người khác
                messageLabel.setStyle(messageLabel.getStyle() + "-fx-background-color: #f5f5f5; -fx-background-radius: 15px; -fx-padding: 8px 12px;");
            }

            messageBox.getChildren().add(messageLabel);
            chatContainer.getChildren().add(messageBox);

            // Auto-scroll to bottom
            scrollPane.setVvalue(1.0);
        });
    }
*/
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
