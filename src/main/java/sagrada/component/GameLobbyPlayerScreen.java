package sagrada.component;

import javafx.fxml.FXMLLoader;
import sagrada.controller.GameLobbyPlayerController;
import sagrada.database.DatabaseConnection;
import sagrada.model.Account;
import sagrada.model.Game;

public class GameLobbyPlayerScreen extends FXMLLoader {
    public GameLobbyPlayerScreen(DatabaseConnection databaseConnection, Game game, Account account) {
        this.setLocation(getClass().getResource("/views/lobby/gameLobbyPlayer.fxml"));
        this.setController(new GameLobbyPlayerController(databaseConnection, game, account));
    }
}
