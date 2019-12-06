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
import sagrada.model.Game;
import sagrada.model.PatternCard;
import sagrada.model.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
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
    private boolean showPatternCard = false;
    private DatabaseConnection connection;

    private final List<Button> windowSquares = new ArrayList<>();

    public WindowPatternCardController(DatabaseConnection connection, PatternCard patternCard, Player player) {
        this.windowField = patternCard;
        this.connection = connection;
        this.player = player;
    }

    public WindowPatternCardController(DatabaseConnection connection, Player player, Game game) {
        var timer = new Timer();
        this.connection = connection;

        PlayerFrameRepository playerFrameRepository = new PlayerFrameRepository(connection);

        try {
            this.patternCard = player.getPatternCard();
            this.playerFrame = playerFrameRepository.getPlayerFrame(game, player);
            this.windowField = this.playerFrame;
            this.player = player;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    try {
                        playerFrameRepository.getPlayerFrame(game, player, windowField);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }
        }, 0, 2000);
    }

    @Override
    public void accept(PatternCard patternCard) {
        this.playerFrame = patternCard;
        this.fillWindow();
    }

    @FXML
    protected void initialize() {
        if (this.playerFrame == null) {
            this.changeView.setDisable(true);
            this.name.setText(this.windowField.getName());
            this.reportMisplacement.setText("Choose");

            this.reportMisplacement.setOnMouseClicked(e -> this.choosePatternCard());
        } else {
            this.changeView.setDisable(false);
            this.name.setText(this.player.getAccount().getUsername());
            this.reportMisplacement.setText("Change field");
        }

        this.changeView.setOnAction((e) -> this.changeView());
        this.changeView.setText("Switch");
        this.initializeWindow();
        this.fillWindow();
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

        for (var square : this.windowField.getSquares()) {
            var button = this.windowSquares.get(i);
            var color = square.getColor();

            button.setText(square.getValue().toString());
            button.setDisable(this.playerFrame == null);

            if (color != null) {
                button.setStyle("-fx-background-color: " + square.getColor().getColor());
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
    }
}
