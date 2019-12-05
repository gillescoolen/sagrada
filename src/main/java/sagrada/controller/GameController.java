package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import sagrada.model.Game;

public class GameController {
    @FXML
    private VBox rowOne;
    @FXML
    private VBox rowTwo;

    private final Game game;

    public GameController(Game game) {
        this.game = game;
    }

    @FXML
    protected void initialize() {

    }
}
