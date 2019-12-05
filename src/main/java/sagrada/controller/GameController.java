package sagrada.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import sagrada.database.DatabaseConnection;
import sagrada.model.Account;

import java.io.IOException;

public class GameController {
    @FXML
    private VBox rowOne;
    @FXML
    private VBox rowTwo;

    private Account user;
    private DatabaseConnection databaseConnection;

    public GameController(DatabaseConnection databaseConnection, Account account) {
        this.user = account;
        this.databaseConnection = databaseConnection;
    }

    @FXML
    protected void initialize() {
        try {
            var loader = new FXMLLoader(getClass().getResource("/views/chat/chatBox.fxml"));
            loader.setController(new ChatController(databaseConnection, user, chatRepository));
            this.rowOne.getChildren().add(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
