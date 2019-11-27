package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import sagrada.model.Game;

public class LobbyItemController {
    @FXML
    private Label lbName;
    @FXML
    private Label lbSpotsLeft;

    private final Game game;

    public LobbyItemController(Game game) {
        this.game = game;
    }

    @FXML
    protected void initialize() {
        this.lbName.setText(this.game.getOwner().getAccount().getUsername() + "'s Game");
        this.lbSpotsLeft.setText(4 - this.game.getPlayers().size() + " spot(s) left");
    }
}
