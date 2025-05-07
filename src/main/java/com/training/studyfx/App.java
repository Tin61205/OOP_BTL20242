package com.training.studyfx;
import com.training.studyfx.service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.training.studyfx.server.Server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        //startServer();
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(1234);
                Server server = new Server(serverSocket);
                server.startServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        // Start with login view
        scene = new Scene(loadFXML("LoginView"), 400, 500);
        scene.getStylesheets().add(getClass().getResource("/styles/ui.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Chat Application");
        stage.show();
    }
    // javafx run with spring boot

    private void startServer() {
        // Tạo server lắng nghe trên cổng 1234
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            Server server = new Server(serverSocket);
            // Bắt đầu server
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));

        if (fxml.equals("UI")) {
            Stage stage = (Stage) scene.getWindow();
            stage.setWidth(975);
            stage.setHeight(620);
            stage.centerOnScreen();
        } else if (fxml.equals("LoginView") || fxml.equals("RegisterView")) {
            Stage stage = (Stage) scene.getWindow();
            stage.setWidth(400);
            stage.setHeight(500);
            stage.centerOnScreen();
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static Parent getView(String fxml) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
            Parent view = fxmlLoader.load();

            view.setUserData(fxmlLoader.getController());
            return view;
        } catch (Exception e) {
            System.err.println("Error loading view: " + fxml);
            e.printStackTrace();
            throw e;
        }
    }
    @Override
    public void stop() {
        UserService.getInstance().closeConnection();
    }

}