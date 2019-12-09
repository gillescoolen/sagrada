package sagrada;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sagrada.controller.LoginController;
import sagrada.database.DatabaseConnection;

import java.sql.SQLException;

public class Main extends Application {
    private DatabaseConnection databaseConnection;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.databaseConnection = new DatabaseConnection();

        var sagrada = new FXMLLoader(getClass().getResource("/views/postgame.fxml"));
//        sagrada.setController(new LoginController(this.databaseConnection));

        Parent root = sagrada.load();
        primaryStage.setTitle("Sagrada");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);

        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();

            try {
                this.databaseConnection.getConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            System.exit(0);
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        var connection = this.databaseConnection.getConnection();

        if (!connection.isClosed()) {
            connection.close();
        }
    }
}
