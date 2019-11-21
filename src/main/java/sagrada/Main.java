package sagrada;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        var sagrada = getClass().getResource("/views/login.fxml");
        Parent root = FXMLLoader.load(sagrada);
        primaryStage.setTitle("Sagrada");
        primaryStage.setScene(new Scene(root, 1200, 720));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}