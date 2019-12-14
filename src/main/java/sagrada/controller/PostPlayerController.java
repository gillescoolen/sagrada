package sagrada.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import sagrada.model.Player;

import java.io.IOException;

public class PostPlayerController {
    @FXML
    private Text playerName, score;
    @FXML
    private HBox windowPatternCardBox;

    private final Player player;
    private final GameController gameController;

    public PostPlayerController(Player player, GameController gameController) {
        this.player = player;
        this.gameController = gameController;
    }

    @FXML
    protected void initialize() {
        this.playerName.setText(this.player.getAccount().getUsername());
        this.score.setText("" + this.player.getScore());

        try {
            this.initializePatternCard();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializePatternCard() throws IOException {
        var loader = new FXMLLoader(getClass().getResource("/views/game/windowPatternCard.fxml"));
        loader.setController(new WindowPatternCardController(this.player, this.gameController));
        this.windowPatternCardBox.getChildren().add(loader.load());
    }
}
