package sagrada.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import sagrada.database.DatabaseConnection;
import sagrada.model.Player;

import java.io.IOException;

public class PostPlayerController {
    @FXML
    private Text playerName, score;
    @FXML
    private HBox windowPatternCardBox;
    @FXML
    private Rectangle privateObjectiveColor;

    private final Player player;
    private final GameController gameController;
    private final DatabaseConnection databaseConnection;

    public PostPlayerController(Player player, GameController gameController, DatabaseConnection connection) {
        this.player = player;
        this.gameController = gameController;
        this.databaseConnection = connection;
    }

    @FXML
    protected void initialize() {
        this.playerName.setText(this.player.getAccount().getUsername());
        this.score.setText("" + this.player.getScore());
        this.initializePatternCard();
        this.loadPrivateObjectiveCard();
    }

    private void initializePatternCard() {
        var loader = new FXMLLoader(getClass().getResource("/views/game/windowPatternCard.fxml"));
        loader.setController(new WindowPatternCardController(this.databaseConnection, this.player, this.gameController, true));

        Platform.runLater(() -> {
            try {
                this.windowPatternCardBox.getChildren().add(loader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void loadPrivateObjectiveCard() {
        this.privateObjectiveColor.setFill(Color.valueOf(this.player.getPrivateObjectiveCard().getColor().getColor()));
    }
}
