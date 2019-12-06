package sagrada.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sagrada.component.BackButton;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.GameRepository;
import sagrada.model.Account;
import sagrada.model.Game;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class GameLobbyPlayerController {
    @FXML
    private VBox vbPanel;
    @FXML
    private AnchorPane panel;
    private final DatabaseConnection databaseConnection;
    private final Game game;
    private final Account account;
    private final static int POLL_TIME = 3000;
    private final Timer checkGameStartedTimer = new Timer();

    public GameLobbyPlayerController(DatabaseConnection databaseConnection, Game game, Account account) {
        this.databaseConnection = databaseConnection;
        this.game = game;
        this.account = account;
    }

    @FXML
    protected void initialize() {
        this.addBackButton();

        this.checkGameStartedTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (checkForGameStarted()) {
                        checkGameStartedTimer.cancel();
                        checkGameStartedTimer.purge();
                        goToGame();
                    }
                });
            }
        }, 0, POLL_TIME);
    }

    private boolean checkForGameStarted() {
        boolean started = false;
        try {
            GameRepository gameRepository = new GameRepository(this.databaseConnection);
            started = gameRepository.checkIfGameHasStarted(this.game);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return started;
    }

    private void addBackButton() {
        try {
            this.vbPanel.getChildren().add(0, new BackButton(this::backToLobbyScreen).load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void backToLobbyScreen() {
        try {
            var loader = new FXMLLoader(getClass().getResource("/views/lobby/lobby.fxml"));
            var stage = ((Stage) this.vbPanel.getScene().getWindow());
            loader.setController(new LobbyController(this.databaseConnection, this.account));
            var scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goToGame() {
        try {
            var loader = new FXMLLoader(getClass().getResource("/views/game.fxml"));
            var stage = ((Stage) this.panel.getScene().getWindow());
            loader.setController(new GameController(this.databaseConnection, this.game, this.account));
            var scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
