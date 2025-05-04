//package com.training.studyfx.controller;
//
//import com.training.studyfx.model.History;
//import com.training.studyfx.server.ClientHandler;
//import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
//import javafx.scene.control.Label;
//import javafx.scene.control.ListView;
//import javafx.scene.layout.VBox;
//import javafx.scene.input.MouseEvent;
//
//import java.net.URL;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.ResourceBundle;
//
//public class ListViewController implements Initializable {
//
//    @FXML
//    private ListView<History> historyListView;
//    private ListView<ClientHandler> clientListView;
//    @FXML
//    private VBox chatListContainer;
//
//    private ChatbotViewController chatViewController;
//
//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
////
////        if (historyListView == null) {
////            historyListView = new ListView<>();
////
////            chatListContainer.getChildren().add(historyListView);
////        }
////        // Set up a default chat history.
////        List<History> defaultHistory = new ArrayList<>();
////        defaultHistory.add(new History(LocalDateTime.now(), new ArrayList<>()));
////
////
//    }
//
//    public void initialize() {
//
//    }
//
//    public void setChatViewController(ChatbotViewController chatViewController) {
//        this.chatViewController = chatViewController;
//    }
//
//}