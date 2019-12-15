package sagrada.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.*;
import sagrada.model.*;
import sagrada.model.card.activators.ToolCardActivatorFactory;
import sagrada.util.StartGame;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class GameController implements Consumer<Game> {
    @FXML
    private HBox mainGamePage;
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
    @FXML
    private VBox mainBox;

    private Game game;
    private StartGame startGameUtil;
    private Player player;
    private DatabaseConnection connection;
    private PlayerRepository playerRepository;
    private GameRepository gameRepository;
    private final DieRepository dieRepository;
    private final FavorTokenRepository favorTokenRepository;
    private RoundTrackRepository roundTrackRepository;

    private boolean gameReady = false;
    private Die selectedDie;
    private boolean placedDie = false;

    public GameController(DatabaseConnection connection, Game game, Account account) {
        game.observe(this);

        this.connection = connection;
        this.playerRepository = new PlayerRepository(connection);
        this.gameRepository = new GameRepository(connection);
        this.dieRepository = new DieRepository(connection);
        this.favorTokenRepository = new FavorTokenRepository(connection);
        this.roundTrackRepository = new RoundTrackRepository(connection);

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

    private void disableAllButtons() {
        // TODO: add more things to disable.
        this.btnSkipTurn.setDisable(true);
        this.btnRollDice.setDisable(true);
    }

    @FXML
    protected void initialize() {
        this.btnSkipTurn.setOnMouseClicked(e -> {
            this.disableAllButtons();

            if (player.getSequenceNumber() == this.game.getPlayers().size() * 2) {
                var unusedDice = this.game.getDraftPool().getDice();
                var thisGameController = this;
                final Task<Void> roundTrackTask = new Task<Void>() {
                    @Override
                    protected Void call() {
                        try {
                            var round = gameRepository.getCurrentRound(game.getId());
                            dieRepository.placeOnRoundTrack(unusedDice, game.getId(), round);
                            for (var die : unusedDice) {
                                game.removeDieFromDraftPool(die);
                            }

                            if (round >= 2) {
                                System.out.println(":D");
                                game.getPlayers().forEach(player -> player.setPlayStatus(PlayStatus.DONE_PLAYING));
                                playerRepository.setAllFinished(game.getPlayers());

                                var loader = new FXMLLoader(getClass().getResource("/views/postGame.fxml"));
                                loader.setController(new PostGameController(game, thisGameController));
                                var stage = ((Stage) mainGamePage.getScene().getWindow());
                                var scene = new Scene(loader.load());
                                stage.setScene(scene);
                            }
                        } catch (SQLException | IOException ex) {
                            ex.printStackTrace();
                        }
                        return null;
                    }
                };
                new Thread(roundTrackTask).start();
            }

            placedDie = false;
            final Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    try {
                        player.skipTurn(playerRepository, game);
                        player.setCurrentPlayer(false);
                        btnSkipTurn.setDisable(true);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    return null;
                }
            };
            new Thread(task).start();
        });

        btnRollDice.setOnMouseClicked(e -> {
            btnRollDice.setDisable(true);

            try {
                var dice = this.player.grabFromDiceBag(this.game.getDiceCount());

                this.game.addDiceInDraftPool(dice);
                this.game.throwDice();

                var round = this.gameRepository.getNextRound(this.game.getId());
                this.dieRepository.addGameDice(this.game.getId(), round, dice);
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
                    this.initializeRoundTrack();
                    this.initializeDraftPool();
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

    Runnable dieStuff = () -> {
        if (!gameReady) {
            return;
        }

        try {
            initializeDieStuffAndFavorTokens(game.getPlayers());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    };

    Runnable other = () -> {
        try {
            if (!gameReady) {
                return;
            }
            player.setCurrentPlayer(player.getCurrent(playerRepository));

            Platform.runLater(() -> {
                if (player != null && player.isCurrentPlayer()) {
                    btnSkipTurn.setDisable(false);

                    if (game.getDraftPool().getDice().isEmpty()) {
                        btnRollDice.setDisable(false);
                        btnSkipTurn.setDisable(true);
                    }
                } else {
                    btnSkipTurn.setDisable(true);
                    btnRollDice.setDisable(true);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    };

    Runnable roundTrack = () -> {
        if (!gameReady) {
            return;
        }

        setCurrentTokenAmount();

        try {
            this.game.setRoundTrack(this.roundTrackRepository.getRoundTrack(game.getId()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    };

    /**
     * This is the main event loop for a game.
     */
    private void startMainGameTimer() {
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(4);

        ScheduledFuture<?> scheduledFuture = ses.scheduleAtFixedRate(dieStuff, 0, 1, TimeUnit.SECONDS);
        ScheduledFuture<?> scheduledFuture1 = ses.scheduleAtFixedRate(other, 0, 1, TimeUnit.SECONDS);
        ScheduledFuture<?> scheduledFuture3 = ses.scheduleAtFixedRate(roundTrack, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Check for player chosen pattern cards.
     * When every player has chosen a card, clear the current cards and only show our player card.
     */
    private void checkForPlayerPatternCards() {
        var playerPatternCardsTimer = new Timer();
        GameController gameController = this;

        playerPatternCardsTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    // Check if every player has chosen a pattern card.
                    var everyoneHasChosen = playerRepository.isPatternCardChosen(game);

                    disableAllButtons();

                    if (!everyoneHasChosen) {
                        return;
                    }

                    var players = playerRepository.getAllGamePlayers(game);
                    game.addPlayers(players);

                    initializeDieStuffAndFavorTokens(game.getPlayers());

                    // Filter our player from the participating players.
                    player = players.stream()
                            .filter(p -> p.getAccount().getUsername().equals(player.getAccount().getUsername()))
                            .findFirst()
                            .orElse(null);

                    if (player == null) {
                        return;
                    }

                    if (game.getOwner().getAccount().getUsername().equals(player.getAccount().getUsername()) && startGameUtil != null && !gameReady) {
                        startGameUtil.assignFavorTokens();
                        game = startGameUtil.getCreatedGame();
                    }

                    Platform.runLater(() -> {
                        try {
                            // If the currentPlayer is our actual player, clear the cards.
                            rowOne.getChildren().clear();
                            rowTwo.getChildren().clear();

                            for (var player : players) {
                                var controller = new WindowPatternCardController(connection, player, gameController);
                                var loader = new FXMLLoader(getClass().getResource("/views/game/windowPatternCard.fxml"));

                                loader.setController(controller);

                                if (rowOne.getChildren().size() < 2) {
                                    rowOne.getChildren().add(loader.load());
                                    rowOne.setVisible(true);
                                } else if (rowTwo.getChildren().size() < 2) {
                                    rowTwo.getChildren().add(loader.load());
                                    rowTwo.setVisible(true);
                                }
                            }

                            gameReady = true;
                            playerPatternCardsTimer.cancel();
                            playerPatternCardsTimer.purge();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 2000);
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
                var controller = new WindowPatternCardController(this.connection, patternCard, this.player, this);
                var loader = new FXMLLoader(getClass().getResource("/views/game/windowPatternCard.fxml"));

                loader.setController(controller);

                if (i <= 2) {
                    this.rowOne.getChildren().add(loader.load());
                    this.rowOne.setVisible(true);
                } else {
                    this.rowTwo.getChildren().add(loader.load());
                    this.rowTwo.setVisible(true);
                }

                ++i;
            }
        } else {
            // Load our clients player pattern card when rejoining a game.
            var controller = new WindowPatternCardController(this.connection, player.getPatternCard(), this.player, this);
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
        var dice = this.dieRepository.getUnusedDice(this.game.getId());

        var diceBag = new DiceBag(dice);

        for (var player : players) {
            player.setDiceBag(diceBag);
            player.addFavorTokens(this.favorTokenRepository.getPlayerFavorTokens(this.game.getId(), player.getId()));
        }
    }

    private void initializeDraftPool() throws IOException {
        var loader = new FXMLLoader(getClass().getResource("/views/game/draftPool.fxml"));
        loader.setController(new DraftPoolController(this.game.getDraftPool(), this.game, this, this.connection));
        this.mainBox.getChildren().add(1, loader.load());
    }

    private void initializeRoundTrack() throws IOException {
        var loader = new FXMLLoader(getClass().getResource("/views/game/roundTrack.fxml"));
        loader.setController(new RoundTrackController(this.game.getRoundTrack()));
        this.mainBox.getChildren().add(0, loader.load());
    }

    private void initializeChat() throws IOException {
        var loader = new FXMLLoader(getClass().getResource("/views/chat/chatBox.fxml"));
        loader.setController(new ChatController(this.connection, this.player, this.game));
        this.chatWrapper.getChildren().add(loader.load());
    }

    private void setCurrentTokenAmount() {
        String message = "U heeft %s tokens.";

        if (this.player == null || this.player.getFavorTokens() == null) {
            message = String.format(message, 0);
        } else {
            int unusedFavorTokens = (int) this.player.getFavorTokens().stream().filter(token -> token.getToolCard() == null).count();
            message = String.format(message, unusedFavorTokens);
        }

        this.currentTokenAmount.setText(message);
    }

    public Game getGame() {
        return this.game;
    }

    public Player getPlayer() {
        return this.game.getPlayerByName(this.player.getAccount().getUsername());
    }

    public void setSelectedDie(Die selectedDie) {
        this.selectedDie = selectedDie;
    }

    public Die getSelectedDie() {
        return this.selectedDie;
    }

    public boolean isPlacedDie() {
        return placedDie;
    }

    public void setPlacedDie(boolean placedDie) {
        this.placedDie = placedDie;
    }
}
