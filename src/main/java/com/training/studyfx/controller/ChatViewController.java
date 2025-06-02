package com.training.studyfx.controller;

import com.training.studyfx.ChatHistoryManager;
import com.training.studyfx.SocketManager;
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

import java.io.IOException;

public class ChatViewController implements SocketManager.MessageListener {
    @FXML private ScrollPane scrollPane;
    @FXML private TextField messageField;
    @FXML private TextField usernameField;
    @FXML private VBox chatContainer;
    @FXML private Text emptyStateText;
    @FXML private Button emojiButton;

    private Popup emojiPopup;
    private GridPane emojiGrid;
    private SocketManager socketManager;
    private ChatHistoryManager chatHistoryManager;

    public void initialize() {
        scrollPane.setFitToWidth(true);
        scrollPane.setVvalue(1.0);
        chatContainer.getStylesheets().add(getClass().getResource("/styles/ui.css").toExternalForm());

        initEmojiPopup();
        socketManager = SocketManager.getInstance();
        socketManager.addMessageListener(this);
        
        chatHistoryManager = ChatHistoryManager.getInstance();
        loadChatHistory();
    }

    private void loadChatHistory() {
        for (String message : chatHistoryManager.loadHistory()) {
            // Tách timestamp và nội dung tin nhắn
            String[] parts = message.split(" \\| ", 2);
            if (parts.length == 2) {
                appendToChat(parts[1]); // Chỉ hiển thị nội dung tin nhắn
            }
        }
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

    @FXML
    private void connectToServer() {
        try {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                appendToChat("Please enter a username");
                return;
            }

            if (!socketManager.isConnected()) {
                socketManager.connect(username);
                appendToChat("Connected to server as " + username);
            } else {
                String oldUsername = socketManager.getUsername();
                if (!oldUsername.equals(username)) {
                    socketManager.sendMessage(oldUsername + " has changed your username to " + username);
                }
            }
        } catch (IOException e) {
            appendToChat("Error connecting to server: " + e.getMessage());
        }
    }

    @FXML
    private void sendMessage() {
        try {
            if (!socketManager.isConnected()) {
                appendToChat("Not connected to server. Please enter your username to join the chat");
                return;
            }
            String message = messageField.getText().trim();
            if (!message.isEmpty()) {
                String fullMessage = socketManager.getUsername() + ": " + message;
                socketManager.sendMessage(fullMessage);
                // Chỉ lưu tin nhắn khi gửi
                chatHistoryManager.saveMessage(fullMessage);
                messageField.clear();
            }
        } catch (IOException e) {
            appendToChat("Error sending message: " + e.getMessage());
        }
    }

    @Override
    public void onMessageReceived(String message) {
        appendToChat(message);
        // Không lưu tin nhắn khi nhận
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
            else if (message.contains("has changed your username to")) {
                messageLabel.getStyleClass().add("join-notification");
                messageBox.setAlignment(Pos.CENTER);
            }
            else if (message.startsWith(socketManager.getUsername())) {
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
}
