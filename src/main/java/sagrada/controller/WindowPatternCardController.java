package sagrada.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.PlayerFrameRepository;
import sagrada.database.repositories.PlayerRepository;
import sagrada.model.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class WindowPatternCardController implements Consumer<PatternCard> {
    @FXML
    private VBox window;
    @FXML
    private Button changeView;
    @FXML
    private Button reportMisplacement;
    @FXML
    private Text name;

    private Player player;
    private PatternCard windowField;
    private PatternCard patternCard;
    private PatternCard playerFrame;
    private DatabaseConnection connection;
    private boolean showPatternCard = false;
    private boolean isEndOfGame = false;

    private final List<Button> windowSquares = new ArrayList<>();
    private final GameController gameController;

    private ScheduledExecutorService ses;
    private ScheduledFuture<?> playerFrameSchedule;
    private ScheduledFuture<?> finishedSchedule;

    public WindowPatternCardController(DatabaseConnection connection, PatternCard patternCard, Player player, GameController gameController) {
        this.windowField = patternCard;
        this.connection = connection;
        this.player = player;
        this.gameController = gameController;
    }

    public WindowPatternCardController(DatabaseConnection connection, Player player, GameController gameController) {
        this.connection = connection;

        PlayerFrameRepository playerFrameRepository = new PlayerFrameRepository(connection);
        PlayerRepository playerRepository = new PlayerRepository(connection);

        Runnable playerFrameTimer = () -> {
            try {
                playerFrameRepository.getPlayerFrame(player);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };

        Runnable finishedTimer = () -> {
            try {
                boolean finished = playerRepository.checkForFinished(this.player.getId());
                if (finished) {
                    this.playerFrameSchedule.cancel(true);
                    this.finishedSchedule.cancel(false);
                    this.ses.shutdown();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };

        this.ses = Executors.newScheduledThreadPool(2);
        this.playerFrameSchedule = this.ses.scheduleAtFixedRate(playerFrameTimer, 0, 750, TimeUnit.MILLISECONDS);
        this.finishedSchedule = this.ses.scheduleAtFixedRate(finishedTimer, 0, 1, TimeUnit.SECONDS);

        this.patternCard = player.getPatternCard();
        this.playerFrame = player.getPlayerFrame();
        this.windowField = this.playerFrame;
        this.player = player;
        this.gameController = gameController;

        this.playerFrame.observe(this);
    }

    public WindowPatternCardController(Player player, GameController gameController) {
        this.patternCard = player.getPatternCard();
        this.playerFrame = player.getPlayerFrame();
        this.windowField = this.playerFrame;
        this.player = player;
        this.isEndOfGame = true;
        this.gameController = gameController;
    }

    @Override
    public void accept(PatternCard patternCard) {
        this.playerFrame = patternCard;

        if (!this.isEndOfGame) {
            try {
                this.player.checkIfCardIsValid(new PlayerFrameRepository(connection));
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Platform.runLater(() -> {
                if (this.windowSquares.size() > 0) {
                    this.fillWindow();
                }

                if (this.changeView != null) {
                    this.changeView.setDisable(false);
                    this.setPatternCardInformation();

                    if (this.gameController != null && this.gameController.getPlayer() != null) {
                        boolean isOwnCard = this.player.getAccount().getUsername().equals(this.gameController.getPlayer().getAccount().getUsername());

                        if (!isOwnCard) {
                            this.reportMisplacement.setText("Ongeldig verklaren");
                            this.reportMisplacement.setOnMouseClicked(e -> this.invalidateCard());

                            this.reportMisplacement.setDisable(this.player.hasInvalidFrameField());
                        } else {
                            if (this.player.hasInvalidFrameField()) {
                                this.setDiceRemovable();

                                this.reportMisplacement.setText("Valide verklaren");
                                this.reportMisplacement.setOnMouseClicked(e -> this.setBoardValid());
                            }

                            this.reportMisplacement.setVisible(this.player.hasInvalidFrameField());
                        }
                    }
                }
            });
        }
    }

    private void setDiceRemovable() {
        int i = 0;

        for (var square : this.windowField.getSquares()) {
            var button = this.windowSquares.get(i);
            button.setOnMouseClicked(c -> this.removeDie(square));
            ++i;
        }
    }

    private void removeDie(Square square) {
        this.playerFrame.removeDie(this.player, square, this.connection);
    }

    private void setBoardValid() {
        try {
            this.player.setCardAsValid(new PlayerFrameRepository(connection));
            this.reportMisplacement.setVisible(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void initialize() {
        if (this.playerFrame == null) {
            this.changeView.setDisable(true);
            this.name.setText(this.windowField.getName() + String.format(" (%s tokens)", this.windowField.getDifficulty()));
            this.reportMisplacement.setText("Kies");
            this.reportMisplacement.setOnMouseClicked(e -> this.choosePatternCard());
        } else {
            this.changeView.setDisable(false);
            this.name.setText(this.player.getAccount().getUsername() + String.format(" (%s tokens)", this.playerFrame.getDifficulty()));
            this.reportMisplacement.setText("Ongeldig verklaren");
            this.reportMisplacement.setOnMouseClicked(e -> this.invalidateCard());
        }

        if (!this.isEndOfGame) {
            if (this.player.getAccount().getUsername().equals(this.gameController.getPlayer().getAccount().getUsername())) {
                this.window.getStyleClass().clear();
                this.window.getStyleClass().add("window-own");
            }
        }


        if (this.isEndOfGame) {
            this.reportMisplacement.setDisable(true);
            this.reportMisplacement.setVisible(false);
        }

        this.changeView.setOnAction((e) -> this.changeView());
        this.changeView.setText("Draai");
        this.initializeWindow();
        this.fillWindow();
    }

    private void invalidateCard() {
        this.reportMisplacement.setDisable(true);

        try {
            this.player.invalidateField(new PlayerFrameRepository(this.connection));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void choosePatternCard() {
        PlayerRepository playerRepository = new PlayerRepository(this.connection);
        this.player.setPatternCard(this.windowField);

        try {
            playerRepository.bindPatternCardToPlayer(this.player);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillWindow() {
        var i = 0;
        var selectedDie = this.gameController.getSelectedDie();

        boolean isOwnCard = this.player.getAccount().getUsername().equals(this.gameController.getPlayer().getAccount().getUsername());
        boolean canBeClicked = (isOwnCard && player.isCurrentPlayer());

        for (var square : this.windowField.getSquares()) {
            var button = this.windowSquares.get(i);
            var color = square.getColor();

            Integer value = square.getValue();

            if (value == 0) {
                button.setText("");
            } else {
                button.setText(value.toString());
            }

            if (color == null || color == sagrada.model.Color.YELLOW) {
                button.setTextFill(javafx.scene.paint.Color.BLACK);
            } else {
                button.setTextFill(javafx.scene.paint.Color.WHITE);
            }

            button.setDisable(this.playerFrame == null || this.showPatternCard);

            if (!this.showPatternCard || !isOwnCard) button.setOnMouseClicked(c -> this.placeDie(square, selectedDie));

            if (this.playerFrame == null || !canBeClicked || this.isEndOfGame) {
                button.setDisable(true);
            } else {
                var emptyCount = this.player.getPlayerFrame().countEmptySquares();
                button.setDisable(emptyCount == 20 && (square.getPosition().getX() != 1 && square.getPosition().getX() != 5 && square.getPosition().getY() != 1 && square.getPosition().getY() != 4));
            }

            if (color != null) {
                button.setStyle("-fx-background-color: " + square.getColor().getColor());
            } else {
                button.setStyle("");
            }

            ++i;
        }
    }

    private void initializeWindow() {
        var i = 0;

        for (Node rowNode : this.window.getChildren()) {
            if (i > 0) {
                var row = ((HBox) rowNode);

                for (Node buttonNode : row.getChildren()) {
                    var button = ((Button) buttonNode);
                    this.windowSquares.add(button);
                }
            }

            ++i;
        }
    }

    private void changeView() {
        this.showPatternCard = !this.showPatternCard;
        this.windowField = this.showPatternCard ? this.patternCard : this.playerFrame;
        this.setPatternCardInformation();
        this.fillWindow();
    }

    private void setPatternCardInformation() {
        String text = this.showPatternCard ? "patroon kaart" : "speler frame";
        this.name.setText(this.player.getAccount().getUsername() + "'s " + text);
    }

    private void placeDie(Square square, Die die) {
        if (die == null || square.getDie() != null) return;
        this.playerFrame.placeDie(this.player, square, die, this.connection);
        this.gameController.setSelectedDie(null);
        this.gameController.getGame().removeDieFromDraftPool(die);
        this.gameController.setPlacedDie(true);
    }
}
