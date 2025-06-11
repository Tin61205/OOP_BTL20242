package com.training.studyfx.controller;

import com.training.studyfx.model.ChatMessage;
import com.training.studyfx.model.ChatMessage.MessageType;
import com.training.studyfx.service.GeminiService;
import com.training.studyfx.util.MarkdownToHtml;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.scene.layout.Region;
import javafx.scene.Node;
import javafx.util.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.net.URL;

public class ChatbotViewController implements Initializable {

    @FXML
    private ScrollPane chatbotScrollPane;

    @FXML
    private VBox chatbotMessagesContainer;

    @FXML
    private TextField chatbotInput;

    private final GeminiService geminiService = new GeminiService();

    // Store chat messages for the current session only.
    private List<ChatMessage> chatHistory = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup scroll behavior
        if (chatbotScrollPane != null) {
            // Enable smooth scrolling
            chatbotScrollPane.setPannable(false);
            chatbotScrollPane.setFitToWidth(true);
            chatbotScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            chatbotScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        }
        
        if (chatbotMessagesContainer != null) {
            addBotMessage("**Chào bạn!** Tôi là *Chatbot cá nhân* của bạn. Tôi có thể giúp gì cho bạn hôm nay?");
        }
        if (chatbotInput != null) {
            chatbotInput.setOnKeyPressed(event -> {
                if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                    handleSendMessage();
                }
            });
        }
    }

    @FXML
    private void handleSendMessage() {
        if (chatbotInput == null)
            return;
        String messageText = chatbotInput.getText().trim();
        if (!messageText.isEmpty()) {
            addUserMessage(messageText);
            chatbotInput.clear();
            processUserMessage(messageText);
        }
    }

    private void processUserMessage(String message) {
        addTypingIndicator();
        new Thread(() -> {
            try {
                String botResponse = geminiService.generateResponse(message);
                Platform.runLater(() -> {
                    removeTypingIndicator();
                    addBotMessage(botResponse);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    removeTypingIndicator();
                    addBotMessage("Sorry, I encountered an error: " + e.getMessage());
                });
            }
        }).start();
    }

    private void addUserMessage(String text) {
        ChatMessage message = new ChatMessage(text, MessageType.USER, getCurrentTime());
        chatHistory.add(message);
        addStyledMessageToContainer("User: " + message.getText(), "user-message", false);
    }

    private void addBotMessage(String text) {
        ChatMessage message = new ChatMessage(text, MessageType.BOT, getCurrentTime());
        chatHistory.add(message);
        addStyledMessageToContainer("Bot: " + message.getText(), "bot-message", true);
    }

    private void addTypingIndicator() {
        addStyledMessageToContainer("Bot: Typing...", "bot-message", false);
    }

    private void removeTypingIndicator() {
        int count = chatbotMessagesContainer.getChildren().size();
        if (count > 0) {
            Node lastNode = chatbotMessagesContainer.getChildren().get(count - 1);
            
            if (lastNode instanceof HBox) {
                HBox hbox = (HBox) lastNode;
                if (hbox.getChildren().size() > 0) {
                    Node child = hbox.getChildren().get(0);
                    if (child instanceof Label) {
                        Label label = (Label) child;
                        if (label.getText().contains("Typing...")) {
                            chatbotMessagesContainer.getChildren().remove(count - 1);
                        }
                    }
                }
            } else if (lastNode instanceof Label) {
                Label lastLabel = (Label) lastNode;
                if (lastLabel.getText().contains("Typing...")) {
                    chatbotMessagesContainer.getChildren().remove(count - 1);
                }
            } else if (lastNode instanceof WebView) {
                // Không cần kiểm tra nội dung WebView vì typing indicator luôn là Label
                // Chỉ xóa nếu đây là node cuối cùng được thêm
                chatbotMessagesContainer.getChildren().remove(count - 1);
            }
        }
    }


    private void addStyledMessageToContainer(String messageText, String styleClass, boolean isMarkdown) {
        Node messageNode;
        
        if (isMarkdown && "bot-message".equals(styleClass)) {
            // Xử lý markdown cho tin nhắn bot bằng WebView
            String content = messageText;
            if (messageText.startsWith("Bot: ")) {
                content = messageText.substring(5); // Bỏ "Bot: " prefix
            }
            
            WebView webView = new WebView();
            webView.setPrefHeight(Region.USE_COMPUTED_SIZE);
            webView.setMaxHeight(Double.MAX_VALUE);
            webView.setMaxWidth(800); // Tăng width để phù hợp với UI mới
            webView.getEngine().setUserStyleSheetLocation(null);
            
            // Disable WebView scrolling để cho phép parent ScrollPane handle scroll
            webView.getEngine().setJavaScriptEnabled(true);
            
            // Enable mouse scroll passthrough từ WebView lên ScrollPane
            webView.setOnScroll(event -> {
                // Pass scroll events to parent ScrollPane
                if (chatbotScrollPane != null) {
                    chatbotScrollPane.fireEvent(event.copyFor(chatbotScrollPane, chatbotScrollPane));
                }
                event.consume();
            });
            
            // Chuyển đổi markdown thành HTML
            String htmlContent = MarkdownToHtml.convertToHtml(content);
            webView.getEngine().loadContent(htmlContent);
            
            // Điều chỉnh chiều cao theo nội dung và disable internal scrolling
            webView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                    Platform.runLater(() -> {
                        // Disable internal scrolling
                        webView.getEngine().executeScript("document.body.style.overflow = 'hidden';");
                        
                        // Auto resize height
                        Object heightObj = webView.getEngine().executeScript("document.body.scrollHeight");
                        if (heightObj instanceof Number) {
                            double height = ((Number) heightObj).doubleValue();
                            webView.setPrefHeight(height + 20); // Add some padding
                            webView.setMinHeight(height + 20);
                            webView.setMaxHeight(height + 20);
                        }
                        
                                                 // Auto scroll to bottom after a short delay
                         scrollToBottom();
                    });
                }
            });
            
            webView.setOpacity(0); // Start invisible
            messageNode = webView;
        } else {
            // Sử dụng Label cho tin nhắn thông thường
            Label messageLabel = new Label(messageText);
            messageLabel.setFont(new Font(14));
            messageLabel.getStyleClass().add(styleClass);
            messageLabel.setWrapText(true);
            messageLabel.setOpacity(0); // Start invisible
            messageNode = messageLabel;
        }

        if ("user-message".equals(styleClass)) {
            HBox hbox = new HBox(messageNode);
            hbox.setAlignment(Pos.CENTER_RIGHT);
            chatbotMessagesContainer.getChildren().add(hbox);
            applyFadeTransition(messageNode);
            // Auto scroll for user messages
            scrollToBottom();
        } else {
            chatbotMessagesContainer.getChildren().add(messageNode);
            applyFadeTransition(messageNode);
            // Auto scroll for bot messages is handled in WebView load listener
            if (!isMarkdown) {
                scrollToBottom();
            }
        }
    }

    private void applyFadeTransition(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(500), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private String getCurrentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalDateTime.now().format(formatter);
    }
    
    private void scrollToBottom() {
        Platform.runLater(() -> {
            // Multiple attempts to ensure scroll works
            chatbotScrollPane.setVvalue(1.0);
            Platform.runLater(() -> {
                chatbotScrollPane.setVvalue(1.0);
            });
        });
    }
}