package com.training.studyfx.controller;

import com.training.studyfx.ChatHistoryManager;
import com.training.studyfx.SocketManager;
import com.training.studyfx.model.User;
import com.training.studyfx.service.GeminiService;
import com.training.studyfx.service.UserService;
import com.training.studyfx.util.MarkdownToHtml;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Popup;
import javafx.util.Duration;

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
    private GeminiService geminiService;
    private User currentUser = UserService.getInstance().getCurrentUser();

    public void initialize() {
        scrollPane.setFitToWidth(true);
        scrollPane.setVvalue(1.0);
        chatContainer.getStylesheets().add(getClass().getResource("/styles/ui.css").toExternalForm());

        scrollPane.setOnScroll(event -> {
            double deltaY = event.getDeltaY();
            double newVValue = scrollPane.getVvalue() - (deltaY / scrollPane.getHeight());
            scrollPane.setVvalue(Math.max(0, Math.min(1, newVValue)));
            event.consume();
        });

        chatContainer.setOnScroll(event -> {
            double deltaY = event.getDeltaY();
            double newVValue = scrollPane.getVvalue() - (deltaY / scrollPane.getHeight());
            scrollPane.setVvalue(Math.max(0, Math.min(1, newVValue)));
            event.consume();
        });

        initEmojiPopup();
        socketManager = SocketManager.getInstance();
        socketManager.addMessageListener(this);

        chatHistoryManager = ChatHistoryManager.getInstance();
        geminiService = new GeminiService();
        loadChatHistory();

        connectToServer();
    }

    private void loadChatHistory() {
        for (String message : chatHistoryManager.loadHistory()) {
            String[] parts = message.split(" \\| ", 2);
            if (parts.length == 2) {
                appendToChat(parts[1]);
            }
        }
        currentUser = UserService.getInstance().getCurrentUser();
        currentUser.bietdanh = currentUser.getFullName() != null ? currentUser.getFullName() : currentUser.getUsername();
    }

    @FXML
    private void showEmojiPicker() {
        if (emojiPopup.isShowing()) {
            emojiPopup.hide();
        } else {
            emojiPopup.show(emojiButton.getScene().getWindow(),
                    emojiButton.localToScreen(emojiButton.getBoundsInLocal()).getMinX(),
                    emojiButton.localToScreen(emojiButton.getBoundsInLocal()).getMaxY());
        }
    }

    private void connectToServer() {
        try {
            if (socketManager.isConnected()) return;

            if (!socketManager.isConnected()) {
                socketManager.connect(currentUser.bietdanh);
                appendToChat("Connected to server as " + currentUser.bietdanh);
            } else {
                String oldUsername = socketManager.getUsername();
                if (!oldUsername.equals(currentUser.bietdanh)) {
                    socketManager.sendMessage(oldUsername + " has changed your username to " + currentUser.bietdanh);
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
                if (message.startsWith("@bot")) {
                    String botMessage = message.substring(4).trim();
                    String systemPrompt = "Bạn là chatbot assistant của một công ty.Hãy trả lời một cách chuyên nghiệp , độ dài vừa phải ,không dài dòng mà đi vào trọng tâm .";
                    String prompt = systemPrompt + botMessage;
                    appendToChat(socketManager.getUsername() + ": " + message);
                    chatHistoryManager.saveMessage(socketManager.getUsername() + ": " + message);

                    appendToChat("Bot: Đang nhập...");

                    new Thread(() -> {
                        try {
                            String botResponse = geminiService.generateResponse(prompt);
                            Platform.runLater(() -> {
                                removeLastMessage();
                                String botMsg = "@#$%^01naffajg: " + botResponse.replace("\n", "<br>");
                                chatHistoryManager.saveMessage(botMsg);
                                try {
                                    socketManager.sendMessage(botMsg);
                                } catch (IOException ex) {
                                    appendToChat("Lỗi gửi tin nhắn r : " + ex.getMessage());
                                }
                            });
                        } catch (Exception e) {
                            Platform.runLater(() -> {
                                removeLastMessage();
                                appendToChat("Lỗi gì đó rồi: " + e.getMessage());
                            });
                        }
                    }).start();
                } else {
                    String fullMessage = socketManager.getUsername() + ": " + message;
                    socketManager.sendMessage(fullMessage);
                    chatHistoryManager.saveMessage(fullMessage);
                }
                messageField.clear();
            }
        } catch (IOException e) {
            appendToChat("Error sending message: " + e.getMessage());
        }
    }

    @Override
    public void onMessageReceived(String message) {
        appendToChat(message);
    }

    private void appendToChat(String message) {
        Platform.runLater(() -> {
            if (emptyStateText.isVisible()) emptyStateText.setVisible(false);

            Node messageNode;
            String username = socketManager.getUsername();

            if (message.startsWith("@#$%^01naffajg:")) {
                String content = message.substring("@#$%^01naffajg:".length()).replace("<br>", "\n");
                WebView webView = new WebView();
                webView.setPrefHeight(Region.USE_COMPUTED_SIZE);
                webView.setMaxWidth(Double.MAX_VALUE);
                webView.setPrefWidth(950);
                webView.setMinWidth(950);
                webView.getEngine().setJavaScriptEnabled(true);

                String htmlContent = MarkdownToHtml.convertToHtml(content);
                webView.getEngine().loadContent(htmlContent);

                webView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                    if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                        Platform.runLater(() -> {
                            webView.getEngine().executeScript("document.body.style.overflow = 'hidden';");
                            Object heightObj = webView.getEngine().executeScript("document.body.scrollHeight");
                            if (heightObj instanceof Number) {
                                double height = ((Number) heightObj).doubleValue();
                                webView.setPrefHeight(height + 20);
                            }
                            scrollToBottom();
                        });
                    }
                });

                webView.setOpacity(0);
                messageNode = webView;
            } else {
                Label messageLabel = new Label(message);
                messageLabel.setMaxWidth(800);
                messageLabel.setWrapText(true);
                messageLabel.setOpacity(0);
                messageNode = messageLabel;
            }

            HBox messageBox = new HBox();
            if (message.contains("has joined the chat") || message.contains("has changed your username to")) {
                messageNode.getStyleClass().add("join-notification");
                messageBox.setAlignment(Pos.CENTER);
            } else if (username != null && message.startsWith(username + ":")) {
                messageNode.getStyleClass().add("mess-global");
                messageBox.setAlignment(Pos.CENTER_RIGHT);
            } else if (message.startsWith("Bot:")) {
                messageNode.getStyleClass().add("bot-message");
                messageBox.setAlignment(Pos.CENTER_RIGHT);
            } else {
                messageNode.getStyleClass().add("other-global");
                messageBox.setAlignment(Pos.CENTER_LEFT);
            }

            messageBox.getChildren().add(messageNode);
            chatContainer.getChildren().add(messageBox);

            applyFadeTransition(messageNode);
            scrollToBottom();
        });
    }

    private void applyFadeTransition(Node node) {
        FadeTransition fade = new FadeTransition(Duration.millis(500), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void scrollToBottom() {
        // Đợi 50ms để đảm bảo layout xong mới scroll
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.millis(50));
        pause.setOnFinished(e -> {
            chatContainer.layout(); // Cập nhật layout
            scrollPane.setVvalue(1.0); // Cuộn xuống cuối
        });
        pause.play();
    }

    private void initEmojiPopup() {
        emojiPopup = new Popup();
        emojiPopup.setAutoHide(true);
        emojiGrid = new GridPane();
        emojiGrid.setPadding(new Insets(10));
        emojiGrid.setHgap(5);
        emojiGrid.setVgap(5);
        emojiGrid.setStyle("-fx-background-color: white; -fx-border-color: gray; -fx-border-width: 1;");

        String[] emojis = {
                "😊", "😂", "😍", "🥰", "😎", "😇", "🤔", "😐", "😒", "😢",
                "😭", "😠", "😲", "🥳", "🤩", "🤯", "😴", "🤤", "😱", "🥺",
                "🥲", "🫠", "🥶", "🥵", "🥴", "🤢", "🤮", "🤧", "🥳", "🤠",
                "🤑", "🤐", "🤫", "🤭", "🧐",
                "👍", "👌", "👏", "🙏", "🙌", "🤝", "🤞", "🤏", "🤘", "🤙",
                "👋", "👎", "👊", "✊", "💪", "🤳", "🤗", "🤷‍♀️", "🤷‍♂️", "🤦‍♀️",
                "🤦‍♂️", "🙇‍♀️", "🙇‍♂️",
        };

        int col = 0;
        int row = 0;
        for (String emoji : emojis) {
            Button emojiBtn = new Button(emoji);
            emojiBtn.setStyle("-fx-background-color: transparent;");
            emojiBtn.setOnAction(e -> insertEmoji(emoji));
            emojiGrid.add(emojiBtn, col, row);
            if (++col > 9) {
                col = 0;
                row++;
            }
        }

        emojiPopup.getContent().add(emojiGrid);
    }

    private void insertEmoji(String emoji) {
        messageField.setText(messageField.getText() + emoji);
        emojiPopup.hide();
    }

    @FXML
    private void removeHistory() {
        ChatHistoryManager.getInstance().clearHistory();
        chatContainer.getChildren().clear();
        emptyStateText.setVisible(true);
    }

    private void removeLastMessage() {
        int count = chatContainer.getChildren().size();
        if (count > 0) {
            chatContainer.getChildren().remove(count - 1);
        }
    }
}
