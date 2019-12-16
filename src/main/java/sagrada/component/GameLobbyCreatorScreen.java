package sagrada.component;

import javafx.fxml.FXMLLoader;
import sagrada.controller.GameLobbyCreatorController;
import sagrada.database.DatabaseConnection;
import sagrada.model.Account;
import sagrada.model.Game;

public class GameLobbyCreatorScreen extends FXMLLoader {
    public GameLobbyCreatorScreen(DatabaseConnection databaseConnection, Game game, Account account) {
        this.setLocation(getClass().getResource("/views/screens/gameLobbyCreator.fxml"));
        this.setController(new GameLobbyCreatorController(databaseConnection, game, account));
    }
}
