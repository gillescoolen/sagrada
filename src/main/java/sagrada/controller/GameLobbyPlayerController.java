package sagrada.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.GameRepository;
import sagrada.model.Game;

import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class GameLobbyPlayerController {
    private final DatabaseConnection databaseConnection;
    private final Game game;
    private final static int POLL_TIME = 3000;
    private final Timer checkGameStartedTimer = new Timer();

    public GameLobbyPlayerController(DatabaseConnection databaseConnection, Game game) {
        this.databaseConnection = databaseConnection;
        this.game = game;
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
        // TODO: Go to game screen
    }
}
