package sagrada.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.GameRepository;
import sagrada.model.Game;
import sagrada.model.Player;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PostGameController {
    @FXML
    private HBox objectiveCardBox, toolCardBox, leaderBoard;

    private final Game game;
    private final GameController gameController;
    private final GameRepository gameRepository;
    private final ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);

    public PostGameController(Game game, GameController gameController, DatabaseConnection connection) {
        this.game = game;
        this.gameController = gameController;
        this.gameRepository = new GameRepository(connection);
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

        Runnable checkForScore = () -> {
            try {
                List<Player> players = this.gameRepository.getAllDonePlayers(this.game);

                if (players.size() > 0) {
                    this.game.addPlayers(players);
                    this.ses.shutdown();

                    this.loadPlayers();
                }

            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        };

        this.ses.scheduleAtFixedRate(checkForScore, 0, 1, TimeUnit.SECONDS);
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
