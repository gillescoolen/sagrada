package sagrada.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.GameRepository;
import sagrada.database.repositories.PlayerRepository;
import sagrada.database.repositories.PublicObjectiveCardRepository;
import sagrada.database.repositories.ToolCardRepository;
import sagrada.model.Account;
import sagrada.model.Game;
import sagrada.model.Player;
import sagrada.util.StartGame;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

    private Game game;
    private final Player player;
    private final DatabaseConnection connection;
    private final PlayerRepository playerRepository;

    public GameController(DatabaseConnection connection, Game game, Account account) {
        this.connection = connection;
        var publicObjectiveCardRepository = new PublicObjectiveCardRepository(connection);
        var toolCardRepository = new ToolCardRepository(connection);
        var gameRepository = new GameRepository(this.connection);

        this.playerRepository = new PlayerRepository(connection);
        this.player = game.getPlayerByName(account.getUsername());

        try {
            if (game.getOwner().getAccount().getUsername().equals(account.getUsername()) && !gameRepository.checkIfGameHasStarted(game)) {
                var startGame = new StartGame(game, connection);
                this.game = startGame.getCreatedGame();
            } else {
                this.game = game;

                var publicObjectiveCards = publicObjectiveCardRepository.getAllByGameId(this.game.getId());
                var toolCards = toolCardRepository.getAllByGameId(this.game.getId());

                for (var publicObjectiveCard : publicObjectiveCards) {
                    this.game.addObjectiveCard(publicObjectiveCard);
                }

                for (var toolCard : toolCards) {
                    this.game.addToolCard(toolCard);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void initialize() {
        for (var player : this.game.getPlayers()) {
            if (player.getAccount().getUsername().equals(this.player.getAccount().getUsername())) {
                try {
                    this.initializeWindowOptions(player);
                    this.initializePrivateObjectiveCard(this.game.getPlayerByName(player.getAccount().getUsername()));
                    this.initializePublicObjectiveCards();
                    this.initializeToolCards();

                    this.checkForPlayerPatternCards();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void checkForPlayerPatternCards() {
        var playerPatternCardsTimer = new Timer();

        playerPatternCardsTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    try {
                        List<Player> players = playerRepository.getAllGamePlayers(game);

                        boolean notEveryoneHasChosen = players.stream().anyMatch(p -> p.getPatternCard() == null);

                        if (notEveryoneHasChosen) {
                            return;
                        }

                        Player currentPlayer = players.stream()
                                .filter(p -> p.getAccount().getUsername().equals(player.getAccount().getUsername()))
                                .findFirst()
                                .orElse(null);

                        if (currentPlayer == null) {
                            return;
                        }

                        Player gamePlayer = game.getPlayerByName(currentPlayer.getAccount().getUsername());

                        if (gamePlayer == null) {
                            var controller = new WindowPatternCardController(connection, currentPlayer.getPatternCard(), currentPlayer);
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/game/windowPatternCard.fxml"));

                            loader.setController(controller);
                            rowOne.getChildren().add(loader.load());

                            playerPatternCardsTimer.cancel();
                        }
                    } catch (SQLException | IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }, 0, 1000);
    }

    private void initializeWindowOptions(Player player) throws IOException {
        var i = 1;

        try {
            var players = this.playerRepository.getAllGamePlayers(this.game);
            this.game.addPlayers(players);
            player = this.game.getPlayerByName(player.getAccount().getUsername());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (player.getCardOptions().size() > 0) {
            for (var patternCard : player.getCardOptions()) {
                var controller = new WindowPatternCardController(this.connection, patternCard, this.player);
                var loader = new FXMLLoader(getClass().getResource("/views/game/windowPatternCard.fxml"));

                loader.setController(controller);

                if (i <= 2) {
                    this.rowOne.getChildren().add(loader.load());
                } else {
                    this.rowTwo.getChildren().add(loader.load());
                }

                ++i;
            }
        } else {
            var controller = new WindowPatternCardController(this.connection, player.getPatternCard(), this.player);
            var loader = new FXMLLoader(getClass().getResource("/views/game/windowPatternCard.fxml"));

            loader.setController(controller);
            this.rowOne.getChildren().add(loader.load());
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

    private void initializeChat() throws IOException {
        var loader = new FXMLLoader(getClass().getResource("/views/chat/chatBox.fxml"));
        loader.setController(new ChatController(this.connection, this.player, this.game));
        this.rowOne.getChildren().add(loader.load());
    }

    public Game getGame() {
        return this.game;
    }

    public Player getPlayer() {
        return this.player;
    }
}
