package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.GameRepository;

import java.sql.SQLException;

public class AccountItemController {
    @FXML
    private Label lbName;

    private final String username;
    private final DatabaseConnection databaseConnection;

    public AccountItemController(String username, DatabaseConnection connection) {
        this.databaseConnection = connection;
        this.username = username;
    }

    @FXML
    protected void initialize() {
        this.fillItem();
    }

    private void fillItem() {
        this.lbName.setText(username);
    }
}
