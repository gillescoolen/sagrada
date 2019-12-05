package sagrada.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.GameRepository;
import sagrada.model.Account;
import sagrada.model.Game;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class GameLobbyPlayerController {
    @FXML
    private AnchorPane pane;

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

    private void goToGame() {
        try {
            var loader = new FXMLLoader(getClass().getResource("/views/game.fxml"));
            var stage = ((Stage) this.pane.getScene().getWindow());
            loader.setController(new GameController(this.databaseConnection, this.game, this.account));
            var scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
