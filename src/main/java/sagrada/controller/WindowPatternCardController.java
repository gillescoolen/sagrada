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
    private final GameController gameController;

    private final List<Button> windowSquares = new ArrayList<>();

    public WindowPatternCardController(DatabaseConnection connection, PatternCard patternCard, Player player, GameController gameController) {
        this.windowField = patternCard;
        this.connection = connection;
        this.player = player;
        this.gameController = gameController;
    }

    public WindowPatternCardController(DatabaseConnection connection, Player player, GameController gameController) {
        var timer = new Timer();
        this.connection = connection;

        PlayerFrameRepository playerFrameRepository = new PlayerFrameRepository(connection);

        this.patternCard = player.getPatternCard();
        this.playerFrame = player.getPlayerFrame();
        this.windowField = this.playerFrame;
        this.player = player;
        this.gameController = gameController;

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    playerFrameRepository.getPlayerFrame(player);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1500);

        this.playerFrame.observe(this);
    }

    @Override
    public void accept(PatternCard patternCard) {
        this.playerFrame = patternCard;

        Platform.runLater(() -> {
            if (this.windowSquares.size() > 0) {
                this.fillWindow();
            }

            if (this.changeView != null) {
                this.changeView.setDisable(false);
                this.setPatternCardInformation();
            this.reportMisplacement.setText("Verander veld");
            }
        });
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
            this.reportMisplacement.setText("Verander veld");
        }

        if (this.player.getAccount().getUsername().equals(this.gameController.getPlayer().getAccount().getUsername())) {
            this.window.getStyleClass().clear();
            this.window.getStyleClass().add("window-own");
        }

        this.changeView.setOnAction((e) -> this.changeView());
        this.changeView.setText("Draai");
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
        var selectedDie = this.gameController.getSelectedDie();

        boolean isOwnCard = this.player.getAccount().getUsername().equals(this.gameController.getPlayer().getAccount().getUsername());
        boolean canBeClicked = (isOwnCard && player.isCurrentPlayer());

        for (var square : this.windowField.getSquares()) {
            var button = this.windowSquares.get(i);
            var color = square.getColor();

            button.setText(square.getValue().toString());


            button.setDisable(this.playerFrame == null || !canBeClicked);

            if (selectedDie != null) button.setOnMouseClicked(c -> this.placeDie(square, selectedDie));

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
        String text = this.showPatternCard ? "Patroon kaart" : "Speler frame";
        this.name.setText(this.player.getAccount().getUsername() + "'s " + text);
    }

    private void placeDie(Square square, Die die) {
        this.playerFrame.placeDie(this.player, square, die, this.connection);
        this.gameController.setSelectedDie(null);
        this.gameController.getGame().removeDieFromDraftpool(die);
    }
}
