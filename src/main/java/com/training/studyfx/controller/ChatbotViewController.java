package com.training.studyfx.controller;

import com.training.studyfx.model.ChatMessage;
import com.training.studyfx.model.ChatMessage.MessageType;
import com.training.studyfx.service.GeminiService;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
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
        if (chatbotMessagesContainer != null) {
            addBotMessage("Hello! I'm your Gemini AI assistant. How can I help you today?");
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
        addStyledMessageToContainer("User: " + message.getText(), "user-message");
    }

    private void addBotMessage(String text) {
        ChatMessage message = new ChatMessage(text, MessageType.BOT, getCurrentTime());
        chatHistory.add(message);
        addStyledMessageToContainer("Bot: " + message.getText(), "bot-message");
    }

    private void addTypingIndicator() {
        addStyledMessageToContainer("Bot: Typing...", "bot-message");
    }

    private void removeTypingIndicator() {
        int count = chatbotMessagesContainer.getChildren().size();
        if (count > 0) {

            if (chatbotMessagesContainer.getChildren().get(count - 1) instanceof HBox) {
                HBox hbox = (HBox) chatbotMessagesContainer.getChildren().get(count - 1);
                Label label = (Label) hbox.getChildren().get(0);
                if (label.getText().contains("Typing...")) {
                    chatbotMessagesContainer.getChildren().remove(count - 1);
                }
            } else {
                Label lastLabel = (Label) chatbotMessagesContainer.getChildren().get(count - 1);
                if (lastLabel.getText().contains("Typing...")) {
                    chatbotMessagesContainer.getChildren().remove(count - 1);
                }
            }
        }
    }


    private void addStyledMessageToContainer(String messageText, String styleClass) {
        Label messageLabel = new Label(messageText);
        messageLabel.setFont(new Font(14));
        messageLabel.getStyleClass().add(styleClass);
        messageLabel.setWrapText(true);
        messageLabel.setOpacity(0); // Start invisible

        if ("user-message".equals(styleClass)) {
            HBox hbox = new HBox(messageLabel);
            hbox.setAlignment(Pos.CENTER_RIGHT);
            chatbotMessagesContainer.getChildren().add(hbox);
            applyFadeTransition(messageLabel);
        } else {
            chatbotMessagesContainer.getChildren().add(messageLabel);
            applyFadeTransition(messageLabel);
        }
        Platform.runLater(() -> chatbotScrollPane.setVvalue(1.0));
    }

    private void applyFadeTransition(Label label) {
        FadeTransition ft = new FadeTransition(Duration.millis(500), label);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private String getCurrentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalDateTime.now().format(formatter);
    }
}