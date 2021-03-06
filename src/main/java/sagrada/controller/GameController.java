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
import sagrada.component.PostGameScreen;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.*;
import sagrada.model.*;
import sagrada.model.card.activators.ToolCardActivatorFactory;
import sagrada.util.EndGame;
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
    private VBox rowOne, rowTwo, chatWrapper, mainBox;
    @FXML
    private HBox publicObjectiveCardBox, privateObjectiveCardBox, toolCardBox, diceBox, mainGamePage;
    @FXML
    private Button btnSkipTurn, btnRollDice;
    @FXML
    private Text currentTokenAmount;
    @FXML
    private Text currentPlayerIndicator;

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
    private boolean placedDie = false;
    private boolean usedToolCard = false;

    private ScheduledExecutorService ses;
    private ScheduledFuture<?> dieSchedule;
    private ScheduledFuture<?> mainGameSchedule;
    private ScheduledFuture<?> roundTrackSchedule;
    private ScheduledFuture<?> gameFinishedSchedule;

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
                int round;

                try {
                    round = this.gameRepository.getCurrentRound(game.getId());
                    this.dieRepository.placeOnRoundTrack(unusedDice, game.getId(), round);
                    for (var die : unusedDice) {
                        game.removeDieFromDraftPool(die);
                    }

                    if (round >= 10) {
                        this.stopAllTimers();

                        var endGame = new EndGame(this.game, this.connection);
                        endGame.calculatePoints();

                        this.game.getPlayers().forEach(player -> player.setPlayStatus(PlayStatus.DONE_PLAYING));
                        this.playerRepository.setAllFinished(game.getPlayers());

                        var stage = ((Stage) btnRollDice.getScene().getWindow());
                        var scene = new Scene(new PostGameScreen(this.game, this, this.connection).load());
                        stage.setScene(scene);
                    }
                } catch (IOException | SQLException ex) {
                    ex.printStackTrace();
                }
            }

            placedDie = false;
            this.usedToolCard = false;

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

    Runnable mainGame = () -> {
        try {
            if (!gameReady) {
                return;
            }
            player.setCurrentPlayer(player.getCurrent(playerRepository));

            for (var toolCard : this.game.getToolCards()) {
                toolCard.setCanUse(player.isCurrentPlayer());

                if (toolCard.isUsed(this.game)) {
                    toolCard.setCost(2);
                }
            }

            Platform.runLater(() -> {
                if (player != null && player.isCurrentPlayer() && !player.hasInvalidFrameField()) {
                    btnSkipTurn.setDisable(false);

                    if (game.getDraftPool().getDice().isEmpty()) {
                        btnRollDice.setDisable(false);
                        btnSkipTurn.setDisable(true);
                    }
                } else {
                    btnSkipTurn.setDisable(true);
                    btnRollDice.setDisable(true);
                }

                if (placedDie || usedToolCard) {
                    btnSkipTurn.setText("Beurt beeindigen");
                } else {
                    btnSkipTurn.setText("Beurt overslaan");
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
        setCurrentPlayerIndicator();

        try {
            this.game.setRoundTrack(this.roundTrackRepository.getRoundTrack(game.getId()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    };

    Runnable gameFinished = () -> {
        try {
            boolean finished = this.playerRepository.checkForFinished(this.player.getId());

            if (finished) {
                this.stopAllTimers();
                Platform.runLater(() -> {
                    var stage = ((Stage) this.mainGamePage.getScene().getWindow());
                    Scene scene = null;
                    try {
                        scene = new Scene(new PostGameScreen(this.game, this, this.connection).load());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    stage.setScene(scene);
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    };

    /**
     * This is the main event loop for a game.
     */
    private void startMainGameTimer() {
        this.ses = Executors.newScheduledThreadPool(4);

        this.dieSchedule = this.ses.scheduleAtFixedRate(dieStuff, 0, 1, TimeUnit.SECONDS);
        this.mainGameSchedule = this.ses.scheduleAtFixedRate(mainGame, 0, 1, TimeUnit.SECONDS);
        this.roundTrackSchedule = this.ses.scheduleAtFixedRate(roundTrack, 0, 1, TimeUnit.SECONDS);
        this.gameFinishedSchedule = this.ses.scheduleAtFixedRate(gameFinished, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Stop all threads of the game.
     */
    public void stopAllTimers() {
        this.dieSchedule.cancel(true);
        this.mainGameSchedule.cancel(true);
        this.roundTrackSchedule.cancel(true);
        this.gameFinishedSchedule.cancel(false);
        this.ses.shutdown();
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
                                var controller = new WindowPatternCardController(connection, player, gameController, false);
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

            // Show available options when our player hasn't chosen a card yet.
            if (!this.playerRepository.isPatternCardChosen(game)) {
                for (var patternCard : player.getCardOptions()) {
                    var controller = new WindowPatternCardController(this.connection, patternCard, this.player, this);
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
        } catch (SQLException e) {
            e.printStackTrace();
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
            loader.setController(new ToolCardController(this, this.favorTokenRepository, toolCard, ToolCardActivatorFactory.getToolCardActivator(this, toolCard)));
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

    private void setCurrentPlayerIndicator() {
        try {
            var username = this.playerRepository.getCurrentPlayer(this.game);
            var message = (username == null) ? "" : String.format("%s is aan de beurt.", username);
            this.currentPlayerIndicator.setText(message);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Game getGame() {
        return this.game;
    }

    public Player getPlayer() {
        return this.game.getPlayerByName(this.player.getAccount().getUsername());
    }

    public void setSelectedDie(Die selectedDie) {
        this.game.setSelectedDie(selectedDie);
    }

    public Die getSelectedDie() {
        return this.game.getSelectedDie();
    }

    public boolean isPlacedDie() {
        return placedDie;
    }

    public void setPlacedDie(boolean placedDie) {
        this.placedDie = placedDie;
    }

    public void setUsedToolCard(boolean usedToolCard) {
        this.usedToolCard = usedToolCard;
    }

    public boolean isToolCardUsed() {
        return this.usedToolCard;
    }
}
