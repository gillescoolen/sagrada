package sagrada.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.GameRepository;
import sagrada.model.Account;
import sagrada.model.Game;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LobbyController {
    @FXML
    private VBox vbLobbyItems;

    private final Account user;

    public LobbyController(Account account) {
        this.user = account;
    }

    @FXML
    protected void initialize() {
        var getGamesTimer = new Timer();

        getGamesTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> getGames());
            }
        }, 0, 3000);
    }

    private void getGames() {
        try {
            var connection = new DatabaseConnection();
            connection.connect();
            var gameRepository = new GameRepository(connection);
            this.fillLobbyList(gameRepository.getAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillLobbyList(List<Game> games) {
        this.vbLobbyItems.getChildren().clear();

        try {
            for (var game : games) {
                if (game.getOwner() != null) {
                    var loader = new FXMLLoader(this.getClass().getResource("/views/lobby/lobbyItem.fxml"));
                    loader.setController(new LobbyItemController(game));
                    this.vbLobbyItems.getChildren().add(loader.load());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
