package sagrada.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.PlayerRepository;
import sagrada.model.Account;
import sagrada.model.Game;
import sagrada.model.Player;
import sagrada.util.StartGame;

import java.io.IOException;
import java.sql.SQLException;

public class GameController {
    @FXML
    private VBox rowOne;
    @FXML
    private VBox rowTwo;

    private final Game game;
    private final Account account;
    private final PlayerRepository playerRepository;

    public GameController(DatabaseConnection connection, Game game, Account account) {
        if (game.getOwner().getAccount().getUsername().equals(account.getUsername())) {
            var startGame = new StartGame(game, connection);
            this.game = startGame.getCreatedGame();
        } else {
            this.game = game;
        }

        this.playerRepository = new PlayerRepository(connection);
        this.account = account;
    }

    @FXML
    protected void initialize() {
        for (var player : this.game.getPlayers()) {
            if (player.getAccount().getUsername().equals(this.account.getUsername())) {
                try {
                    this.initializeWindowOptions(player);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initializeWindowOptions(Player player) throws IOException {
        var i = 1;

        if (player.getCardOptions().size() == 0) {
            try {
                var players = this.playerRepository.getAllGamePlayers(this.game);
                this.game.addPlayers(players);
                player = this.game.getPlayerByName(player.getAccount().getUsername());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        for (var patternCard : player.getCardOptions()) {
            var controller = new WindowPatternCardController(patternCard);
            var loader = new FXMLLoader(getClass().getResource("/views/game/windowPatternCard.fxml"));

            loader.setController(controller);

            if (i <= 2) {
                this.rowOne.getChildren().add(loader.load());
            } else {
                this.rowTwo.getChildren().add(loader.load());
            }

            ++i;
        }
    }
}
