package sagrada.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.PlayerRepository;
import sagrada.database.repositories.PublicObjectiveCardRepository;
import sagrada.database.repositories.ToolCardRepository;
import sagrada.model.Account;
import sagrada.model.Game;
import sagrada.model.Player;
import sagrada.model.PublicObjectiveCard;
import sagrada.util.StartGame;

import java.io.IOException;
import java.sql.SQLException;

public class GameController {
    @FXML
    private VBox rowOne;
    @FXML
    private VBox rowTwo;
    @FXML
    private HBox toolCardBox;
    @FXML
    private HBox publicObjectiveCardBox;
    @FXML
    private HBox privateObjectiveCardBox;

    private final Game game;
    private final Account account;
    private final PlayerRepository playerRepository;
    private final PublicObjectiveCardRepository publicObjectiveCardRepository;
    private final ToolCardRepository toolCardRepository;

    public GameController(DatabaseConnection connection, Game game, Account account) {
        this.publicObjectiveCardRepository = new PublicObjectiveCardRepository(connection);
        this.toolCardRepository = new ToolCardRepository(connection);

        if (game.getOwner().getAccount().getUsername().equals(account.getUsername())) {
            var startGame = new StartGame(game, connection);
            this.game = startGame.getCreatedGame();
        } else {
            this.game = game;

            try {
                var publicObjectiveCards = this.publicObjectiveCardRepository.getAllByGameId(this.game.getId());
                var toolCards = this.toolCardRepository.getAllByGameId(this.game.getId());

                for (var publicObjectiveCard : publicObjectiveCards) {
                    this.game.addObjectiveCard(publicObjectiveCard);
                }

                for (var toolCard : toolCards) {
                    this.game.addToolCard(toolCard);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
                    this.initializePrivateObjectiveCard(this.game.getPlayerByName(player.getAccount().getUsername()));
                    this.initializePublicObjectiveCards();
                    this.initializeToolCards();
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

    private void initializePrivateObjectiveCard(Player player) throws IOException {
        var loader = new FXMLLoader(getClass().getResource("/views/game/privateObjectiveCard.fxml"));
        loader.setController(new PrivateObjectiveCardController(player));
        this.privateObjectiveCardBox.getChildren().add(loader.load());
    }

    private void initializePublicObjectiveCards() throws IOException {
        for (var publicObjectiveCard : this.game.getObjectiveCards()) {
            var loader = new FXMLLoader(getClass().getResource("/views/game/publicObjectiveCard.fxml"));
            loader.setController(new PublicObjectiveCardController(publicObjectiveCard));
            this.publicObjectiveCardBox.getChildren().add(loader.load());
        }
    }

    private void initializeToolCards() throws IOException {
        for (var toolCard : this.game.getToolCards()) {
            var loader = new FXMLLoader(getClass().getResource("/views/game/toolCard.fxml"));
            loader.setController(new ToolCardController(toolCard));
            this.toolCardBox.getChildren().add(loader.load());
        }
    }
}
