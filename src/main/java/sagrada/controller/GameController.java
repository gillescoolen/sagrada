package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import sagrada.database.DatabaseConnection;
import sagrada.model.Game;
import sagrada.util.StartGame;

public class GameController {
    @FXML
    private VBox rowOne;
    @FXML
    private VBox rowTwo;

    private final Game game;
    private final DatabaseConnection connection;

    public GameController(DatabaseConnection connection, Game game) {
        var startGame = new StartGame(game, connection);
        this.game = startGame.getCreatedGame();
        this.connection = connection;
    }

    @FXML
    protected void initialize() {
        
    }
}
