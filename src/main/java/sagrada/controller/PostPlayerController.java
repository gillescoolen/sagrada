package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import sagrada.model.Player;

public class PostPlayerController {
    @FXML
    private Text playerName, score;

    private final Player player;

    public PostPlayerController(Player player) {
        this.player = player;
    }

    @FXML
    protected void initialize() {
        this.playerName.setText(this.player.getAccount().getUsername());
        this.score.setText("" + this.player.getScore());
    }
}
