package sagrada.controller;

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

public class LobbyController {
    private final Account user;

    @FXML
    private VBox vbLobbyItems;

    public LobbyController(Account account) {
        this.user = account;
    }

    @FXML
    protected void initialize() {
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
        try {
            for (var game : games) {
                var loader = new FXMLLoader(this.getClass().getResource("/views/lobby/lobbyItem.fxml"));
                loader.setController(new LobbyItemController(game));
                this.vbLobbyItems.getChildren().add(loader.load());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
