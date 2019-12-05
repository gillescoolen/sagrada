package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.GameRepository;
import sagrada.database.repositories.PlayerRepository;
import sagrada.model.Game;

import java.sql.SQLException;
import java.util.Optional;


public class GameController {
    @FXML
    private VBox rowOne;
    @FXML
    private VBox rowTwo;

    private final Game game;
    private final DatabaseConnection connection;

    public GameController(DatabaseConnection connection, Game game) {
        this.game = game;
        this.connection = connection;
    }

    @FXML
    protected void initialize() {
        
    }
}
