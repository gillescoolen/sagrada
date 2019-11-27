package sagrada.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.GameRepository;
import sagrada.model.Account;
import sagrada.model.Game;
import sagrada.model.Player;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LobbyController {
    private final Account user;

    @FXML
    private VBox vbLobbyItems;
    @FXML
    private Button btnCreateGame;

    public LobbyController(Account account) {
        this.user = account;
    }

    @FXML
    protected void initialize() {
        this.btnCreateGame.setOnMouseClicked(e -> this.createGame());

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
                loader.setController(new LobbyItemController(game, this.user));
                this.vbLobbyItems.getChildren().add(loader.load());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createGame() {

        Player player = new Player();
        Game game = new Game();

    }
}
