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
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LobbyController {
    @FXML
    private VBox vbLobbyGames;
    @FXML
    private VBox vbLobbyInvites;

    private final Account user;
    private final DatabaseConnection databaseConnection;

    public LobbyController(DatabaseConnection databaseConnection, Account account) {
        this.databaseConnection = databaseConnection;
        this.user = account;
    }

    @FXML
    protected void initialize() {
        var getGamesTimer = new Timer();
        var getInvitesTimer = new Timer();

        getGamesTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> getGames());
            }
        }, 0, 5000);

        getInvitesTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> getInvites());
            }
        }, 0, 3000);
    }

    private void getGames() {
        try {
            var gameRepository = new GameRepository(this.databaseConnection);
            var loader = this.getClass().getResource("/views/lobby/lobbyGame.fxml");
            this.fillLobbyList(gameRepository.getAll(), this.vbLobbyGames, loader);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getInvites() {
        try {
            var gameRepository = new GameRepository(this.databaseConnection);
            var loader = this.getClass().getResource("/views/lobby/lobbyInvite.fxml");
            this.fillLobbyList(gameRepository.getInvitedGames(this.user), this.vbLobbyInvites, loader);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillLobbyList(List<Game> games, VBox items, URL view) {
        items.getChildren().clear();

        try {
            for (var game : games) {
                if (game.getOwner() != null) {
                    var loader = new FXMLLoader(view);
                    loader.setController(new LobbyItemController(game));
                    items.getChildren().add(loader.load());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
