package sagrada.component;

import javafx.fxml.FXMLLoader;
import sagrada.controller.GameController;
import sagrada.database.DatabaseConnection;
import sagrada.model.Account;
import sagrada.model.Game;

public class GameScreen extends FXMLLoader {
    public GameScreen(DatabaseConnection databaseConnection, Game game, Account account) {
        this.setLocation(getClass().getResource("/views/game.fxml"));
        this.setController(new GameController(databaseConnection, game, account));
    }
}
