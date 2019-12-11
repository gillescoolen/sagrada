package sagrada.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.*;
import sagrada.model.*;
import sagrada.model.card.activators.ToolCardActivatorFactory;
import sagrada.util.StartGame;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

public class GameController implements Consumer<Game> {
    @FXML
    private VBox rowOne;
    @FXML
    private VBox rowTwo;
    @FXML
    private HBox diceBox;
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
    @FXML
    private Button btnRollDice;
    @FXML
    private Text currentTokenAmount;

    private Game game;
    private StartGame startGameUtil;
    private Player player;
    private final DatabaseConnection connection;
    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final DieRepository dieRepository;
    private final FavorTokenRepository favorTokenRepository;

    private boolean gameReady = false;

    public GameController(DatabaseConnection connection, Game game, Account account) {
        game.observe(this);

        this.connection = connection;
        this.playerRepository = new PlayerRepository(connection);
        this.gameRepository = new GameRepository(connection);
        this.dieRepository = new DieRepository(connection);
        this.favorTokenRepository = new FavorTokenRepository(connection);

        var publicObjectiveCardRepository = new PublicObjectiveCardRepository(connection);
        var toolCardRepository = new ToolCardRepository(connection);

        try {
            if (game.getOwner().getAccount().getUsername().equals(account.getUsername()) && !this.gameRepository.checkIfGameHasStarted(game)) {
                this.startGameUtil = new StartGame(game, connection);
            } else {
                this.game.addObjectiveCard(publicObjectiveCardRepository.getAllByGameId(this.game.getId()));
                this.game.addToolCard(toolCardRepository.getAllByGameId(this.game.getId()));

                this.initializeDieStuffAndFavorTokens(this.game.getPlayers());

                this.game.addFavorTokens(this.favorTokenRepository.getFavorTokens(this.game.getId()));
            }

            this.game.setPlayerTurn(this.playerRepository.getNextGamePlayer(this.game));
            this.player = this.game.getPlayers().stream().filter(p -> p.getAccount().getUsername().equals(account.getUsername())).findFirst().orElse(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void accept(Game game) {
        this.game = game;
    }

    @FXML
    protected void initialize() {
        this.btnSkipTurn.setOnMouseClicked(e -> {
            try {
                this.player.skipTurn(this.playerRepository, this.game);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        btnRollDice.setOnMouseClicked(e -> {
            btnRollDice.setDisable(true);

            try {
                var draftPool = this.game.getDraftPool();
                draftPool.removeAllDice();

                var dice = this.player.grabFromDiceBag(this.game.getDiceCount());

                draftPool.addAllDice(dice);
                draftPool.throwDice();

                var round = this.gameRepository.getCurrentRound(this.game.getId());
                this.dieRepository.addGameDice(this.game.getId(), round, draftPool.getDice());

                this.initializeDice();
            } catch (Exception ex) {
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
                    this.initializeDice();

                    this.checkForPlayerPatternCards();
                    this.startMainGameTimer();
                    this.setCurrentTokenAmount();
                    this.initializeChat();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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

                    var playerFrameRepository = new PlayerFrameRepository(connection);

                    try {
                        for (var player : game.getPlayers()) {
                            playerFrameRepository.getPlayerFrame(player);
                        }

                        if (player != null && player.isCurrentPlayer()) {
//                            btnSkipTurn.setDisable(false);
                            btnSkipTurn.setStyle("-fx-background-color: green");

                            if (game.getDraftPool().getDice().isEmpty()) {
                                btnRollDice.setDisable(false);
                            }
                        } else {
//                            btnSkipTurn.setDisable(true);
                            btnSkipTurn.setStyle("-fx-background-color: red");
                            btnRollDice.setDisable(true);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }
        }, 0, 1000);
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
                        game.addPlayers(players);

                        if (game.getOwner().getAccount().getUsername().equals(player.getAccount().getUsername()) && startGameUtil != null && !gameReady) {
                            startGameUtil.shareFavorTokens();
                        }

                        initializeDieStuffAndFavorTokens(game.getPlayers());

                        // Filter our player from the participating players.
                        player = players.stream()
                                .filter(p -> p.getAccount().getUsername().equals(player.getAccount().getUsername()))
                                .findFirst()
                                .orElse(null);

                        if (player == null) {
                            return;
                        }

                        // If the currentPlayer is our actual player, clear the cards.
                        rowOne.getChildren().clear();
                        rowTwo.getChildren().clear();

                        for (var player : players) {
                            var controller = new WindowPatternCardController(connection, player);
                            var loader = new FXMLLoader(getClass().getResource("/views/game/windowPatternCard.fxml"));

                            loader.setController(controller);

                            if (rowOne.getChildren().size() < 2) {
                                rowOne.getChildren().add(loader.load());
                            } else if (rowTwo.getChildren().size() < 2) {
                                rowTwo.getChildren().add(loader.load());
                            }
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
            loader.setController(new ToolCardController(toolCard, ToolCardActivatorFactory.getToolCardActivator(this, toolCard)));
            this.toolCardBox.getChildren().add(loader.load());
        }
    }

    private void initializeDieStuffAndFavorTokens(List<Player> players) throws SQLException {
        var draftedDice = this.dieRepository.getDraftPoolDice(this.game.getId(), this.gameRepository.getCurrentRound(this.game.getId()));
        this.game.getDraftPool().addAllDice(draftedDice);

        var dice = this.dieRepository.getUnusedDice(this.game.getId());
        var diceBag = new DiceBag(dice);

        for (var player : players) {
            player.setDiceBag(diceBag);
            player.addFavorTokens(this.favorTokenRepository.getPlayerFavorTokens(this.game.getId(), player.getId()));
        }
    }

    private void initializeDice() throws IOException {
        var diceCount = this.game.getDiceCount();
        var draftedDice = this.game.getDraftPool().getDice();

        this.diceBox.getChildren().clear();
        for (int i = 0; i < diceCount; ++i) {
            var loader = new FXMLLoader(getClass().getResource("/views/game/die.fxml"));
            if (i < draftedDice.size()) {
                var die = draftedDice.get(i);
                loader.setController(new DieController(die));
            }
            this.diceBox.getChildren().add(loader.load());
        }
    }

    private void initializeChat() throws IOException {
        var loader = new FXMLLoader(getClass().getResource("/views/chat/chatBox.fxml"));
        loader.setController(new ChatController(this.connection, this.player, this.game));
        this.chatWrapper.getChildren().add(loader.load());
    }

    private void setCurrentTokenAmount() {
        this.currentTokenAmount.setText(String.format("You have %s tokens.", String.valueOf(this.player.getFavorTokens().size())));
    }

    public Game getGame() {
        return this.game;
    }

    public Player getPlayer() {
        return this.player;
    }
}
