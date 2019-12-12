package sagrada.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sagrada.component.BackButton;
import sagrada.component.GameScreen;
import sagrada.component.LobbyScreen;
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
    private final static int TASK_DELAY = 2000;
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
                boolean started = checkForGameStarted();
                Platform.runLater(() -> {
                    if (started) {
                        checkGameStartedTimer.cancel();
                        checkGameStartedTimer.purge();
                        goToGame();
                    }
                });
            }
        }, TASK_DELAY, POLL_TIME);
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
            this.checkGameStartedTimer.cancel();
            this.checkGameStartedTimer.purge();

            var stage = ((Stage) this.vbPanel.getScene().getWindow());
            var scene = new Scene(new LobbyScreen(this.databaseConnection, this.account).load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goToGame() {
        try {
            var stage = ((Stage) this.panel.getScene().getWindow());
            var scene = new Scene(new GameScreen(this.databaseConnection, this.game, this.account).load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
