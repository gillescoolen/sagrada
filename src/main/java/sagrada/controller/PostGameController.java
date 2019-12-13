package sagrada.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import sagrada.model.Game;

import java.io.IOException;

public class PostGameController {
    @FXML
    private HBox objectiveCardBox, toolCardBox, leaderBoard;

    private final Game game;
    private final GameController gameController;

    public PostGameController(Game game, GameController gameController) {
        this.game = game;
        this.gameController = gameController;
    }

    @FXML
    protected void initialize() {
        try {
            this.loadToolCards();
            this.loadPublicObjectiveCards();
            this.loadPlayers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadToolCards() throws IOException {
        for (var toolCard : this.game.getToolCards()) {
            var loader = new FXMLLoader(getClass().getResource("/views/game/toolCard.fxml"));
            loader.setController(new PublicObjectiveCardController(toolCard));
            this.toolCardBox.getChildren().add(loader.load());
        }
    }

    private void loadPublicObjectiveCards() throws IOException {
        for (var publicObjectiveCard : this.game.getObjectiveCards()) {
            var loader = new FXMLLoader(getClass().getResource("/views/game/publicObjectiveCard.fxml"));
            loader.setController(new PublicObjectiveCardController(publicObjectiveCard));
            this.objectiveCardBox.getChildren().add(loader.load());
        }
    }

    private void loadPlayers() throws IOException {
        for (var player : this.game.getPlayers()) {
            var loader = new FXMLLoader(getClass().getResource("/views/game/postPlayer.fxml"));
            loader.setController(new PostPlayerController(player, this.gameController));
            this.leaderBoard.getChildren().add(loader.load());
        }
    }
}
