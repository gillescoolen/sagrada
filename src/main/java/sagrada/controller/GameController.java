package sagrada.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.*;
import sagrada.model.*;
import sagrada.util.StartGame;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

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
    @FXML
    private VBox chatWrapper;
    @FXML
    private Button btnSkipTurn;

    private Game game;
    private Player player;
    private final DatabaseConnection connection;
    private final PlayerRepository playerRepository;
    private final DieRepository dieRepository;

    private boolean gameReady = false;
    private TreeMap<Integer, PatternCard> patternCards = new TreeMap<>();

    public GameController(DatabaseConnection connection, Game game, Account account) {
        this.connection = connection;
        var publicObjectiveCardRepository = new PublicObjectiveCardRepository(connection);
        var toolCardRepository = new ToolCardRepository(connection);
        var gameRepository = new GameRepository(this.connection);

        this.playerRepository = new PlayerRepository(connection);
        this.dieRepository = new DieRepository(connection);

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

                var dice = this.dieRepository.getUnusedDice(this.game.getId());
                var diceBag = new DiceBag(dice);
                for (var player : this.game.getPlayers()) {
                    player.setDiceBag(diceBag);
                }
            }

            this.player = this.playerRepository.getGamePlayer(account.getUsername(), game);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void initialize() {
        btnSkipTurn.setOnMouseClicked(e -> {
            try {
                this.player.skipTurn(this.playerRepository, this.game);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        for (var player : this.game.getPlayers()) {
            if (player.getAccount().getUsername().equals(this.player.getAccount().getUsername())) {
                try {
                    this.initializeWindowOptions(player);
                    this.initializePrivateObjectiveCard(this.game.getPlayerByName(player.getAccount().getUsername()));
                    this.initializePublicObjectiveCards();
                    this.initializeToolCards();

                    this.checkForPlayerPatternCards();
                    this.startMainGameTimer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            this.initializeChat();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This is the main event loop for a game.
     */
    private void startMainGameTimer() {
        Timer mainGameTimer = new Timer();

        mainGameTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (!gameReady) {
                        return;
                    }

                    syncPatternCards();

                    try {
                        var player1 = playerRepository.findById(player.getId());

                        if (player1.isCurrentPlayer()) {
                            btnSkipTurn.setDisable(false);
                        } else {
                            btnSkipTurn.setDisable(true);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }
        }, 0, 1000);
    }

    /**
     * Sync the pattern cards from our client with the ones inside the database.
     */
    private void syncPatternCards() {
        var players = this.game.getPlayers();
        var newPatternCards = new TreeMap<Integer, PatternCard>();
        var changed = false;

        // Fetch the pattern cards for every player and check for differences in each one.
        for (Player player : players) {
            // TODO: Check if the result from getPatternCard() is up to date with the entry from the DB.
            // Fetch the card from a player and add it to the new list.
            newPatternCards.put(player.getId(), player.getPatternCard());
        }

        // If Pattern Cards have never been set, set them to our newPatternCards.
        // Otherwise, check for differences between squares.
        if (this.patternCards.size() == 0) {
            this.patternCards = newPatternCards;
        } else {
            for (Map.Entry<Integer, PatternCard> entry : newPatternCards.entrySet()) {
                var currentCard = this.patternCards.get(entry.getKey());

                // Match the entry from our new cards to our existing one and set the squares when they are different.
                if (entry.getValue() != currentCard) {
                    // This also sets the die.
                    currentCard.setSquares(entry.getValue().getSquares());
                    changed = true;
                }
            }
        }

        // Update the existing cards whenever there was a difference.
        if (changed) {
            try {
                this.showPatternCards();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Load the pattern cards for each player.
     * Optimization: Load only the card that changed instead of all cards.
     */
    private void showPatternCards() throws IOException {
        var row = 0;

        // Clear the existing pattern cards.
        this.rowOne.getChildren().clear();
        this.rowTwo.getChildren().clear();

        // TODO: Render our card first or with another style.
        // Loop through patterns cards and render our clients player card first.
        for (Map.Entry<Integer, PatternCard> entry : this.patternCards.entrySet()) {
            var controller = new WindowPatternCardController(this.connection, entry.getValue(), this.player);
            var loader = new FXMLLoader(getClass().getResource("/views/game/windowPatternCard.fxml"));
            loader.setController(controller);

            if (row <= 2) {
                this.rowOne.getChildren().add(loader.load());
            } else {
                this.rowTwo.getChildren().add(loader.load());
            }

            ++row;
        }
    }

    /**
     * Check for player chosen pattern cards.
     * When every player has chosen a card, clear the current cards and only show our player card.
     */
    private void checkForPlayerPatternCards() {
        var playerPatternCardsTimer = new Timer();

        playerPatternCardsTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    try {
                        // Check if every player has chosen a pattern card.
                        var everyoneHasChosen = playerRepository.isPatternCardChosen(game);

                        if (!everyoneHasChosen) {
                            return;
                        }

                        var players = playerRepository.getAllGamePlayers(game);

                        // Filter our player from the participating players.
                        Player currentPlayer = players.stream()
                                .filter(p -> p.getAccount().getUsername().equals(player.getAccount().getUsername()))
                                .findFirst()
                                .orElse(null);

                        if (currentPlayer == null) {
                            return;
                        }

                        // If the currentPlayer is our actual player, clear the cards.
                        rowOne.getChildren().clear();
                        rowTwo.getChildren().clear();

                        for (var player : players) {
                            var controller = new WindowPatternCardController(connection, player);
                            var loader = new FXMLLoader(getClass().getResource("/views/game/windowPatternCard.fxml"));

                            loader.setController(controller);
                            rowOne.getChildren().add(loader.load());
                        }

                        gameReady = true;
                        playerPatternCardsTimer.cancel();
                        playerPatternCardsTimer.purge();
                    } catch (SQLException | IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }, 0, 1000);
    }

    /**
     * Loads the available pattern cards for our player to choose from.
     *
     * @param player
     * @throws IOException
     */
    private void initializeWindowOptions(Player player) throws IOException {
        var i = 1;

        try {
            var players = this.playerRepository.getAllGamePlayers(this.game);
            this.game.addPlayers(players);
            player = this.game.getPlayerByName(player.getAccount().getUsername());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Show available options when our player hasn't chosen a card yet.
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
            // Load our clients player pattern card when rejoining a game.
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
        this.chatWrapper.getChildren().add(loader.load());
    }

    public Game getGame() {
        return this.game;
    }

    public Player getPlayer() {
        return this.player;
    }
}
